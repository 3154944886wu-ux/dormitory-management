package com.dormitory.controller;

import com.dormitory.model.ManagerScope;
import com.dormitory.service.ManagerScopeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/manager-scopes")
@PreAuthorize("hasRole('ADMIN')")
public class ManagerScopeController {

    private final ManagerScopeService managerScopeService;

    public ManagerScopeController(ManagerScopeService managerScopeService) {
        this.managerScopeService = managerScopeService;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(Map.of("code", 200, "data", managerScopeService.findAllActive()));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ManagerScope scope) {
        return ResponseEntity.ok(Map.of("code", 200, "message", "保存成功", "data", managerScopeService.save(scope)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ManagerScope scope) {
        scope.setId(id);
        return ResponseEntity.ok(Map.of("code", 200, "message", "保存成功", "data", managerScopeService.save(scope)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> disable(@PathVariable Long id) {
        managerScopeService.disable(id);
        return ResponseEntity.ok(Map.of("code", 200, "message", "已停用"));
    }
}
