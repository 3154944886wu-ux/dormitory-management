package com.dormitory.controller;

import com.dormitory.mapper.UserMapper;
import com.dormitory.model.Room;
import com.dormitory.model.User;
import com.dormitory.service.ManagerScopeService;
import com.dormitory.service.RoomService;
import com.dormitory.utils.ApiResponses;
import com.dormitory.utils.AuthRoles;
import com.dormitory.utils.Pagination;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    
    private final RoomService roomService;
    private final ManagerScopeService managerScopeService;
    private final UserMapper userMapper;
    
    public RoomController(RoomService roomService,
                          ManagerScopeService managerScopeService,
                          UserMapper userMapper) {
        this.roomService = roomService;
        this.managerScopeService = managerScopeService;
        this.userMapper = userMapper;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) Long buildingId,
            @RequestParam(name = "pageNum", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "20") int size,
            Authentication auth) {
        int safePage = Pagination.page(page);
        int safeSize = Pagination.size(size);
        List<Room> rooms;
        long total;
        Long managerId = managerUserId(auth);
        if (managerId != null) {
            if (!managerScopeService.hasScope(managerId)
                    || (buildingId != null && !managerScopeService.canSeeBuilding(managerId, buildingId))) {
                rooms = List.of();
                total = 0;
            } else {
                List<Room> all = buildingId != null
                        ? roomService.findByBuildingId(buildingId)
                        : roomService.findAll();
                all = managerScopeService.filterVisibleByBuilding(managerId, all, Room::getBuildingId);
                total = all.size();
                rooms = Pagination.slice(all, safePage, safeSize);
            }
        } else {
            int offset = Pagination.offset(safePage, safeSize);
            if (buildingId != null) {
                rooms = roomService.findByBuildingIdWithPagination(buildingId, offset, safeSize);
                total = roomService.countByBuildingId(buildingId);
            } else {
                rooms = roomService.findAllWithPagination(offset, safeSize);
                total = roomService.countAll();
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", rooms);
        data.put("content", rooms);
        data.put("total", total);
        data.put("totalElements", total);
        data.put("totalPages", (int) Math.ceil(safeSize == 0 ? 0 : (double) total / safeSize));
        data.put("currentPage", safePage);
        data.put("pageSize", safeSize);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/building/{buildingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getByBuildingId(@PathVariable Long buildingId,
                                                               Authentication auth) {
        ResponseEntity<Map<String, Object>> denied = denyIfBuildingOutOfScope(auth, buildingId);
        if (denied != null) {
            return denied;
        }
        List<Room> rooms = roomService.findByBuildingId(buildingId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", rooms);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id, Authentication auth) {
        Room room = roomService.findById(id);
        
        Map<String, Object> result = new HashMap<>();
        if (room == null) {
            result.put("code", 404);
            result.put("message", "房间不存在");
            return ResponseEntity.status(404).body(result);
        }
        ResponseEntity<Map<String, Object>> denied = denyIfBuildingOutOfScope(auth, room.getBuildingId());
        if (denied != null) {
            return denied;
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

    private Long managerUserId(Authentication auth) {
        if (!AuthRoles.isManagerOnly(auth)) {
            return null;
        }
        User user = userMapper.findByUsername(auth.getName());
        return user == null ? null : user.getId();
    }

    private ResponseEntity<Map<String, Object>> denyIfBuildingOutOfScope(Authentication auth, Long buildingId) {
        Long managerId = managerUserId(auth);
        if (managerId == null) {
            return null;
        }
        if (!managerScopeService.canSeeBuilding(managerId, buildingId)) {
            return ApiResponses.forbidden("无权查看该范围外的房间");
        }
        return null;
    }
}