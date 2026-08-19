package com.dormitory.controller;

import com.dormitory.model.InspectionItem;
import com.dormitory.service.InspectionItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检查项模板管理
 */
@RestController
@RequestMapping("/api/inspection/items")
public class InspectionItemController {

    @Autowired
    private InspectionItemService itemService;

    /**
     * 分页获取所有检查项
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        List<InspectionItem> items = itemService.findAll(page, size);
        int total = itemService.count();

        Map<String, Object> result = new HashMap<>();
        result.put("data", items);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return ResponseEntity.ok(result);
    }

    /**
     * 获取启用的检查项
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getActive() {
        List<InspectionItem> items = itemService.findAllActive();
        return ResponseEntity.ok(Map.of("data", items));
    }

    /**
     * 按类别获取检查项
     */
    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getByCategory(@PathVariable String category) {
        List<InspectionItem> items = itemService.findByCategory(category);
        return ResponseEntity.ok(Map.of("data", items));
    }

    /**
     * 获取单个检查项
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        InspectionItem item = itemService.findById(id);
        if (item == null) {
            return ResponseEntity.status(404).body(Map.of("message", "检查项不存在"));
        }
        return ResponseEntity.ok(Map.of("data", item));
    }

    /**
     * 创建检查项
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody InspectionItem item) {
        try {
            InspectionItem created = itemService.create(item);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "检查项创建成功",
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
     * 更新检查项
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InspectionItem item) {
        try {
            item.setId(id);
            InspectionItem updated = itemService.update(item);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "检查项更新成功",
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
     * 删除检查项
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            itemService.delete(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "检查项删除成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
