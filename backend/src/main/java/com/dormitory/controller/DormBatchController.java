package com.dormitory.controller;

import com.dormitory.model.DormBatch;
import com.dormitory.service.DormBatchService;
import com.dormitory.service.MatchingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batches")
public class DormBatchController {

    private final DormBatchService batchService;
    private final MatchingService matchingService;

    public DormBatchController(DormBatchService batchService, MatchingService matchingService) {
        this.batchService = batchService;
        this.matchingService = matchingService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> list(@RequestParam(required = false) Long collegeId,
                                     @RequestParam(required = false) String matchStatus) {
        List<DormBatch> list;
        if (collegeId != null) {
            list = batchService.findByCollegeId(collegeId);
        } else if (matchStatus != null && !matchStatus.isEmpty()) {
            list = batchService.findByMatchStatus(matchStatus);
        } else {
            list = batchService.findAll();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getById(@PathVariable Long id) {
        DormBatch batch = batchService.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (batch == null) {
            result.put("code", 404);
            result.put("message", "批次不存在");
            return result;
        }
        result.put("code", 200);
        result.put("data", batch);
        return result;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> create(@RequestBody DormBatch batch) {
        Map<String, Object> result = new HashMap<>();
        try {
            DormBatch created = batchService.create(batch);
            result.put("code", 201);
            result.put("message", "创建成功");
            result.put("data", created);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody DormBatch batch) {
        Map<String, Object> result = new HashMap<>();
        try {
            DormBatch updated = batchService.update(id, batch);
            result.put("code", 200);
            result.put("message", "更新成功");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}/start")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> start(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            DormBatch updated = batchService.startBatch(id);
            result.put("code", 200);
            result.put("message", "批次已启动");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 手动: running → cancelled（作废批次）
    @PutMapping("/{id}/cutoff")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> cutoff(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            DormBatch updated = batchService.cancelBatch(id);
            result.put("code", 200);
            result.put("message", "批次已作废（手动截止不触发匹配）");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 手动: confirming → finished
    @PutMapping("/{id}/finish")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> finish(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            DormBatch updated = batchService.advanceToFinished(id);
            result.put("code", 200);
            result.put("message", "批次已结束");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> reset(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            DormBatch updated = batchService.resetBatch(id);
            result.put("code", 200);
            result.put("message", "批次已重置");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            batchService.delete(id);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}/trigger-matching")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> triggerMatching(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            matchingService.executeMatching(id);
            result.put("code", 200);
            result.put("message", "匹配完成");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", "匹配失败: " + e.getMessage());
        }
        return result;
    }
}
