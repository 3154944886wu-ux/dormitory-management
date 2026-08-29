package com.dormitory.utils;

import com.dormitory.model.LeaveRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaveStatisticsTest {

    @Test
    void countsPendingAndApprovedLists() {
        LeaveRequest a = new LeaveRequest();
        LeaveRequest b = new LeaveRequest();
        Map<String, Object> stats = LeaveStatistics.of(List.of(a, b), List.of(a));
        assertEquals(2, stats.get("pendingCount"));
        assertEquals(1, stats.get("approvedCount"));
        assertEquals(List.of(a, b), stats.get("pending"));
    }

    @Test
    void nullListsBecomeZero() {
        Map<String, Object> stats = LeaveStatistics.of(null, null);
        assertEquals(0, stats.get("pendingCount"));
        assertEquals(0, stats.get("approvedCount"));
        assertEquals(List.of(), stats.get("pending"));
    }
}
