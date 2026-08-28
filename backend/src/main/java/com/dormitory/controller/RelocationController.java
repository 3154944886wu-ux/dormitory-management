package com.dormitory.controller;

import com.dormitory.model.RelocationApplication;
import com.dormitory.model.Student;
import com.dormitory.model.User;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.service.RelocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relocation")
public class RelocationController {

    private final RelocationService relocationService;
    private final UserMapper userMapper;
    private final StudentMapper studentMapper;

    public RelocationController(RelocationService relocationService,
                                UserMapper userMapper,
                                StudentMapper studentMapper) {
        this.relocationService = relocationService;
        this.userMapper = userMapper;
        this.studentMapper = studentMapper;
    }

    /** 学生提交调换申请 */
    @PostMapping("/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> apply(Authentication auth,
                                                     @RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userMapper.findByUsername(auth.getName());
            if (user == null) {
                result.put("code", 401);
                result.put("message", "未登录");
                return ResponseEntity.status(401).body(result);
            }
            Student student = studentMapper.findByUserId(user.getId());
            if (student == null) {
                result.put("code", 400);
                result.put("message", "学生信息不存在");
                return ResponseEntity.badRequest().body(result);
            }

            String reason = (String) body.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                result.put("code", 400);
                result.put("message", "申请理由不能为空");
                return ResponseEntity.badRequest().body(result);
            }

            Long preferredBuildingId = null;
            if (body.get("preferredBuildingId") != null) {
                preferredBuildingId = ((Number) body.get("preferredBuildingId")).longValue();
            }

            RelocationApplication app = relocationService.apply(student.getId(), reason, preferredBuildingId);
            result.put("code", 200);
            result.put("message", "申请已提交");
            result.put("data", app);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /** 学生查看自己的申请 */
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> myApplications(Authentication auth) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userMapper.findByUsername(auth.getName());
            if (user == null) {
                result.put("code", 401);
                result.put("message", "未登录");
                return ResponseEntity.status(401).body(result);
            }
            Student student = studentMapper.findByUserId(user.getId());
            if (student == null) {
                result.put("code", 400);
                result.put("message", "学生信息不存在");
                return ResponseEntity.badRequest().body(result);
            }
            List<RelocationApplication> apps = relocationService.findByStudentId(student.getId());
            result.put("code", 200);
            result.put("data", apps);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /** 管理员查看所有申请 */
    @GetMapping("/applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> list(@RequestParam(required = false) String status) {
        Map<String, Object> result = new HashMap<>();
        List<RelocationApplication> apps;
        if (status != null && !status.isEmpty()) {
            apps = relocationService.findByStatus(status);
        } else {
            apps = relocationService.findAll();
        }
        result.put("code", 200);
        result.put("data", apps);
        return ResponseEntity.ok(result);
    }

    /** 管理员查看单个申请 */
    @GetMapping("/applications/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        RelocationApplication app = relocationService.findById(id);
        if (app == null) {
            result.put("code", 404);
            result.put("message", "申请不存在");
            return ResponseEntity.status(404).body(result);
        }
        result.put("code", 200);
        result.put("data", app);
        return ResponseEntity.ok(result);
    }

    /** 管理员审批通过 */
    @PostMapping("/applications/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> approve(Authentication auth,
                                                       @PathVariable Long id,
                                                       @RequestBody(required = false) Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            User admin = userMapper.findByUsername(auth.getName());
            if (admin == null) {
                result.put("code", 401);
                result.put("message", "未登录");
                return ResponseEntity.status(401).body(result);
            }
            String comment = body != null ? body.getOrDefault("comment", "") : "";
            RelocationApplication app = relocationService.approve(id, admin.getId(), comment);
            result.put("code", 200);
            result.put("message", "已审批通过");
            result.put("data", app);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /** 管理员拒绝 */
    @PostMapping("/applications/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> reject(Authentication auth,
                                                      @PathVariable Long id,
                                                      @RequestBody(required = false) Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            User admin = userMapper.findByUsername(auth.getName());
            if (admin == null) {
                result.put("code", 401);
                result.put("message", "未登录");
                return ResponseEntity.status(401).body(result);
            }
            String comment = body != null ? body.getOrDefault("comment", "") : "";
            RelocationApplication app = relocationService.reject(id, admin.getId(), comment);
            result.put("code", 200);
            result.put("message", "已拒绝");
            result.put("data", app);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /** 管理员执行调换 */
    @PostMapping("/applications/{id}/execute")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> execute(Authentication auth,
                                                       @PathVariable Long id,
                                                       @RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            User admin = userMapper.findByUsername(auth.getName());
            if (admin == null) {
                result.put("code", 401);
                result.put("message", "未登录");
                return ResponseEntity.status(401).body(result);
            }
            Long newRoomId = ((Number) body.get("roomId")).longValue();
            Long newBedId = ((Number) body.get("bedId")).longValue();
            RelocationApplication app = relocationService.execute(id, admin.getId(), newRoomId, newBedId);
            result.put("code", 200);
            result.put("message", "调换执行成功");
            result.put("data", app);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
