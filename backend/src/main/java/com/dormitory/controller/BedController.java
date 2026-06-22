package com.dormitory.controller;

import com.dormitory.model.Bed;
import com.dormitory.service.BedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/beds")
public class BedController {

    private final BedService bedService;

    public BedController(BedService bedService) {
        this.bedService = bedService;
    }

    @GetMapping("/available/{roomId}")
    public ResponseEntity<Map<String, Object>> getAvailableBeds(@PathVariable Long roomId) {
        List<Bed> beds = bedService.findAvailableByRoomId(roomId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", beds);
        return ResponseEntity.ok(result);
    }
}
