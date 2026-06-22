package com.dormitory.controller;

import com.dormitory.model.BatchRoom;
import com.dormitory.model.Room;
import com.dormitory.service.BatchRoomService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batch-rooms")
public class BatchRoomController {

    private final BatchRoomService batchRoomService;

    public BatchRoomController(BatchRoomService batchRoomService) {
        this.batchRoomService = batchRoomService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> list(@RequestParam Long batchId) {
        List<BatchRoom> list = batchRoomService.findByBatchId(batchId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> available(@RequestParam Long batchId,
                                          @RequestParam(required = false) Long buildingId,
                                          @RequestParam(required = false) Integer floor,
                                          @RequestParam(required = false) Integer minAvailableBeds) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Room> rooms = batchRoomService.findAvailableRooms(batchId, buildingId, floor);
            if (minAvailableBeds != null && minAvailableBeds > 0) {
                rooms = rooms.stream()
                        .filter(r -> r.getAvailableBeds() >= minAvailableBeds)
                        .toList();
            }
            result.put("code", 200);
            result.put("data", rooms);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> addRooms(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long batchId = ((Number) body.get("batchId")).longValue();
            @SuppressWarnings("unchecked")
            List<Number> roomIdNums = (List<Number>) body.get("roomIds");
            List<Long> roomIds = roomIdNums.stream().map(Number::longValue).toList();

            Map<String, Object> addResult = batchRoomService.addRooms(batchId, roomIds);
            result.put("code", 200);
            result.put("message", "操作完成");
            result.put("data", addResult);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> removeRooms(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long batchId = ((Number) body.get("batchId")).longValue();
            @SuppressWarnings("unchecked")
            List<Number> roomIdNums = (List<Number>) body.get("roomIds");
            List<Long> roomIds = roomIdNums.stream().map(Number::longValue).toList();

            batchRoomService.removeRooms(batchId, roomIds);
            result.put("code", 200);
            result.put("message", "移除成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
