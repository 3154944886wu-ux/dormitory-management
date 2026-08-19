package com.dormitory.controller;

import com.dormitory.model.Room;
import com.dormitory.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    
    private final RoomService roomService;
    
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) Long buildingId,
            @RequestParam(name = "pageNum", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "20") int size) {
        int offset = (page - 1) * size;
        List<Room> rooms;
        long total;
        if (buildingId != null) {
            rooms = roomService.findByBuildingIdWithPagination(buildingId, offset, size);
            total = roomService.countByBuildingId(buildingId);
        } else {
            rooms = roomService.findAllWithPagination(offset, size);
            total = roomService.countAll();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", rooms);
        data.put("content", rooms);
        data.put("total", total);
        data.put("totalElements", total);
        data.put("totalPages", (int) Math.ceil((double) total / size));
        data.put("currentPage", page);
        data.put("pageSize", size);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/building/{buildingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> getByBuildingId(@PathVariable Long buildingId) {
        List<Room> rooms = roomService.findByBuildingId(buildingId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", rooms);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Room room = roomService.findById(id);
        
        Map<String, Object> result = new HashMap<>();
        if (room == null) {
            result.put("code", 404);
            result.put("message", "房间不存在");
            return ResponseEntity.status(404).body(result);
        }
        
        result.put("code", 200);
        result.put("data", room);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Room room) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long id = roomService.create(room);
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
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Room room) {
        Map<String, Object> result = new HashMap<>();
        
        room.setId(id);
        try {
            roomService.update(room);
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
            roomService.delete(id);
            result.put("code", 200);
            result.put("message", "删除成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, 
                                                             @RequestBody Map<String, Integer> body) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Integer status = body.get("status");
            roomService.updateStatus(id, status);
            result.put("code", 200);
            result.put("message", "状态更新成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 批量创建房间（根据楼栋配置自动生成）
     */
    @PostMapping("/batch/{buildingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> batchCreate(@PathVariable Long buildingId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int created = roomService.batchCreate(buildingId);
            result.put("code", 200);
            result.put("message", "成功创建 " + created + " 个房间");
            result.put("data", Map.of("created", created));
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}