package com.dormitory.controller;

import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.model.InspectionRecord;
import com.dormitory.model.Room;
import com.dormitory.model.Student;
import com.dormitory.model.User;
import com.dormitory.service.InspectionRecordService;
import com.dormitory.service.ManagerScopeService;
import com.dormitory.utils.ApiResponses;
import com.dormitory.utils.AuthRoles;
import com.dormitory.utils.InspectionRoomAccess;
import com.dormitory.utils.JwtUtils;
import com.dormitory.utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全卫生检查记录管理
 */
@RestController
@RequestMapping("/api/inspection/records")
public class InspectionRecordController {

    @Autowired
    private InspectionRecordService recordService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ManagerScopeService managerScopeService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoomMapper roomMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Authentication auth) {
        int safePage = Pagination.page(page);
        int safeSize = Pagination.size(size);
        List<InspectionRecord> records;
        int total;
        Long managerId = managerUserId(auth);
        if (managerId != null) {
            if (!managerScopeService.hasScope(managerId)) {
                records = List.of();
                total = 0;
            } else {
                List<InspectionRecord> all = filterForManager(auth, recordService.findAll());
                total = all.size();
                records = Pagination.slice(all, safePage, safeSize);
            }
        } else {
            records = recordService.findAll(safePage, safeSize);
            total = recordService.count();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", records);
        result.put("total", total);
        result.put("page", safePage);
        result.put("size", safeSize);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getPending(Authentication auth) {
        List<InspectionRecord> records = filterForManager(auth, recordService.findByRectificationStatus("PENDING"));
        return ResponseEntity.ok(Map.of("data", records));
    }

    @GetMapping("/plan/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByPlanId(@PathVariable Long planId, Authentication auth) {
        List<InspectionRecord> records = filterForManager(auth, recordService.findByPlanId(planId));
        return ResponseEntity.ok(Map.of("data", records));
    }

    @GetMapping("/room/{roomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getByRoomId(@PathVariable Long roomId, Authentication auth) {
        String role = currentRole(auth);
        Long studentRoomId = null;
        if ("STUDENT".equals(role) && auth != null) {
            Student student = studentMapper.findByStudentNo(auth.getName());
            studentRoomId = student == null ? null : student.getRoomId();
        }
        if (!InspectionRoomAccess.canView(role, roomId, studentRoomId)) {
            return ResponseEntity.status(403).body(Map.of("code", 403, "message", "无权查看该房间检查记录"));
        }
        ResponseEntity<?> denied = denyIfRoomOutOfScope(auth, roomId);
        if (denied != null) {
            return denied;
        }
        List<InspectionRecord> records = recordService.findByRoomId(roomId);
        return ResponseEntity.ok(Map.of("data", records));
    }

    private String currentRole(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) {
            return null;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a != null && a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByStatus(@PathVariable String status, Authentication auth) {
        List<InspectionRecord> records = filterForManager(auth, recordService.findByRectificationStatus(status));
        return ResponseEntity.ok(Map.of("data", records));
    }

    @GetMapping("/result/{result}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByResult(@PathVariable String result, Authentication auth) {
        List<InspectionRecord> records = filterForManager(auth, recordService.findByResult(result));
        return ResponseEntity.ok(Map.of("data", records));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> search(
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String result,
            @RequestParam(required = false) String rectificationStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication auth) {
        List<InspectionRecord> records = filterForManager(auth, recordService.search(
            planId, buildingId, result, rectificationStatus, startDate, endDate));
        return ResponseEntity.ok(Map.of("data", records));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getById(@PathVariable Long id, Authentication auth) {
        InspectionRecord record = recordService.findById(id);
        if (record == null) {
            return ResponseEntity.status(404).body(Map.of("message", "检查记录不存在"));
        }
        ResponseEntity<?> denied = denyIfOutOfScope(auth, record);
        if (denied != null) {
            return denied;
        }
        return ResponseEntity.ok(Map.of("data", record));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> create(@RequestBody InspectionRecord record,
                                   @RequestHeader("Authorization") String token,
                                   Authentication auth) {
        try {
            fillBuildingFromRoom(record);
            ResponseEntity<?> denied = denyIfOutOfScope(auth, record);
            if (denied != null) {
                return denied;
            }
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String username = jwtUtils.getUsernameFromToken(token.replace("Bearer ", ""));
            record.setInspectorId(userId);
            record.setInspectorName(username);

            InspectionRecord created = recordService.create(record);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "检查记录创建成功",
                "data", created
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InspectionRecord record,
                                    Authentication auth) {
        try {
            InspectionRecord existing = recordService.findById(id);
            ResponseEntity<?> denied = denyIfOutOfScope(auth, existing);
            if (denied != null) {
                return denied;
            }
            record.setId(id);
            fillBuildingFromRoom(record);
            denied = denyIfOutOfScope(auth, record);
            if (denied != null) {
                return denied;
            }
            InspectionRecord updated = recordService.update(record);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "检查记录更新成功",
                "data", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/rectify")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> submitRectify(@PathVariable Long id,
                                           @RequestBody Map<String, String> body,
                                           @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            InspectionRecord existing = recordService.findById(id);
            if (existing == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "检查记录不存在"));
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String role = currentRole(auth);
            Long studentRoomId = null;
            if ("STUDENT".equals(role) && auth != null) {
                Student student = studentMapper.findByStudentNo(auth.getName());
                studentRoomId = student == null ? null : student.getRoomId();
            }
            if (!InspectionRoomAccess.canView(role, existing.getRoomId(), studentRoomId)) {
                return ResponseEntity.status(403).body(Map.of("code", 403, "success", false, "message", "无权提交该房间整改"));
            }
            ResponseEntity<?> denied = denyIfOutOfScope(auth, existing);
            if (denied != null) {
                return denied;
            }
            String rectificationPhotos = body.get("rectificationPhotos");
            String rectifyRemark = body.get("rectifyRemark");
            Long ownerUserId = null;
            if (token != null && token.startsWith("Bearer ")) {
                ownerUserId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            }
            if (ownerUserId == null && auth != null) {
                User user = userMapper.findByUsername(auth.getName());
                ownerUserId = user == null ? null : user.getId();
            }

            InspectionRecord updated = recordService.submitRectify(id, rectificationPhotos, rectifyRemark, ownerUserId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "整改提交成功",
                "data", updated
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> approveRectify(@PathVariable Long id,
                                            @RequestHeader("Authorization") String token,
                                            Authentication auth) {
        try {
            InspectionRecord existing = recordService.findById(id);
            ResponseEntity<?> denied = denyIfOutOfScope(auth, existing);
            if (denied != null) {
                return denied;
            }
            String username = jwtUtils.getUsernameFromToken(token.replace("Bearer ", ""));
            InspectionRecord updated = recordService.approveRectify(id, username);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "整改审核通过",
                "data", updated
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        try {
            InspectionRecord existing = recordService.findById(id);
            ResponseEntity<?> denied = denyIfOutOfScope(auth, existing);
            if (denied != null) {
                return denied;
            }
            recordService.delete(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "检查记录删除成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    private Long managerUserId(Authentication auth) {
        if (!AuthRoles.isManagerOnly(auth)) {
            return null;
        }
        User user = userMapper.findByUsername(auth.getName());
        return user == null ? null : user.getId();
    }

    private List<InspectionRecord> filterForManager(Authentication auth, List<InspectionRecord> records) {
        Long managerId = managerUserId(auth);
        if (managerId == null) {
            return records;
        }
        return managerScopeService.filterVisibleByRoom(managerId, records,
                InspectionRecord::getBuildingId, InspectionRecord::getRoomId);
    }

    private ResponseEntity<?> denyIfOutOfScope(Authentication auth, InspectionRecord record) {
        Long managerId = managerUserId(auth);
        if (managerId == null) {
            return null;
        }
        if (record == null) {
            return ResponseEntity.status(404).body(Map.of("code", 404, "success", false, "message", "检查记录不存在"));
        }
        if (!managerScopeService.canSeeRoom(managerId, record.getBuildingId(), record.getRoomId())) {
            return ApiResponses.forbidden("无权操作该范围外的检查记录");
        }
        return null;
    }

    private ResponseEntity<?> denyIfRoomOutOfScope(Authentication auth, Long roomId) {
        Long managerId = managerUserId(auth);
        if (managerId == null) {
            return null;
        }
        Room room = roomMapper.findById(roomId);
        if (room == null) {
            return ResponseEntity.status(404).body(Map.of("code", 404, "message", "房间不存在"));
        }
        if (!managerScopeService.canSeeRoom(managerId, room.getBuildingId(), roomId)) {
            return ApiResponses.forbidden("无权查看该范围外的房间检查记录");
        }
        return null;
    }

    private void fillBuildingFromRoom(InspectionRecord record) {
        if (record == null || record.getBuildingId() != null || record.getRoomId() == null) {
            return;
        }
        Room room = roomMapper.findById(record.getRoomId());
        if (room != null) {
            record.setBuildingId(room.getBuildingId());
        }
    }
}
