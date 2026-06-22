package com.dormitory.controller;

import com.dormitory.model.CheckRule;
import com.dormitory.service.CheckRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 归寝规则管理控制器
 */
@RestController
@RequestMapping("/api/check-rules")
@CrossOrigin
public class CheckRuleController {

    @Autowired
    private CheckRuleService checkRuleService;

    /**
     * 获取所有规则
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAll() {
        List<CheckRule> rules = checkRuleService.findAll();
        return ResponseEntity.ok(Map.of("data", rules));
    }

    /**
     * 获取生效的规则
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<?> getActive() {
        List<CheckRule> rules = checkRuleService.findActive();
        return ResponseEntity.ok(Map.of("data", rules));
    }

    /**
     * 获取默认规则
     */
    @GetMapping("/default")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<?> getDefault() {
        CheckRule rule = checkRuleService.findDefault();
        if (rule == null) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "未设置默认规则"
            ));
        }
        return ResponseEntity.ok(Map.of("data", rule));
    }

    /**
     * 根据楼栋获取规则
     */
    @GetMapping("/building/{buildingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<?> getByBuilding(@PathVariable Long buildingId) {
        CheckRule rule = checkRuleService.findByBuildingId(buildingId);
        if (rule == null) {
            // 返回默认规则
            rule = checkRuleService.findDefault();
        }
        return ResponseEntity.ok(Map.of("data", rule));
    }

    /**
     * 获取规则详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        CheckRule rule = checkRuleService.findById(id);
        if (rule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("data", rule));
    }

    /**
     * 创建规则
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody CheckRule rule) {
        try {
            CheckRule created = checkRuleService.create(rule);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "规则创建成功",
                "data", created
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "规则创建失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 更新规则
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CheckRule rule) {
        try {
            rule.setId(id);
            CheckRule updated = checkRuleService.update(rule);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "规则更新成功",
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
     * 删除规则
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            checkRuleService.delete(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "规则删除成功"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 设为默认规则
     */
    @PostMapping("/{id}/set-default")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setDefault(@PathVariable Long id) {
        try {
            CheckRule rule = checkRuleService.setDefault(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "已设为默认规则",
                "data", rule
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 切换规则启用/停用
     */
    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            Integer status = body.get("status");
            CheckRule rule = checkRuleService.updateStatus(id, status);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", status != null && status == 1 ? "规则已启用" : "规则已停用",
                "data", rule
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}