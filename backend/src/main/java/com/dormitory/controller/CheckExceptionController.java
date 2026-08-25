package com.dormitory.controller;

import com.dormitory.model.CheckException;
import com.dormitory.service.CheckExceptionService;
import com.dormitory.service.ManagerScopeService;
import com.dormitory.service.OperationLogService;
import com.dormitory.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 归寝异常记录控制器
 */
@RestController
@RequestMapping("/api/check-exceptions")
@CrossOrigin
public class CheckExceptionController {

    @Autowired
    private CheckExceptionService checkExceptionService;
    
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ManagerScopeService managerScopeService;

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 分页查询所有异常记录
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   HttpServletRequest request) {
        if (isManager()) {
            Long userId = getUserId(request);
            if (!managerScopeService.hasScope(userId)) {
                return ResponseEntity.ok(Map.of("data", List.of(), "total", 0, "page", page, "size", size));
            }
            List<CheckException> scoped = checkExceptionService.searchScoped(
                    null, null,
                    managerScopeService.buildingIdsCsv(userId),
                    managerScopeService.classNamesCsv(userId),
                    null, null);
            return ResponseEntity.ok(Map.of("data", scoped, "total", scoped.size(), "page", page, "size", size));
        }
        List<CheckException> exceptions = checkExceptionService.findAll(page, size);
        int total = checkExceptionService.count();
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", exceptions);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取异常记录详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getById(@PathVariable Long id, HttpServletRequest request) {
        CheckException exception = checkExceptionService.findById(id);
        if (exception == null) {
            return ResponseEntity.notFound().build();
        }
        if (isManager()) {
            managerScopeService.assertStudentInScope("MANAGER", getUserId(request), exception.getStudentId());
        }
        return ResponseEntity.ok(Map.of("data", exception));
    }

    /**
     * 按日期查询
     */
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                       HttpServletRequest request) {
        if (isManager()) {
            return ResponseEntity.ok(Map.of("data", scopedExceptions(date, date, null, null, request)));
        }
        List<CheckException> exceptions = checkExceptionService.findByDate(date);
        return ResponseEntity.ok(Map.of("data", exceptions));
    }

    /**
     * 按学生查询
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByStudent(@PathVariable Long studentId, HttpServletRequest request) {
        if (isManager()) {
            managerScopeService.assertStudentInScope("MANAGER", getUserId(request), studentId);
        }
        List<CheckException> exceptions = checkExceptionService.findByStudentId(studentId);
        return ResponseEntity.ok(Map.of("data", exceptions));
    }

    /**
     * 按处理状态查询
     */
    @GetMapping("/handled/{handled}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByHandled(@PathVariable Integer handled, HttpServletRequest request) {
        if (isManager()) {
            return ResponseEntity.ok(Map.of("data", scopedExceptions(null, null, null, handled, request)));
        }
        List<CheckException> exceptions = checkExceptionService.findByHandled(handled);
        return ResponseEntity.ok(Map.of("data", exceptions));
    }

    /**
     * 多条件搜索
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Integer exceptionType,
            @RequestParam(required = false) Integer handled,
            HttpServletRequest request) {
        if (isManager()) {
            Long userId = getUserId(request);
            if (!managerScopeService.hasScope(userId)) {
                return ResponseEntity.ok(Map.of("data", List.of()));
            }
            List<CheckException> exceptions = checkExceptionService.searchScoped(
                    startDate, endDate,
                    managerScopeService.buildingIdsCsv(userId),
                    managerScopeService.classNamesCsv(userId),
                    exceptionType, handled);
            return ResponseEntity.ok(Map.of("data", exceptions));
        }
        
        List<CheckException> exceptions = checkExceptionService.search(
            startDate, endDate, buildingId, exceptionType, handled
        );
        return ResponseEntity.ok(Map.of("data", exceptions));
    }

    /**
     * 处理异常记录
     */
    @PostMapping("/{id}/handle")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> handle(@PathVariable Long id,
                                   @RequestBody Map<String, Object> body,
                                   @RequestHeader("Authorization") String token) {
        try {
            String handleNote = (String) body.get("handleNote");
            String handleResult = (String) body.get("handleResult");
            Long handlerId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String handlerName = jwtUtils.getUsernameFromToken(token.replace("Bearer ", ""));

            if (isManager()) {
                CheckException exception = checkExceptionService.findById(id);
                if (exception == null) {
                    throw new RuntimeException("异常记录不存在");
                }
                managerScopeService.assertStudentInScope("MANAGER", handlerId, exception.getStudentId());
            }

            checkExceptionService.handle(id, handlerId, handlerName, handleResult, handleNote);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "success", true,
                "message", "异常记录已处理"
            ));
        } catch (AccessDeniedException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 获取异常统计
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletRequest request) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (isManager()) {
            Long userId = getUserId(request);
            if (!managerScopeService.hasScope(userId)) {
                return ResponseEntity.ok(emptyExceptionStats());
            }
            Map<String, Object> trend = checkExceptionService.getScopedTrendStatistics(
                    date, date,
                    managerScopeService.buildingIdsCsv(userId),
                    managerScopeService.classNamesCsv(userId));
            @SuppressWarnings("unchecked")
            Map<String, Object> summary = (Map<String, Object>) trend.get("summary");
            return ResponseEntity.ok(toExceptionStats(summary));
        }
        Map<String, Object> stats = checkExceptionService.getStatistics(date);
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取日期范围内的异常数量
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> countBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {
        if (isManager()) {
            return ResponseEntity.ok(Map.of("count", scopedExceptions(startDate, endDate, null, null, request).size()));
        }
        int count = checkExceptionService.countBetweenDates(startDate, endDate);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/trend")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> trend(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {
        if (isManager()) {
            Long userId = getUserId(request);
            if (!managerScopeService.hasScope(userId)) {
                return ResponseEntity.ok(Map.of("code", 200, "data", Map.of(
                        "byBuilding", List.of(), "byClass", List.of(),
                        "summary", emptyExceptionStats()
                )));
            }
            return ResponseEntity.ok(Map.of("code", 200, "data", checkExceptionService.getScopedTrendStatistics(
                    startDate, endDate,
                    managerScopeService.buildingIdsCsv(userId),
                    managerScopeService.classNamesCsv(userId)
            )));
        }
        return ResponseEntity.ok(Map.of("code", 200, "data", checkExceptionService.getTrendStatistics(startDate, endDate)));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Integer exceptionType,
            @RequestParam(required = false) Integer handled,
            HttpServletRequest request) {
        List<CheckException> exceptions;
        if (isManager()) {
            Long userId = getUserId(request);
            if (!managerScopeService.hasScope(userId)) {
                exceptions = List.of();
            } else {
            exceptions = checkExceptionService.searchScoped(startDate, endDate,
                    managerScopeService.buildingIdsCsv(userId),
                    managerScopeService.classNamesCsv(userId),
                    exceptionType, handled);
            }
        } else {
            exceptions = checkExceptionService.search(startDate, endDate, buildingId, exceptionType, handled);
        }

        StringBuilder csv = new StringBuilder("\uFEFF学生姓名,学号,院系,班级,楼栋,房间,异常日期,异常类型,处理状态,处理结果,处理备注\n");
        for (CheckException exception : exceptions) {
            csv.append(nullToEmpty(exception.getStudentName())).append(',')
                    .append(nullToEmpty(exception.getStudentNo())).append(',')
                    .append(nullToEmpty(exception.getDepartment())).append(',')
                    .append(nullToEmpty(exception.getClassName())).append(',')
                    .append(nullToEmpty(exception.getBuildingName())).append(',')
                    .append(nullToEmpty(exception.getRoomNumber())).append(',')
                    .append(exception.getExceptionDate()).append(',')
                    .append(exceptionTypeText(exception.getExceptionType())).append(',')
                    .append(exception.getHandled() != null && exception.getHandled() == 1 ? "已处理" : "未处理").append(',')
                    .append(nullToEmpty(exception.getHandleResult())).append(',')
                    .append(nullToEmpty(exception.getHandleNote())).append('\n');
        }
        operationLogService.log(null, isManager() ? "manager" : "admin",
                jwtUtils.getUsernameFromToken(getToken(request)), "check_exception.export", Map.of("count", exceptions.size()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=check-exceptions.csv")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private Long getUserId(HttpServletRequest request) {
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

    private List<CheckException> scopedExceptions(LocalDate startDate, LocalDate endDate,
                                                  Integer exceptionType, Integer handled,
                                                  HttpServletRequest request) {
        Long userId = getUserId(request);
        if (!managerScopeService.hasScope(userId)) {
            return List.of();
        }
        return checkExceptionService.searchScoped(
                startDate, endDate,
                managerScopeService.buildingIdsCsv(userId),
                managerScopeService.classNamesCsv(userId),
                exceptionType, handled);
    }

    private Map<String, Object> emptyExceptionStats() {
        return Map.of(
                "lateCount", 0, "absentCount", 0,
                "absentHandledCount", 0, "absentUnhandledCount", 0,
                "missingCount", 0, "totalCount", 0, "unhandledCount", 0);
    }

    private Map<String, Object> toExceptionStats(Map<String, Object> summary) {
        if (summary == null) {
            return emptyExceptionStats();
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("lateCount", summary.getOrDefault("lateCount", 0));
        stats.put("absentCount", summary.getOrDefault("absentCount", 0));
        stats.put("missingCount", summary.getOrDefault("missingCount", 0));
        stats.put("totalCount", summary.getOrDefault("totalCount", 0));
        stats.put("unhandledCount", summary.getOrDefault("unhandledCount", 0));
        return stats;
    }

    private String exceptionTypeText(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 1 -> "晚归";
            case 2 -> "未归";
            case 3 -> "缺卡";
            default -> "未知";
        };
    }

    private String nullToEmpty(Object value) {
        return value == null ? "" : String.valueOf(value).replace(",", "，");
    }
}