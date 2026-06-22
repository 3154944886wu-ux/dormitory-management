package com.dormitory.controller;

import com.dormitory.model.InspectionPlan;
import com.dormitory.service.InspectionPlanService;
import com.dormitory.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全卫生检查计划管理
 */
@RestController
@RequestMapping("/api/inspection/plans")
public class InspectionPlanController {

    @Autowired
    private InspectionPlanService planService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 分页获取检查计划列表
     */
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        List<InspectionPlan> plans = planService.findAll(page, size);
        int total = planService.count();

        Map<String, Object> result = new HashMap<>();
        result.put("data", plans);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return ResponseEntity.ok(result);
    }

    /**
     * 获取待执行的检查计划
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPending() {
        List<InspectionPlan> plans = planService.findByStatus("SCHEDULED");
        return ResponseEntity.ok(Map.of("data", plans));
    }

    /**
     * 按状态查询
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable String status) {
        List<InspectionPlan> plans = planService.findByStatus(status);
        return ResponseEntity.ok(Map.of("data", plans));
    }

    /**
     * 按类型查询
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getByType(@PathVariable String type) {
        List<InspectionPlan> plans = planService.findByType(type);
        return ResponseEntity.ok(Map.of("data", plans));
    }

    /**
     * 获取单个检查计划
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        InspectionPlan plan = planService.findById(id);
        if (plan == null) {
            return ResponseEntity.status(404).body(Map.of("message", "检查计划不存在"));
        }
        return ResponseEntity.ok(Map.of("data", plan));
    }

    /**
     * 创建检查计划
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody InspectionPlan plan,
                                   @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String username = jwtUtils.getUsernameFromToken(token.replace("Bearer ", ""));
            plan.setCreatorId(userId);
            plan.setCreatorName(username);

            InspectionPlan created = planService.create(plan);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "检查计划创建成功",
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
     * 更新检查计划
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InspectionPlan plan) {
        try {
            plan.setId(id);
            InspectionPlan updated = planService.update(plan);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "检查计划更新成功",
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
     * 更新计划状态（通用）
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            InspectionPlan updated = planService.updateStatus(id, status);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "状态更新成功",
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
     * 开始执行检查计划（DRAFT/SCHEDULED -> IN_PROGRESS）
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<?> startPlan(@PathVariable Long id) {
        try {
            InspectionPlan updated = planService.startPlan(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "检查计划已开始执行",
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
     * 完成检查计划（IN_PROGRESS -> COMPLETED）
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completePlan(@PathVariable Long id) {
        try {
            InspectionPlan updated = planService.completePlan(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "检查计划已完成",
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
     * 取消检查计划（-> CANCELLED）
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelPlan(@PathVariable Long id) {
        try {
            InspectionPlan updated = planService.cancelPlan(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "检查计划已取消",
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
     * 删除检查计划
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            planService.delete(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "检查计划删除成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
