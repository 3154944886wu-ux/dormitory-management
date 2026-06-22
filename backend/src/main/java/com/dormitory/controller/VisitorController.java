package com.dormitory.controller;

import com.dormitory.model.Visitor;
import com.dormitory.service.VisitorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visitors")
public class VisitorController {
    
    private final VisitorService visitorService;
    
    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            LocalDateTime date) {
        
        List<Visitor> visitors;
        if (roomId != null) {
            visitors = visitorService.findByRoomId(roomId);
        } else if (status != null) {
            visitors = visitorService.findByStatus(status);
        } else if (name != null && !name.isEmpty()) {
            visitors = visitorService.searchByName(name);
        } else if (date != null) {
            visitors = visitorService.findByDate(date);
        } else {
            visitors = visitorService.findAll();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", visitors);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Visitor visitor = visitorService.findById(id);
        
        Map<String, Object> result = new HashMap<>();
        if (visitor == null) {
            result.put("code", 404);
            result.put("message", "访客记录不存在");
            return ResponseEntity.status(404).body(result);
        }
        
        result.put("code", 200);
        result.put("data", visitor);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/active/count")
    public ResponseEntity<Map<String, Object>> getActiveCount() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", Map.of("count", visitorService.getActiveCount()));
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Visitor visitor) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long id = visitorService.register(visitor);
            result.put("code", 201);
            result.put("message", "访客登记成功");
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
                                                     @RequestBody Visitor visitor) {
        Map<String, Object> result = new HashMap<>();
        
        visitor.setId(id);
        try {
            visitorService.update(visitor);
            result.put("code", 200);
            result.put("message", "更新成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @PostMapping("/{id}/leave")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> leave(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            visitorService.leave(id);
            result.put("code", 200);
            result.put("message", "访客已离开");
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
            visitorService.delete(id);
            result.put("code", 200);
            result.put("message", "删除成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}