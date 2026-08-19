package com.dormitory.controller;

import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.Student;
import com.dormitory.model.UtilityFee;
import com.dormitory.service.UtilityFeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utility-fees")
public class UtilityFeeController {
    
    private final UtilityFeeService feeService;
    private final StudentMapper studentMapper;

    public UtilityFeeController(UtilityFeeService feeService, StudentMapper studentMapper) {
        this.feeService = feeService;
        this.studentMapper = studentMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Integer status,
            Authentication auth) {

        List<UtilityFee> fees;
        if (isStudent(auth)) {
            Student student = requireStudent(auth);
            if (student.getRoomId() == null) {
                fees = List.of();
            } else {
                fees = feeService.findByRoomId(student.getRoomId());
                if (status != null) {
                    fees = fees.stream().filter(f -> status.equals(f.getStatus())).toList();
                }
            }
        } else if (roomId != null) {
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
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id, Authentication auth) {
        UtilityFee fee = feeService.findById(id);

        Map<String, Object> result = new HashMap<>();
        if (fee == null) {
            result.put("code", 404);
            result.put("message", "费用记录不存在");
            return ResponseEntity.status(404).body(result);
        }
        if (isStudent(auth)) {
            Student student = requireStudent(auth);
            if (student.getRoomId() == null || !student.getRoomId().equals(fee.getRoomId())) {
                result.put("code", 403);
                result.put("message", "无权查看该费用");
                return ResponseEntity.status(403).body(result);
            }
        }

        result.put("code", 200);
        result.put("data", fee);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();

        try {
            UtilityFee fee = new UtilityFee();

            // roomId
            Object roomIdObj = body.get("roomId");
            if (roomIdObj == null) throw new RuntimeException("请选择房间");
            fee.setRoomId(Long.valueOf(roomIdObj.toString()));

            // 月份: "YYYY-MM" -> year, month
            String monthStr = (String) body.get("month");
            if (monthStr == null || monthStr.isEmpty()) throw new RuntimeException("请选择月份");
            String[] parts = monthStr.split("-");
            fee.setYear(Integer.parseInt(parts[0]));
            fee.setMonth(Integer.parseInt(parts[1]));

            // 直接传入的金额
            Object electricFeeObj = body.get("electricFee");
            Object waterFeeObj = body.get("waterFee");
            BigDecimal electricFee = electricFeeObj != null ? new BigDecimal(electricFeeObj.toString()) : BigDecimal.ZERO;
            BigDecimal waterFee = waterFeeObj != null ? new BigDecimal(waterFeeObj.toString()) : BigDecimal.ZERO;

            fee.setElectricityFee(electricFee);
            fee.setWaterFee(waterFee);
            fee.setTotalFee(electricFee.add(waterFee));

            // 设置默认读数（避免计算错误）
            fee.setElectricityStart(BigDecimal.ZERO);
            fee.setElectricityEnd(BigDecimal.ZERO);
            fee.setElectricityUsage(BigDecimal.ZERO);
            fee.setWaterStart(BigDecimal.ZERO);
            fee.setWaterEnd(BigDecimal.ZERO);
            fee.setWaterUsage(BigDecimal.ZERO);

            // 状态
            Object statusObj = body.get("status");
            fee.setStatus(statusObj != null ? Integer.valueOf(statusObj.toString()) : 0);

            Long id = feeService.createDirect(fee);
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
                                                      @RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();

        try {
            UtilityFee fee = feeService.findById(id);
            if (fee == null) throw new RuntimeException("费用记录不存在");

            // roomId
            Object roomIdObj = body.get("roomId");
            if (roomIdObj != null) fee.setRoomId(Long.valueOf(roomIdObj.toString()));

            // 月份
            String monthStr = (String) body.get("month");
            if (monthStr != null && !monthStr.isEmpty()) {
                String[] parts = monthStr.split("-");
                fee.setYear(Integer.parseInt(parts[0]));
                fee.setMonth(Integer.parseInt(parts[1]));
            }

            // 直接传入的金额
            Object electricFeeObj = body.get("electricFee");
            Object waterFeeObj = body.get("waterFee");
            if (electricFeeObj != null) fee.setElectricityFee(new BigDecimal(electricFeeObj.toString()));
            if (waterFeeObj != null) fee.setWaterFee(new BigDecimal(waterFeeObj.toString()));
            fee.setTotalFee(fee.getElectricityFee().add(fee.getWaterFee()));

            // 状态
            Object statusObj = body.get("status");
            if (statusObj != null) fee.setStatus(Integer.valueOf(statusObj.toString()));

            feeService.updateDirect(fee);
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

    private boolean isStudent(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_STUDENT".equals(a.getAuthority()));
    }

    private Student requireStudent(Authentication auth) {
        Student student = studentMapper.findByStudentNo(auth.getName());
        if (student == null) {
            throw new RuntimeException("当前账号未关联学生信息");
        }
        return student;
    }
}