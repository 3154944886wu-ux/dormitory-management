package com.dormitory.controller;

import com.dormitory.dto.CreateTeacherRequest;
import com.dormitory.model.ManagerScope;
import com.dormitory.model.User;
import com.dormitory.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teachers")
@PreAuthorize("hasRole('ADMIN')")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(Map.of("code", 200, "data", teacherService.listTeachers()));
    }

    @GetMapping("/class-names")
    public ResponseEntity<?> classNames() {
        return ResponseEntity.ok(Map.of("code", 200, "data", teacherService.listClassNames()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(Map.of("code", 200, "data", teacherService.findById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateTeacherRequest request) {
        try {
            return ResponseEntity.ok(Map.of(
                    "code", 201,
                    "message", "教师创建成功",
                    "data", teacherService.createTeacher(request)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User user) {
        try {
            return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "更新成功",
                    "data", teacherService.updateTeacher(id, user)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            teacherService.deleteTeacher(id);
            return ResponseEntity.ok(Map.of("code", 200, "message", "教师已删除"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/scopes")
    public ResponseEntity<?> addScope(@PathVariable Long userId, @RequestBody ManagerScope scope) {
        try {
            return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "绑定成功",
                    "data", teacherService.addScope(userId, scope)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        }
    }

    @PutMapping("/scopes/{scopeId}")
    public ResponseEntity<?> updateScope(@PathVariable Long scopeId, @RequestBody ManagerScope scope) {
        try {
            return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "换绑成功",
                    "data", teacherService.updateScope(scopeId, scope)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/scopes/{scopeId}")
    public ResponseEntity<?> removeScope(@PathVariable Long scopeId) {
        try {
            teacherService.removeScope(scopeId);
            return ResponseEntity.ok(Map.of("code", 200, "message", "已解除绑定"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        }
    }
}
