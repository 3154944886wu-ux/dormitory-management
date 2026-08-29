package com.dormitory.controller;

import com.dormitory.mapper.UserMapper;
import com.dormitory.model.Room;
import com.dormitory.model.User;
import com.dormitory.service.BedService;
import com.dormitory.service.ManagerScopeService;
import com.dormitory.service.RoomService;
import com.dormitory.utils.ApiResponses;
import com.dormitory.utils.AuthRoles;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.dormitory.model.Bed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/beds")
public class BedController {

    private final BedService bedService;
    private final RoomService roomService;
    private final ManagerScopeService managerScopeService;
    private final UserMapper userMapper;

    public BedController(BedService bedService,
                         RoomService roomService,
                         ManagerScopeService managerScopeService,
                         UserMapper userMapper) {
        this.bedService = bedService;
        this.roomService = roomService;
        this.managerScopeService = managerScopeService;
        this.userMapper = userMapper;
    }

    @GetMapping("/available/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getAvailableBeds(@PathVariable Long roomId,
                                                                Authentication auth) {
        Room room = roomService.findById(roomId);
        Map<String, Object> result = new HashMap<>();
        if (room == null) {
            result.put("code", 404);
            result.put("message", "房间不存在");
            return ResponseEntity.status(404).body(result);
        }
        Long managerId = managerUserId(auth);
        if (managerId != null && !managerScopeService.canSeeBuilding(managerId, room.getBuildingId())) {
            return ApiResponses.forbidden("无权查看该范围外的床位");
        }
        List<Bed> beds = bedService.findAvailableByRoomId(roomId);
        result.put("code", 200);
        result.put("data", beds);
        return ResponseEntity.ok(result);
    }

    private Long managerUserId(Authentication auth) {
        if (!AuthRoles.isManagerOnly(auth)) {
            return null;
        }
        User user = userMapper.findByUsername(auth.getName());
        return user == null ? null : user.getId();
    }
}
