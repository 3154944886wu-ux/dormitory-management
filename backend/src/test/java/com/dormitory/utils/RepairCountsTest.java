package com.dormitory.utils;

import com.dormitory.model.Repair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepairCountsTest {

    @Test
    void overviewKeysMatchDashboardCards() {
        Map<String, Object> stats = RepairCounts.overview(List.of(
                repair(0), repair(0), repair(1), repair(2), repair(3)));
        assertEquals(2, stats.get("pendingRepairs"));
        assertEquals(1, stats.get("processingRepairs"));
        assertEquals(1, stats.get("completedRepairs"));
        assertEquals(1, stats.get("closedRepairs"));
    }

    @Test
    void panelKeysIncludeTotal() {
        Map<String, Object> stats = RepairCounts.panel(List.of(repair(0), repair(3)));
        assertEquals(1, stats.get("pending"));
        assertEquals(0, stats.get("processing"));
        assertEquals(0, stats.get("completed"));
        assertEquals(1, stats.get("closed"));
        assertEquals(2, stats.get("total"));
    }

    private static Repair repair(int status) {
        Repair r = new Repair();
        r.setStatus(status);
        return r;
    }
}
