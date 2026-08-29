package com.dormitory.utils;

import com.dormitory.model.LeaveRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LeaveStatistics {

    private LeaveStatistics() {
    }

    public static Map<String, Object> of(List<LeaveRequest> pending, List<LeaveRequest> approved) {
        List<LeaveRequest> pendingList = pending == null ? List.of() : pending;
        List<LeaveRequest> approvedList = approved == null ? List.of() : approved;
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingCount", pendingList.size());
        stats.put("approvedCount", approvedList.size());
        stats.put("pending", pendingList);
        return stats;
    }
}
