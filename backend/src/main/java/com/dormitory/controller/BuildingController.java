package com.dormitory.controller;

import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.Building;
import com.dormitory.model.Student;
import com.dormitory.service.BuildingService;
import com.dormitory.utils.AuthRoles;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {
    
    private final BuildingService buildingService;
    private final StudentMapper studentMapper;
    
    public BuildingController(BuildingService buildingService, StudentMapper studentMapper) {
        this.buildingService = buildingService;
        this.studentMapper = studentMapper;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> list(Authentication auth) {
        List<Building> buildings = buildingService.findAll();
        if (AuthRoles.has(auth, "STUDENT") && !AuthRoles.has(auth, "ADMIN")) {
            Student student = studentMapper.findByStudentNo(auth.getName());
            Long buildingId = student == null ? null : student.getBuildingId();
            buildings = buildings.stream()
                    .filter(b -> buildingId != null && buildingId.equals(b.getId()))
                    .toList();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", buildings);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id, Authentication auth) {
        Building building = buildingService.findById(id);
        
        Map<String, Object> result = new HashMap<>();
        if (building == null) {
            result.put("code", 404);
            result.put("message", "楼栋不存在");
            return ResponseEntity.status(404).body(result);
        }
        if (AuthRoles.has(auth, "STUDENT") && !AuthRoles.has(auth, "ADMIN")) {
            Student student = studentMapper.findByStudentNo(auth.getName());
            if (student == null || student.getBuildingId() == null || !student.getBuildingId().equals(id)) {
                result.put("code", 403);
                result.put("message", "无权查看该楼栋");
                return ResponseEntity.status(403).body(result);
            }
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