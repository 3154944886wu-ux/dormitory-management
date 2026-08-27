package com.dormitory.controller;

import com.dormitory.mapper.*;
import com.dormitory.model.Repair;
import com.dormitory.model.UtilityFee;
import com.dormitory.utils.DashboardFees;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class DashboardController {

    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final StudentMapper studentMapper;
    private final UtilityFeeMapper utilityFeeMapper;
    private final RepairMapper repairMapper;
    private final VisitorMapper visitorMapper;
    private final DormBatchMapper dormBatchMapper;
    private final AllocationResultMapper allocationResultMapper;

    public DashboardController(BuildingMapper buildingMapper,
                              RoomMapper roomMapper,
                              StudentMapper studentMapper,
                              UtilityFeeMapper utilityFeeMapper,
                              RepairMapper repairMapper,
                              VisitorMapper visitorMapper,
                              DormBatchMapper dormBatchMapper,
                              AllocationResultMapper allocationResultMapper) {
        this.buildingMapper = buildingMapper;
        this.roomMapper = roomMapper;
        this.studentMapper = studentMapper;
        this.utilityFeeMapper = utilityFeeMapper;
        this.repairMapper = repairMapper;
        this.visitorMapper = visitorMapper;
        this.dormBatchMapper = dormBatchMapper;
        this.allocationResultMapper = allocationResultMapper;
    }
    
    /**
     * 获取概览数据
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        Map<String, Object> data = new HashMap<>();
        
        // 楼栋统计
        int buildingCount = buildingMapper.count();
        data.put("buildingCount", buildingCount);
        
        // 房间统计
        int totalRooms = roomMapper.count();
        int freeRooms = roomMapper.countFree();
        int partialRooms = roomMapper.countPartial();
        int fullRooms = roomMapper.countFull();
        data.put("roomCount", totalRooms);
        data.put("freeRooms", freeRooms);
        data.put("partialRooms", partialRooms);
        data.put("fullRooms", fullRooms);
        
        // 学生统计
        int studentCount = studentMapper.count();
        data.put("studentCount", studentCount);
        
        // 报修统计
        int pendingRepairs = repairMapper.countByStatus(0);
        int processingRepairs = repairMapper.countByStatus(1);
        int completedRepairs = repairMapper.countByStatus(2);
        int closedRepairs = repairMapper.countByStatus(3);
        data.put("pendingRepairs", pendingRepairs);
        data.put("processingRepairs", processingRepairs);
        data.put("completedRepairs", completedRepairs);
        data.put("closedRepairs", closedRepairs);
        
        // 访客统计
        int activeVisitors = visitorMapper.countActive();
        data.put("activeVisitors", activeVisitors);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取入住统计
     */
    @GetMapping("/accommodation")
    public ResponseEntity<Map<String, Object>> getAccommodationStats() {
        Map<String, Object> data = new HashMap<>();
        
        int totalRooms = roomMapper.count();
        int occupiedRooms = roomMapper.countOccupied();
        int studentCount = studentMapper.count();
        int buildingCount = buildingMapper.count();
        
        data.put("totalRooms", totalRooms);
        data.put("occupiedRooms", occupiedRooms);
        data.put("vacantRooms", totalRooms - occupiedRooms);
        data.put("studentCount", studentCount);
        data.put("buildingCount", buildingCount);
        
        // 计算入住率
        double occupancyRate = totalRooms > 0 ? (double) occupiedRooms / totalRooms * 100 : 0;
        data.put("occupancyRate", String.format("%.1f", occupancyRate));
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取报修统计
     */
    @GetMapping("/repair")
    public ResponseEntity<Map<String, Object>> getRepairStats() {
        Map<String, Object> data = new HashMap<>();
        
        // 按状态统计
        int pending = repairMapper.countByStatus(0);
        int processing = repairMapper.countByStatus(1);
        int completed = repairMapper.countByStatus(2);
        int closed = repairMapper.countByStatus(3);
        int total = pending + processing + completed + closed;
        
        data.put("pending", pending);
        data.put("processing", processing);
        data.put("completed", completed);
        data.put("closed", closed);
        data.put("total", total);
        
        // 最近报修列表（取前5条）
        List<Repair> recentRepairs = repairMapper.findAll();
        if (recentRepairs.size() > 5) {
            recentRepairs = recentRepairs.subList(0, 5);
        }
        data.put("recentRepairs", recentRepairs);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取水电费统计
     */
    @GetMapping("/utility")
    public ResponseEntity<Map<String, Object>> getUtilityStats() {
        Map<String, Object> data = new HashMap<>();
        
        // 获取所有水电费记录
        List<UtilityFee> allFees = utilityFeeMapper.findAll();
        
        // 统计总数
        int total = allFees.size();
        data.put("total", total);
        
        // 按状态统计
        long unpaid = allFees.stream().filter(f -> f.getStatus() == 0).count();
        long paid = allFees.stream().filter(f -> f.getStatus() == 1).count();
        data.put("unpaid", unpaid);
        data.put("paid", paid);
        
        // 计算总金额
        java.math.BigDecimal totalAmount = allFees.stream()
            .map(f -> f.getTotalFee() != null ? f.getTotalFee() : java.math.BigDecimal.ZERO)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal paidAmount = allFees.stream()
            .filter(f -> f.getStatus() == 1)
            .map(f -> f.getTotalFee() != null ? f.getTotalFee() : java.math.BigDecimal.ZERO)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal unpaidAmount = totalAmount.subtract(paidAmount);
        
        data.put("totalAmount", totalAmount);
        data.put("paidAmount", paidAmount);
        data.put("unpaidAmount", unpaidAmount);
        data.putAll(DashboardFees.summarize(allFees));
        
        // 最近账单（取前5条）
        List<UtilityFee> recentFees = allFees.size() > 5 ? allFees.subList(0, 5) : allFees;
        data.put("recentFees", recentFees);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 选宿统计
     */
    @GetMapping("/dorm-stats")
    public ResponseEntity<Map<String, Object>> getDormStats() {
        Map<String, Object> data = new HashMap<>();

        int activeBatches = dormBatchMapper.countActive();
        data.put("activeBatches", activeBatches);

        int totalAllocated = allocationResultMapper.countAll();
        data.put("totalAllocated", totalAllocated);

        java.math.BigDecimal avgScore = allocationResultMapper.avgTotalMatchScore();
        data.put("avgMatchScore", avgScore != null ? String.format("%.1f", avgScore) : "0.0");

        int pendingConfirm = allocationResultMapper.countByStatus("recommended");
        data.put("pendingConfirm", pendingConfirm);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取统计数据（兼容旧接口）
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return getOverview();
    }
}