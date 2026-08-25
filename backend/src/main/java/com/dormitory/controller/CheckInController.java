package com.dormitory.controller;

import com.dormitory.model.CheckInRecord;
import com.dormitory.model.Student;
import com.dormitory.service.CheckInService;
import com.dormitory.service.ManagerScopeService;
import com.dormitory.service.OperationLogService;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ManagerScopeService managerScopeService;

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 学生打卡
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> checkIn(
            @RequestBody(required = false) Map<String, Object> body,
            HttpServletRequest request) {
        try {
            Map<String, Object> data = body == null ? Map.of() : body;
            Long studentId = getStudentIdFromRequest(request);
            
            Integer checkType = toInteger(data.get("checkType"));
            if (checkType == null) checkType = 0; // 默认定位打卡
            String ipAddress = getClientIp(request);
            
            CheckInRecord record = checkInService.checkIn(
                studentId,
                checkType,
                toBigDecimal(data.get("latitude")),
                toBigDecimal(data.get("longitude")),
                toBigDecimal(data.get("locationAccuracy")),
                (String) data.get("deviceInfo"),
                ipAddress
            );
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "success", true,
                "message", "打卡成功",
                "data", record
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 获取当前学生的打卡记录
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyRecords(HttpServletRequest request) {
        Long studentId = getStudentIdFromRequest(request);
        List<CheckInRecord> records = checkInService.findByStudentId(studentId);
        return ResponseEntity.ok(Map.of("code", 200, "data", records, "total", records.size()));
    }

    /**
     * 当前学生今日归寝状态
     */
    @GetMapping("/today")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getTodayStatus(HttpServletRequest request) {
        Long studentId = getStudentIdFromRequest(request);
        return ResponseEntity.ok(Map.of("code", 200, "data", checkInService.getTodayStatus(studentId)));
    }
    
    /**
     * 获取指定日期的打卡记录
     */
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByDate(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                       HttpServletRequest request) {
        if (isManager()) {
            return ResponseEntity.ok(scopedCheckInRecords(date, date, null, request));
        }
        List<CheckInRecord> records = checkInService.findByDate(date);
        return ResponseEntity.ok(records);
    }
    
    /**
     * 搜索打卡记录
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> search(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        if (isManager()) {
            return ResponseEntity.ok(scopedCheckInRecords(startDate, endDate, status, request));
        }
        List<CheckInRecord> records = checkInService.search(startDate, endDate, buildingId, status);
        return ResponseEntity.ok(records);
    }

    /**
     * 分页查询打卡记录，admin 全局，manager 自动限制范围。
     */
    @GetMapping("/records")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> records(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String studentNo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        if (isManager()) {
            Long userId = getUserIdFromRequest(request);
            if (!managerScopeService.hasScope(userId)) {
                return ResponseEntity.ok(Map.of("code", 200, "data", Map.of("records", List.of(), "total", 0, "page", page, "size", size)));
            }
            Map<String, Object> result = checkInService.searchScopedPaged(
                    startDate, endDate,
                    managerScopeService.buildingIdsCsv(userId),
                    managerScopeService.classNamesCsv(userId),
                    status, page, size);
            return ResponseEntity.ok(Map.of("code", 200, "data", result));
        }

        Map<String, Object> result = checkInService.searchPaged(
                startDate, endDate, buildingId, status, studentName, studentNo, page, size);
        return ResponseEntity.ok(Map.of("code", 200, "data", result));
    }
    
    /**
     * 获取打卡统计（单日或日期范围）
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        if (startDate != null || endDate != null) {
            Map<String, Object> data;
            if (isManager()) {
                Long userId = getUserIdFromRequest(request);
                if (!managerScopeService.hasScope(userId)) {
                    data = Map.of(
                            "summary", emptyCheckInSummary(),
                            "dailyTrend", List.of()
                    );
                } else {
                    data = checkInService.getTrendStatistics(
                            startDate, endDate,
                            managerScopeService.buildingIdsCsv(userId),
                            managerScopeService.classNamesCsv(userId));
                }
            } else {
                data = checkInService.getTrendStatistics(startDate, endDate, null, null);
            }
            return ResponseEntity.ok(Map.of("code", 200, "data", data));
        }

        if (date == null) {
            date = LocalDate.now();
        }
        if (isManager()) {
            Long userId = getUserIdFromRequest(request);
            if (!managerScopeService.hasScope(userId)) {
                return ResponseEntity.ok(Map.of("code", 200, "data", emptyCheckInSummary()));
            }
            Map<String, Object> trend = checkInService.getTrendStatistics(
                    date, date,
                    managerScopeService.buildingIdsCsv(userId),
                    managerScopeService.classNamesCsv(userId));
            return ResponseEntity.ok(Map.of("code", 200, "data", trend.get("summary")));
        }
        Map<String, Object> stats = checkInService.getStatistics(date);
        return ResponseEntity.ok(Map.of("code", 200, "data", stats));
    }

    /**
     * 归寝趋势统计（日期范围内汇总 + 每日趋势）
     */
    @GetMapping("/trend")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> trend(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        if (isManager()) {
            Long userId = getUserIdFromRequest(request);
            if (!managerScopeService.hasScope(userId)) {
                return ResponseEntity.ok(Map.of("code", 200, "data", Map.of(
                        "summary", emptyCheckInSummary(),
                        "dailyTrend", List.of()
                )));
            }
            return ResponseEntity.ok(Map.of("code", 200, "data", checkInService.getTrendStatistics(
                    startDate, endDate,
                    managerScopeService.buildingIdsCsv(userId),
                    managerScopeService.classNamesCsv(userId)
            )));
        }
        return ResponseEntity.ok(Map.of("code", 200, "data", checkInService.getTrendStatistics(
                startDate, endDate, null, null)));
    }
    
    /**
     * 手动生成未归异常（管理员操作）
     */
    @PostMapping("/generate-exceptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateExceptions(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            int count = checkInService.generateMissingCheckIns(date);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "success", true,
                "message", "生成异常记录成功",
                "count", count
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 导出归寝记录 CSV。
     */
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Map<String, Object> result;
        if (isManager()) {
            Long userId = getUserIdFromRequest(request);
            if (!managerScopeService.hasScope(userId)) {
                result = Map.of("records", List.of(), "total", 0);
            } else {
            result = checkInService.searchScopedPaged(startDate, endDate,
                    managerScopeService.buildingIdsCsv(userId),
                    managerScopeService.classNamesCsv(userId),
                    status, 1, 10000);
            }
        } else {
            result = checkInService.searchPaged(startDate, endDate, buildingId, status, null, null, 1, 10000);
        }

        @SuppressWarnings("unchecked")
        List<CheckInRecord> records = (List<CheckInRecord>) result.get("records");
        StringBuilder csv = new StringBuilder("\uFEFF学生姓名,学号,楼栋,房间,日期,打卡时间,状态,纬度,经度,精度\n");
        for (CheckInRecord record : records) {
            csv.append(nullToEmpty(record.getStudentName())).append(',')
                    .append(nullToEmpty(record.getStudentNo())).append(',')
                    .append(nullToEmpty(record.getBuildingName())).append(',')
                    .append(nullToEmpty(record.getRoomNumber())).append(',')
                    .append(record.getCheckDate()).append(',')
                    .append(record.getCheckTime()).append(',')
                    .append(statusText(record.getStatus())).append(',')
                    .append(nullToEmpty(record.getLatitude())).append(',')
                    .append(nullToEmpty(record.getLongitude())).append(',')
                    .append(nullToEmpty(record.getLocationAccuracy())).append('\n');
        }
        operationLogService.log(null, isManager() ? "manager" : "admin",
                jwtUtils.getUsernameFromToken(getToken(request)), "checkin.export", Map.of("count", records.size()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=checkin-records.csv")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
    
    private Long getStudentIdFromRequest(HttpServletRequest request) {
        // 通过用户名（学号）查找学生
        String username = jwtUtils.getUsernameFromToken(getToken(request));
        Student student = studentMapper.findByStudentNo(username);
        if (student == null) {
            throw new RuntimeException("当前账号未关联学生信息，请使用学生账号登录");
        }
        return student.getId();
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        return jwtUtils.getUserIdFromToken(getToken(request));
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    private boolean isManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_MANAGER".equals(authority.getAuthority()));
    }

    @SuppressWarnings("unchecked")
    private List<CheckInRecord> scopedCheckInRecords(LocalDate startDate, LocalDate endDate,
                                                     Integer status, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        if (!managerScopeService.hasScope(userId)) {
            return List.of();
        }
        Map<String, Object> result = checkInService.searchScopedPaged(
                startDate, endDate,
                managerScopeService.buildingIdsCsv(userId),
                managerScopeService.classNamesCsv(userId),
                status, 1, 10000);
        Object records = result.get("records");
        return records instanceof List<?> list ? (List<CheckInRecord>) list : List.of();
    }

    private Map<String, Object> emptyCheckInSummary() {
        return Map.of("normalCount", 0, "lateCount", 0, "absentCount", 0, "leaveCount", 0, "totalCount", 0);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal decimal) return decimal;
        if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
        return new BigDecimal(value.toString());
    }

    private Integer toInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.intValue();
        return Integer.parseInt(value.toString());
    }

    private String nullToEmpty(Object value) {
        return value == null ? "" : String.valueOf(value).replace(",", "，");
    }

    private String statusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "已归";
            case 1 -> "晚归";
            case 2 -> "未归";
            case 3 -> "请假";
            default -> "未知";
        };
    }

    /**
     * 获取单条打卡记录（放在最后，避免与 /trend、/statistics 等路径冲突）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getById(@PathVariable Long id, HttpServletRequest request) {
        CheckInRecord record = checkInService.findById(id);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        if (isManager()) {
            managerScopeService.assertStudentInScope("MANAGER", getUserIdFromRequest(request), record.getStudentId());
        }
        return ResponseEntity.ok(record);
    }
}