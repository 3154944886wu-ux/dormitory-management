package com.dormitory.controller;

import com.dormitory.model.LeaveRequest;
import com.dormitory.model.Student;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.service.LeaveRequestService;
import com.dormitory.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请假申请管理控制器
 */
@RestController
@RequestMapping("/api/leave-requests")
@CrossOrigin
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;
    
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 提交请假申请
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> submit(@RequestBody LeaveRequest request,
                                    @RequestHeader("Authorization") String token) {
        try {
            request.setStudentId(getStudentId(token));
            LeaveRequest submitted = leaveRequestService.submit(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "请假申请提交成功",
                "data", submitted
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 审批请假申请
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> approve(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    @RequestHeader("Authorization") String token) {
        try {
            Integer status = parseStatus(body.get("status"));
            if (status == null) {
                status = 1; // 批准接口默认通过
            }
            String note = parseNote(body);
            
            // 从token获取审批人信息
            Long approverId = jwtUtils.getUserIdFromToken(stripBearer(token));
            String username = jwtUtils.getUsernameFromToken(stripBearer(token));
            
            LeaveRequest approved = leaveRequestService.approve(id, status, approverId, username, note);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", status == 1 ? "请假申请已批准" : "请假申请已拒绝",
                "data", approved
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 撤销请假申请
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> cancel(@PathVariable Long id,
                                    @RequestHeader("Authorization") String token) {
        try {
            Long studentId = getStudentId(token);
            leaveRequestService.cancel(id, studentId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "请假申请已撤销"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 销假（确认返回）
     */
    @PostMapping("/{id}/confirm-return")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> confirmReturn(@PathVariable Long id,
                                          @RequestHeader("Authorization") String token) {
        try {
            Long studentId = getStudentId(token);
            leaveRequestService.confirmReturn(id, studentId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "销假成功"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 获取请假申请详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<?> getById(@PathVariable Long id, Authentication auth) {
        LeaveRequest request = leaveRequestService.findById(id);
        if (request == null) {
            return ResponseEntity.notFound().build();
        }
        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_STUDENT".equals(a.getAuthority()))) {
            Long studentId = getStudentIdFromAuth(auth);
            if (!studentId.equals(request.getStudentId())) {
                return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "无权查看该请假申请"
                ));
            }
        }
        return ResponseEntity.ok(Map.of("data", request));
    }

    /**
     * 获取当前学生的请假记录
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyRequests(@RequestHeader("Authorization") String token) {
        Long studentId = getStudentId(token);
        List<LeaveRequest> requests = leaveRequestService.findByStudentId(studentId);
        return ResponseEntity.ok(Map.of("data", requests));
    }

    /**
     * 按状态查询
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByStatus(@PathVariable Integer status, Authentication auth,
                                         @RequestHeader("Authorization") String token) {
        List<LeaveRequest> requests = leaveRequestService.findByStatus(status);
        Long managerId = managerScopeUserId(auth, token);
        if (managerId != null) {
            requests = leaveRequestService.filterByManagerScope(requests, managerId);
        }
        return ResponseEntity.ok(Map.of("data", requests));
    }

    /**
     * 获取待审批列表
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getPending(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        Authentication auth,
                                        @RequestHeader("Authorization") String token) {
        List<LeaveRequest> requests = leaveRequestService.findByStatus(0); // 0=待审批
        Long managerId = managerScopeUserId(auth, token);
        if (managerId != null) {
            requests = leaveRequestService.filterByManagerScope(requests, managerId);
        }
        int total = requests.size();
        
        // 简单分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<LeaveRequest> pagedRequests = start < total ? 
            requests.subList(start, end) : List.of();
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pagedRequests);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 拒绝请假申请
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> reject(@PathVariable Long id,
                                   @RequestBody Map<String, Object> body,
                                   @RequestHeader("Authorization") String token) {
        try {
            String reason = (String) body.get("reason");
            
            // 从token获取审批人信息
            Long approverId = jwtUtils.getUserIdFromToken(stripBearer(token));
            String username = jwtUtils.getUsernameFromToken(stripBearer(token));
            
            LeaveRequest rejected = leaveRequestService.approve(id, 2, approverId, username, reason);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "请假申请已拒绝",
                "data", rejected
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 分页查询所有请假申请
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Authentication auth,
                                    @RequestHeader("Authorization") String token) {
        List<LeaveRequest> requests;
        int total;
        Long managerId = managerScopeUserId(auth, token);
        if (managerId != null) {
            // manager 仅能查看其管理范围内的请假：先按范围过滤再内存分页
            List<LeaveRequest> all = leaveRequestService.filterByManagerScope(
                    leaveRequestService.findAllList(), managerId);
            total = all.size();
            int start = (page - 1) * size;
            int end = Math.min(start + size, total);
            requests = start < total ? all.subList(start, end) : List.of();
        } else {
            requests = leaveRequestService.findAll(page, size);
            total = leaveRequestService.count();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", requests);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取请假统计（待审批数量等）
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getStatistics() {
        return ResponseEntity.ok(leaveRequestService.getStatistics());
    }

    private Long getStudentId(String token) {
        String username = jwtUtils.getUsernameFromToken(stripBearer(token));
        Student student = studentMapper.findByStudentNo(username);
        if (student == null) {
            throw new RuntimeException("当前账号未关联学生信息");
        }
        return student.getId();
    }

    private Long getStudentIdFromAuth(Authentication auth) {
        Student student = studentMapper.findByStudentNo(auth.getName());
        if (student == null) {
            throw new RuntimeException("当前账号未关联学生信息");
        }
        return student.getId();
    }

    /**
     * 若当前调用者是 manager（且非 admin），返回其 userId 以便按范围过滤；admin 或其它返回 null（不过滤）。
     */
    private Long managerScopeUserId(Authentication auth, String token) {
        if (auth == null) {
            return null;
        }
        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean manager = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_MANAGER".equals(a.getAuthority()));
        if (manager && !admin) {
            return jwtUtils.getUserIdFromToken(stripBearer(token));
        }
        return null;
    }

    private String stripBearer(String token) {
        if (token == null) {
            return "";
        }
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }

    private Integer parseStatus(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(value.toString());
    }

    private String parseNote(Map<String, Object> body) {
        Object note = body.get("note");
        if (note == null) {
            note = body.get("approverNote");
        }
        return note == null ? null : note.toString();
    }
}