package com.dormitory.controller;

import com.dormitory.model.CheckException;
import com.dormitory.service.CheckExceptionService;
import com.dormitory.service.ManagerScopeService;
import com.dormitory.service.OperationLogService;
import com.dormitory.utils.ApiResponses;
import com.dormitory.utils.AuthRoles;
import com.dormitory.utils.JwtUtils;
import com.dormitory.utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
                return ResponseEntity.ok(Map.of("data", List.of(), "total", 0, "page", Pagination.page(page), "size", Pagination.size(size)));
            }
            List<CheckException> scoped = checkExceptionService.searchScoped(
                    null, null,
                    managerScopeService.scopesJson(userId),
                    null, null);
            int safePage = Pagination.page(page);
            int safeSize = Pagination.size(size);
            return ResponseEntity.ok(Map.of(
                    "data", Pagination.slice(scoped, safePage, safeSize),
                    "total", scoped.size(),
                    "page", safePage,
                    "size", safeSize));
        }
        int safePage = Pagination.page(page);
        int safeSize = Pagination.size(size);
        List<CheckException> exceptions = checkExceptionService.findAll(safePage, safeSize);
        int total = checkExceptionService.count();
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", exceptions);
        result.put("total", total);
        result.put("page", safePage);
        result.put("size", safeSize);
        
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
        ResponseEntity<?> denied = denyIfOutOfScope(request, exception);
        if (denied != null) {
            return denied;
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
            Long userId = getUserId(request);
            if (!managerScopeService.hasScope(userId)) {
                return ResponseEntity.ok(Map.of("data", List.of()));
            }
            return ResponseEntity.ok(Map.of("data", checkExceptionService.searchScoped(
                    date, date, managerScopeService.scopesJson(userId), null, null)));
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
        if (isManager()) {
            Long userId = getUserId(request);
            exceptions = managerScopeService.filterVisible(userId, exceptions,
                    CheckException::getBuildingId, CheckException::getClassName);
        }
        return ResponseEntity.ok(Map.of("data", exceptions));
    }

    /**
     * 按处理状态查询
     */
    @GetMapping("/handled/{handled}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByHandled(@PathVariable Integer handled, HttpServletRequest request) {
        if (isManager()) {
            Long userId = getUserId(request);
            if (!managerScopeService.hasScope(userId)) {
                return ResponseEntity.ok(Map.of("data", List.of()));
            }
            return ResponseEntity.ok(Map.of("data", checkExceptionService.searchScoped(
                    null, null, managerScopeService.scopesJson(userId), null, handled)));
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
                    managerScopeService.scopesJson(userId),
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
                                   @RequestHeader("Authorization") String token,
                                   HttpServletRequest request) {
        try {
            CheckException exception = checkExceptionService.findById(id);
            ResponseEntity<?> denied = denyIfOutOfScope(request, exception);
            if (denied != null) {
                return denied;
            }
            String handleNote = (String) body.get("handleNote");
            String handleResult = (String) body.get("handleResult");
            Long handlerId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String handlerName = jwtUtils.getUsernameFromToken(token.replace("Bearer ", ""));
            
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
                return ResponseEntity.ok(Map.of(
                        "lateCount", 0, "absentCount", 0, "missingCount", 0,
                        "totalCount", 0, "unhandledCount", 0));
            }
            Map<String, Object> scoped = checkExceptionService.getScopedTrendStatistics(
                    date, date, managerScopeService.scopesJson(userId));
            @SuppressWarnings("unchecked")
            Map<String, Object> summary = (Map<String, Object>) scoped.getOrDefault("summary", Map.of());
            return ResponseEntity.ok(summary);
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
            Long userId = getUserId(request);
            if (!managerScopeService.hasScope(userId)) {
                return ResponseEntity.ok(Map.of("count", 0));
            }
            int count = checkExceptionService.searchScoped(
                    startDate, endDate, managerScopeService.scopesJson(userId), null, null).size();
            return ResponseEntity.ok(Map.of("count", count));
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
                        "summary", Map.of(
                                "lateCount", 0, "absentCount", 0,
                                "absentHandledCount", 0, "absentUnhandledCount", 0,
                                "missingCount", 0, "totalCount", 0, "unhandledCount", 0)
                )));
            }
            return ResponseEntity.ok(Map.of("code", 200, "data", checkExceptionService.getScopedTrendStatistics(
                    startDate, endDate,
                    managerScopeService.scopesJson(userId)
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
                    managerScopeService.scopesJson(userId),
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
        return AuthRoles.isManagerOnly(SecurityContextHolder.getContext().getAuthentication());
    }

    private ResponseEntity<?> denyIfOutOfScope(HttpServletRequest request, CheckException exception) {
        if (exception == null) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "success", false, "message", "异常记录不存在"));
        }
        if (!isManager()) {
            return null;
        }
        Long userId = getUserId(request);
        if (!managerScopeService.canSee(userId, exception.getBuildingId(), exception.getClassName())) {
            return ApiResponses.forbidden("无权处理该范围外的异常");
        }
        return null;
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