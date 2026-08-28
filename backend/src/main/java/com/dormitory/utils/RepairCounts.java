package com.dormitory.utils;

import com.dormitory.model.Repair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RepairCounts {

    private RepairCounts() {
    }

    public static Map<String, Object> overview(List<Repair> repairs) {
        int[] c = counts(repairs);
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingRepairs", c[0]);
        stats.put("processingRepairs", c[1]);
        stats.put("completedRepairs", c[2]);
        stats.put("closedRepairs", c[3]);
        return stats;
    }

    public static Map<String, Object> panel(List<Repair> repairs) {
        int[] c = counts(repairs);
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", c[0]);
        stats.put("processing", c[1]);
        stats.put("completed", c[2]);
        stats.put("closed", c[3]);
        stats.put("total", c[0] + c[1] + c[2] + c[3]);
        return stats;
    }

    private static int[] counts(List<Repair> repairs) {
        int[] c = new int[4];
        if (repairs == null) {
            return c;
        }
        for (Repair repair : repairs) {
            if (repair == null || repair.getStatus() == null) {
                continue;
            }
            int status = repair.getStatus();
            if (status >= 0 && status <= 3) {
                c[status]++;
            }
        }
        return c;
    }
}
