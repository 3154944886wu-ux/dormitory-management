package com.dormitory.controller;

import com.dormitory.model.Building;
import com.dormitory.service.BuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {
    
    private final BuildingService buildingService;
    
    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> list() {
        List<Building> buildings = buildingService.findAll();
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", buildings);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Building building = buildingService.findById(id);
        
        Map<String, Object> result = new HashMap<>();
        if (building == null) {
            result.put("code", 404);
            result.put("message", "楼栋不存在");
            return ResponseEntity.status(404).body(result);
        }
        
        result.put("code", 200);
        result.put("data", building);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Building building) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long id = buildingService.create(building);
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
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Building building) {
        Map<String, Object> result = new HashMap<>();
        
        building.setId(id);
        try {
            buildingService.update(building);
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
            buildingService.delete(id);
            result.put("code", 200);
            result.put("message", "删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Integer status = body.get("status");
            buildingService.updateStatus(id, status);
            result.put("code", 200);
            result.put("message", "状态更新成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}