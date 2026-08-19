package com.dormitory.controller;

import com.dormitory.model.Repair;
import com.dormitory.model.Student;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.service.RepairService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repairs")
public class RepairController {

    private final RepairService repairService;
    private final StudentMapper studentMapper;

    public RepairController(RepairService repairService, StudentMapper studentMapper) {
        this.repairService = repairService;
        this.studentMapper = studentMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String roomNumber,
            Authentication auth) {

        List<Repair> repairs;
        if (isStudent(auth)) {
            Student student = requireStudent(auth);
            repairs = repairService.findByStudentId(student.getId());
            if (status != null) {
                repairs = repairs.stream().filter(r -> status.equals(r.getStatus())).toList();
            }
        } else if (studentId != null) {
            repairs = repairService.findByStudentId(studentId);
        } else if (roomId != null) {
            repairs = repairService.findByRoomId(roomId);
        } else if (roomNumber != null && !roomNumber.isBlank()) {
            repairs = repairService.findByRoomNumber(roomNumber.trim());
        } else if (status != null) {
            repairs = repairService.findByStatus(status);
        } else {
            repairs = repairService.findAll();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", repairs);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id, Authentication auth) {
        Repair repair = repairService.findById(id);

        Map<String, Object> result = new HashMap<>();
        if (repair == null) {
            result.put("code", 404);
            result.put("message", "报修记录不存在");
            return ResponseEntity.status(404).body(result);
        }
        if (isStudent(auth) && !requireStudent(auth).getId().equals(repair.getStudentId())) {
            result.put("code", 403);
            result.put("message", "无权查看该报修");
            return ResponseEntity.status(403).body(result);
        }

        result.put("code", 200);
        result.put("data", repair);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", Map.of(
            "pending", repairService.getPendingCount(),
            "processing", repairService.getProcessingCount()
        ));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Repair repair, Authentication auth) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (isStudent(auth)) {
                Student student = requireStudent(auth);
                if (student.getRoomId() == null) {
                    throw new RuntimeException("未分配宿舍，无法报修");
                }
                repair.setStudentId(student.getId());
                repair.setRoomId(student.getRoomId());
            }

            Long id = repairService.create(repair);
            result.put("code", 201);
            result.put("message", "报修提交成功");
            result.put("data", Map.of("id", id));
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/{id}/handle")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> handle(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();

        try {
            String handler = body.get("handler");
            String note = body.get("note");
            repairService.handle(id, handler, note);
            result.put("code", 200);
            result.put("message", "已开始处理");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> complete(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();

        try {
            String note = body != null ? body.get("note") : null;
            repairService.complete(id, note);
            result.put("code", 200);
            result.put("message", "报修已完成");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> close(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();

        try {
            String note = body != null ? body.get("note") : null;
            repairService.close(id, note);
            result.put("code", 200);
            result.put("message", "报修已关闭");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Repair repair) {
        Map<String, Object> result = new HashMap<>();
        try {
            repair.setId(id);
            repairService.update(repair);
            result.put("code", 200);
            result.put("message", "更新成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        try {
            repairService.delete(id);
            result.put("code", 200);
            result.put("message", "删除成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    private boolean isStudent(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_STUDENT".equals(a.getAuthority()));
    }

    private Student requireStudent(Authentication auth) {
        Student student = studentMapper.findByStudentNo(auth.getName());
        if (student == null) {
            throw new RuntimeException("当前账号未关联学生信息");
        }
        return student;
    }
}
