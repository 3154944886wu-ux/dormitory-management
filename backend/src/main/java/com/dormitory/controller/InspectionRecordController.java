package com.dormitory.controller;

import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.InspectionRecord;
import com.dormitory.model.Student;
import com.dormitory.service.InspectionRecordService;
import com.dormitory.utils.InspectionRoomAccess;
import com.dormitory.utils.JwtUtils;
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

    /**
     * 分页获取所有检查记录
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        List<InspectionRecord> records = recordService.findAll(page, size);
        int total = recordService.count();

        Map<String, Object> result = new HashMap<>();
        result.put("data", records);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return ResponseEntity.ok(result);
    }

    /**
     * 获取待整改记录
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getPending() {
        List<InspectionRecord> records = recordService.findByRectificationStatus("PENDING");
        return ResponseEntity.ok(Map.of("data", records));
    }

    /**
     * 按检查计划查询
     */
    @GetMapping("/plan/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByPlanId(@PathVariable Long planId) {
        List<InspectionRecord> records = recordService.findByPlanId(planId);
        return ResponseEntity.ok(Map.of("data", records));
    }

    /**
     * 按房间查询。管理员/宿管可查任意房间；学生仅能查本人入住房间。
     */
    @GetMapping("/room/{roomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getByRoomId(@PathVariable Long roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = currentRole(auth);
        Long studentRoomId = null;
        if ("STUDENT".equals(role) && auth != null) {
            Student student = studentMapper.findByStudentNo(auth.getName());
            studentRoomId = student == null ? null : student.getRoomId();
        }
        if (!InspectionRoomAccess.canView(role, roomId, studentRoomId)) {
            return ResponseEntity.status(403).body(Map.of("code", 403, "message", "无权查看该房间检查记录"));
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

    /**
     * 按整改状态查询
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByStatus(@PathVariable String status) {
        List<InspectionRecord> records = recordService.findByRectificationStatus(status);
        return ResponseEntity.ok(Map.of("data", records));
    }

    /**
     * 按检查结果查询
     */
    @GetMapping("/result/{result}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByResult(@PathVariable String result) {
        List<InspectionRecord> records = recordService.findByResult(result);
        return ResponseEntity.ok(Map.of("data", records));
    }

    /**
     * 多条件搜索检查记录
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> search(
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String result,
            @RequestParam(required = false) String rectificationStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<InspectionRecord> records = recordService.search(
            planId, buildingId, result, rectificationStatus, startDate, endDate);
        return ResponseEntity.ok(Map.of("data", records));
    }

    /**
     * 获取单个检查记录
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        InspectionRecord record = recordService.findById(id);
        if (record == null) {
            return ResponseEntity.status(404).body(Map.of("message", "检查记录不存在"));
        }
        return ResponseEntity.ok(Map.of("data", record));
    }

    /**
     * 创建检查记录（提交检查结果）
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> create(@RequestBody InspectionRecord record,
                                   @RequestHeader("Authorization") String token) {
        try {
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

    /**
     * 更新检查记录
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InspectionRecord record) {
        try {
            record.setId(id);
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

    /**
     * 提交整改（PENDING -> COMPLETED）
     */
    @PostMapping("/{id}/rectify")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> submitRectify(@PathVariable Long id,
                                           @RequestBody Map<String, String> body) {
        try {
            String rectificationPhotos = body.get("rectificationPhotos");
            String rectifyRemark = body.get("rectifyRemark");

            InspectionRecord updated = recordService.submitRectify(id, rectificationPhotos, rectifyRemark);
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

    /**
     * 审核整改（COMPLETED -> VERIFIED）
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> approveRectify(@PathVariable Long id,
                                            @RequestHeader("Authorization") String token) {
        try {
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

    /**
     * 删除检查记录
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
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
}
