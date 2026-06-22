package com.dormitory.controller;

import com.dormitory.model.UtilityFee;
import com.dormitory.service.UtilityFeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utility-fees")
public class UtilityFeeController {
    
    private final UtilityFeeService feeService;
    
    public UtilityFeeController(UtilityFeeService feeService) {
        this.feeService = feeService;
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Integer status) {
        
        List<UtilityFee> fees;
        if (roomId != null) {
            fees = feeService.findByRoomId(roomId);
        } else if (status != null) {
            fees = feeService.findByStatus(status);
        } else {
            fees = feeService.findAll();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", fees);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        UtilityFee fee = feeService.findById(id);
        
        Map<String, Object> result = new HashMap<>();
        if (fee == null) {
            result.put("code", 404);
            result.put("message", "费用记录不存在");
            return ResponseEntity.status(404).body(result);
        }
        
        result.put("code", 200);
        result.put("data", fee);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody UtilityFee fee) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long id = feeService.create(fee);
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
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, 
                                                      @RequestBody UtilityFee fee) {
        Map<String, Object> result = new HashMap<>();
        
        fee.setId(id);
        try {
            feeService.update(fee);
            result.put("code", 200);
            result.put("message", "更新成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @PostMapping("/{id}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> pay(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            feeService.pay(id);
            result.put("code", 200);
            result.put("message", "缴费成功");
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
            feeService.delete(id);
            result.put("code", 200);
            result.put("message", "删除成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 批量生成某月费用记录
     */
    @PostMapping("/batch-generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> batchGenerate(
            @RequestParam Integer year, @RequestParam Integer month) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int created = feeService.batchGenerate(year, month);
            result.put("code", 200);
            result.put("message", "成功生成 " + created + " 条费用记录");
            result.put("data", Map.of("created", created));
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}