package com.dormitory.controller;

import com.dormitory.mapper.UserMapper;
import com.dormitory.model.InspectionPlan;
import com.dormitory.model.User;
import com.dormitory.service.InspectionPlanService;
import com.dormitory.service.ManagerScopeService;
import com.dormitory.utils.AuthRoles;
import com.dormitory.utils.InspectionPlanScope;
import com.dormitory.utils.JwtUtils;
import com.dormitory.utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 安全卫生检查计划管理
 */
@RestController
@RequestMapping("/api/inspection/plans")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class InspectionPlanController {

    @Autowired
    private InspectionPlanService planService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ManagerScopeService managerScopeService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 分页获取检查计划列表
     */
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Authentication auth) {
        List<InspectionPlan> plans = scoped(auth, planService.findAll());
        int safePage = Pagination.page(page);
        int safeSize = Pagination.size(size);
        Map<String, Object> result = new HashMap<>();
        result.put("data", Pagination.slice(plans, safePage, safeSize));
        result.put("total", plans.size());
        result.put("page", safePage);
        result.put("size", safeSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取待执行的检查计划
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPending(Authentication auth) {
        List<InspectionPlan> plans = scoped(auth, planService.findByStatus("SCHEDULED"));
        return ResponseEntity.ok(Map.of("data", plans));
    }

    /**
     * 按状态查询
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable String status, Authentication auth) {
        List<InspectionPlan> plans = scoped(auth, planService.findByStatus(status));
        return ResponseEntity.ok(Map.of("data", plans));
    }

    /**
     * 按类型查询
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getByType(@PathVariable String type, Authentication auth) {
        List<InspectionPlan> plans = scoped(auth, planService.findByType(type));
        return ResponseEntity.ok(Map.of("data", plans));
    }

    /**
     * 获取单个检查计划
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, Authentication auth) {
        InspectionPlan plan = planService.findById(id);
        if (plan == null) {
            return ResponseEntity.status(404).body(Map.of("message", "检查计划不存在"));
        }
        ResponseEntity<?> denied = denyIfOutOfScope(auth, plan);
        if (denied != null) {
            return denied;
        }
        return ResponseEntity.ok(Map.of("data", plan));
    }

    /**
     * 创建检查计划
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody InspectionPlan plan,
                                   @RequestHeader("Authorization") String token,
                                   Authentication auth) {
        try {
            Set<Long> buildings = managerBuildingIds(auth);
            if (buildings != null && !InspectionPlanScope.fullyWithin(plan.getBuildingIds(), buildings)) {
                return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "无权创建范围外楼栋的检查计划"
                ));
            }
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
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InspectionPlan plan, Authentication auth) {
        try {
            ResponseEntity<?> denied = denyIfOutOfScope(auth, planService.findById(id));
            if (denied != null) {
                return denied;
            }
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
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body,
                                          Authentication auth) {
        try {
            ResponseEntity<?> denied = denyIfOutOfScope(auth, planService.findById(id));
            if (denied != null) {
                return denied;
            }
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
    public ResponseEntity<?> startPlan(@PathVariable Long id, Authentication auth) {
        try {
            ResponseEntity<?> denied = denyIfOutOfScope(auth, planService.findById(id));
            if (denied != null) {
                return denied;
            }
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
    public ResponseEntity<?> completePlan(@PathVariable Long id, Authentication auth) {
        try {
            ResponseEntity<?> denied = denyIfOutOfScope(auth, planService.findById(id));
            if (denied != null) {
                return denied;
            }
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
    public ResponseEntity<?> cancelPlan(@PathVariable Long id, Authentication auth) {
        try {
            ResponseEntity<?> denied = denyIfOutOfScope(auth, planService.findById(id));
            if (denied != null) {
                return denied;
            }
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
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        try {
            InspectionPlan plan = planService.findById(id);
            ResponseEntity<?> denied = denyIfOutOfScope(auth, plan);
            if (denied != null) {
                return denied;
            }
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

    private List<InspectionPlan> scoped(Authentication auth, List<InspectionPlan> plans) {
        Set<Long> buildings = managerBuildingIds(auth);
        if (buildings == null) {
            return plans;
        }
        return plans.stream()
                .filter(p -> InspectionPlanScope.visibleToManager(p.getBuildingIds(), buildings))
                .toList();
    }

    private Set<Long> managerBuildingIds(Authentication auth) {
        if (!AuthRoles.isManagerOnly(auth)) {
            return null;
        }
        User user = userMapper.findByUsername(auth.getName());
        if (user == null) {
            return Set.of();
        }
        return managerScopeService.findActiveByUserId(user.getId()).stream()
                .map(com.dormitory.model.ManagerScope::getBuildingId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
    }

    private ResponseEntity<?> denyIfOutOfScope(Authentication auth, InspectionPlan plan) {
        Set<Long> buildings = managerBuildingIds(auth);
        if (buildings == null) {
            return null;
        }
        if (plan == null || !InspectionPlanScope.visibleToManager(plan.getBuildingIds(), buildings)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "无权操作该范围外的检查计划"));
        }
        return null;
    }
}
