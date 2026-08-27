package com.dormitory.controller;

import com.dormitory.model.Student;
import com.dormitory.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    private final StudentService studentService;
    
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long roomId,
            @RequestParam(name = "pageNum", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "20") int size) {

        int offset = (page - 1) * size;
        List<Student> students;
        long total;

        if (name != null && !name.isEmpty()) {
            students = studentService.searchByName(name, offset, size);
            total = studentService.countByName(name);
        } else if (roomId != null) {
            students = studentService.findByRoomIdWithPagination(roomId, offset, size);
            total = studentService.countByRoomId(roomId);
        } else {
            students = studentService.findAllWithPagination(offset, size);
            total = studentService.countAll();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", students);
        data.put("total", total);
        data.put("totalPages", (int) Math.ceil((double) total / size));
        data.put("currentPage", page);
        data.put("pageSize", size);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Student student = studentService.findById(id);
        
        Map<String, Object> result = new HashMap<>();
        if (student == null) {
            result.put("code", 404);
            result.put("message", "学生不存在");
            return ResponseEntity.status(404).body(result);
        }
        
        result.put("code", 200);
        result.put("data", student);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/no/{studentNo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> getByStudentNo(
            @PathVariable String studentNo,
            Authentication auth) {
        Map<String, Object> result = new HashMap<>();
        if (isStudent(auth) && (studentNo == null || !studentNo.equals(auth.getName()))) {
            result.put("code", 403);
            result.put("message", "只能查询本人信息");
            return ResponseEntity.status(403).body(result);
        }

        Student student = studentService.findByStudentNoWithRoommates(studentNo);

        if (student == null) {
            result.put("code", 404);
            result.put("message", "学生不存在");
            return ResponseEntity.status(404).body(result);
        }

        result.put("code", 200);
        result.put("data", student);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Student student) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long id = studentService.create(student);
            result.put("code", 201);
            result.put("message", "创建成功");
            result.put("data", Map.of("id", id));
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, 
                                                      @RequestBody Student student) {
        Map<String, Object> result = new HashMap<>();
        
        student.setId(id);
        try {
            studentService.update(student);
            result.put("code", 200);
            result.put("message", "更新成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @PostMapping("/{id}/relocate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> relocate(@PathVariable Long id,
                                                         @RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long newRoomId = ((Number) body.get("roomId")).longValue();
            Long newBedId = ((Number) body.get("bedId")).longValue();
            studentService.relocate(id, newRoomId, newBedId);
            result.put("code", 200);
            result.put("message", "调宿成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/{id}/checkout")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> checkOut(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            studentService.checkOut(id);
            result.put("code", 200);
            result.put("message", "退宿成功");
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
            studentService.delete(id);
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
}