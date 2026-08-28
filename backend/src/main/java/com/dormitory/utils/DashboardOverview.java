package com.dormitory.utils;

import com.dormitory.model.AllocationResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DashboardOverview {

    private DashboardOverview() {
    }

    public static Map<String, Object> overview(int buildingCount,
                                               Map<String, Object> roomFill,
                                               int studentCount,
                                               Map<String, Object> repairOverview,
                                               int activeVisitors) {
        Map<String, Object> data = new HashMap<>();
        data.put("buildingCount", buildingCount);
        if (roomFill != null) {
            data.putAll(roomFill);
        }
        data.put("studentCount", studentCount);
        if (repairOverview != null) {
            data.putAll(repairOverview);
        }
        data.put("activeVisitors", activeVisitors);
        return data;
    }

    public static Map<String, Object> accommodation(int buildingCount,
                                                    Map<String, Object> roomFill,
                                                    int studentCount) {
        int totalRooms = number(roomFill, "roomCount");
        int occupiedRooms = number(roomFill, "occupiedRooms");
        Map<String, Object> data = new HashMap<>();
        data.put("totalRooms", totalRooms);
        data.put("occupiedRooms", occupiedRooms);
        data.put("vacantRooms", totalRooms - occupiedRooms);
        data.put("studentCount", studentCount);
        data.put("buildingCount", buildingCount);
        data.put("occupancyRate", RoomFill.occupancyRate(occupiedRooms, totalRooms));
        return data;
    }

    public static int distinctBatchCount(List<AllocationResult> results) {
        if (results == null || results.isEmpty()) {
            return 0;
        }
        Set<Long> batchIds = new HashSet<>();
        for (AllocationResult result : results) {
            if (result != null && result.getBatchId() != null) {
                batchIds.add(result.getBatchId());
            }
        }
        return batchIds.size();
    }

    public static Map<String, Object> dormStats(List<AllocationResult> results, int activeBatches) {
        List<AllocationResult> list = results == null ? List.of() : results;
        int pendingConfirm = 0;
        BigDecimal sum = BigDecimal.ZERO;
        int scored = 0;
        for (AllocationResult result : list) {
            if (result == null) {
                continue;
            }
            if ("recommended".equals(result.getStatus())) {
                pendingConfirm++;
            }
            if (result.getMatchScore() != null) {
                sum = sum.add(result.getMatchScore());
                scored++;
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("activeBatches", activeBatches);
        data.put("totalAllocated", list.size());
        data.put("pendingConfirm", pendingConfirm);
        data.put("avgMatchScore", scored == 0
                ? "0.0"
                : sum.divide(BigDecimal.valueOf(scored), 1, RoundingMode.HALF_UP).toPlainString());
        return data;
    }

    private static int number(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) {
            return 0;
        }
        Object value = map.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }
}
