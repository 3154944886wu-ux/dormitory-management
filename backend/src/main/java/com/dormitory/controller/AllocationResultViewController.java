package com.dormitory.controller;

import com.dormitory.mapper.*;
import com.dormitory.model.*;
import com.dormitory.service.*;
import jakarta.servlet.http.HttpServletResponse;
import com.dormitory.utils.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/batches")
public class AllocationResultViewController {

    private final BatchRoomMapper batchRoomMapper;
    private final BedMapper bedMapper;
    private final StudentMapper studentMapper;
    private final AllocationResultMapper allocationResultMapper;
    private final AllocationReportService reportService;
    private final AllocationStatisticsService statisticsService;
    private final DormBatchService batchService;

    public AllocationResultViewController(BatchRoomMapper batchRoomMapper,
                                           BedMapper bedMapper,
                                           StudentMapper studentMapper,
                                           AllocationResultMapper allocationResultMapper,
                                           AllocationReportService reportService,
                                           AllocationStatisticsService statisticsService,
                                           DormBatchService batchService) {
        this.batchRoomMapper = batchRoomMapper;
        this.bedMapper = bedMapper;
        this.studentMapper = studentMapper;
        this.allocationResultMapper = allocationResultMapper;
        this.reportService = reportService;
        this.statisticsService = statisticsService;
        this.batchService = batchService;
    }

    // ========== 分配结果视图 ==========

    @GetMapping("/{batchId}/view-rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> viewRooms(@PathVariable Long batchId,
                                         @RequestParam(required = false) Long buildingId,
                                         @RequestParam(required = false) String roomNumber,
                                         @RequestParam(required = false) String occupancyStatus) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Room> rooms = batchRoomMapper.findRoomsByBatchIdWithFilter(batchId, buildingId, roomNumber);

            if (occupancyStatus != null && !occupancyStatus.isEmpty() && !"all".equals(occupancyStatus)) {
                rooms = rooms.stream().filter(r -> {
                    int cur = r.getOccupancy() != null ? r.getOccupancy()
                            : (r.getCurrentCount() != null ? r.getCurrentCount() : 0);
                    int cap = r.getCapacity() != null ? r.getCapacity() : 4;
                    return switch (occupancyStatus) {
                        case "empty" -> cur == 0;
                        case "partial" -> cur > 0 && cur < cap;
                        case "full" -> cur >= cap;
                        default -> true;
                    };
                }).collect(Collectors.toList());
            }

            result.put("code", 200);
            result.put("data", rooms);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    @GetMapping("/{batchId}/view-rooms/{roomId}/beds")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> viewRoomBeds(@PathVariable Long batchId,
                                            @PathVariable Long roomId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Bed> beds = bedMapper.findByRoomId(roomId);
            List<Student> students = studentMapper.findByRoomId(roomId);
            List<AllocationResult> ars = allocationResultMapper.findByRoomIdAndBatchId(roomId, batchId);

            Map<Long, Student> studentMap = students.stream()
                    .filter(s -> s.getBedNumber() != null)
                    .collect(Collectors.toMap(s -> {
                        for (Bed b : beds) {
                            if (b.getBedNumber() != null && b.getBedNumber().equals(s.getBedNumber())) {
                                return b.getId();
                            }
                        }
                        return 0L;
                    }, s -> s, (a, b) -> a));

            Map<Long, AllocationResult> arMap = ars.stream()
                    .filter(ar -> ar.getBedId() != null)
                    .collect(Collectors.toMap(AllocationResult::getBedId, ar -> ar, (a, b) -> a));

            List<Map<String, Object>> bedList = new ArrayList<>();
            for (Bed bed : beds) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("bedId", bed.getId());
                item.put("bedNumber", bed.getBedNumber());
                item.put("bedType", bed.getBedType());
                item.put("isOccupied", bed.getIsOccupied() != null && bed.getIsOccupied() == 1);

                AllocationResult ar = arMap.get(bed.getId());
                Student s = ar != null ? studentMapper.findById(ar.getStudentId()) : studentMap.get(bed.getId());
                if (s != null) {
                    Map<String, Object> studentInfo = new LinkedHashMap<>();
                    studentInfo.put("id", s.getId());
                    studentInfo.put("studentNo", s.getStudentNo());
                    studentInfo.put("name", s.getName());
                    studentInfo.put("gender", s.getGender());
                    studentInfo.put("majorId", s.getMajorId());
                    studentInfo.put("matchScore", ar == null ? null : ar.getMatchScore());
                    studentInfo.put("allocationStatus", ar == null ? null : ar.getStatus());
                    item.put("student", studentInfo);
                } else {
                    item.put("student", null);
                }

                bedList.add(item);
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("beds", bedList);

            result.put("code", 200);
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    // ========== 分配报表 ==========

    @GetMapping("/{batchId}/report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getReport(@PathVariable Long batchId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<AllocationResult> report = reportService.getReport(batchId);
            result.put("code", 200);
            result.put("data", report);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    @GetMapping("/{batchId}/report/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public void downloadExcel(@PathVariable Long batchId, HttpServletResponse response) throws IOException {
        reportService.exportToExcel(batchId, response);
    }

    // ========== 统计分析 ==========

    @GetMapping("/{batchId}/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatistics(@PathVariable Long batchId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> stats = statisticsService.getBatchStats(batchId);
            result.put("code", 200);
            result.put("data", stats);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    // ========== 归档 ==========

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> archive(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            DormBatch updated = batchService.archiveBatch(id);
            result.put("code", 200);
            result.put("message", "批次已归档");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }
}
