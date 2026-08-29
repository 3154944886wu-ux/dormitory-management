package com.dormitory.utils;

import com.dormitory.model.AllocationResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardOverviewTest {

    @Test
    void overviewMergesBuildingRoomStudentRepairVisitor() {
        Map<String, Object> rooms = Map.of(
                "roomCount", 10,
                "freeRooms", 4,
                "partialRooms", 3,
                "fullRooms", 3,
                "occupiedRooms", 6);
        Map<String, Object> repairs = Map.of(
                "pendingRepairs", 2,
                "processingRepairs", 1,
                "completedRepairs", 0,
                "closedRepairs", 1);
        Map<String, Object> data = DashboardOverview.overview(2, rooms, 15, repairs, 4);
        assertEquals(2, data.get("buildingCount"));
        assertEquals(10, data.get("roomCount"));
        assertEquals(4, data.get("freeRooms"));
        assertEquals(15, data.get("studentCount"));
        assertEquals(2, data.get("pendingRepairs"));
        assertEquals(4, data.get("activeVisitors"));
    }

    @Test
    void accommodationUsesLiveOccupiedRooms() {
        Map<String, Object> rooms = Map.of(
                "roomCount", 4,
                "occupiedRooms", 1,
                "freeRooms", 3,
                "partialRooms", 1,
                "fullRooms", 0);
        Map<String, Object> data = DashboardOverview.accommodation(1, rooms, 2);
        assertEquals(4, data.get("totalRooms"));
        assertEquals(1, data.get("occupiedRooms"));
        assertEquals(3, data.get("vacantRooms"));
        assertEquals(2, data.get("studentCount"));
        assertEquals(1, data.get("buildingCount"));
        assertEquals("25.0", data.get("occupancyRate"));
    }

    @Test
    void dormStatsScopeToVisibleAllocations() {
        AllocationResult recommended = new AllocationResult();
        recommended.setStatus("recommended");
        recommended.setMatchScore(new BigDecimal("80.0"));
        AllocationResult confirmed = new AllocationResult();
        confirmed.setStatus("confirmed");
        confirmed.setMatchScore(new BigDecimal("60.0"));
        Map<String, Object> data = DashboardOverview.dormStats(List.of(recommended, confirmed), 3);
        assertEquals(3, data.get("activeBatches"));
        assertEquals(2, data.get("totalAllocated"));
        assertEquals(1, data.get("pendingConfirm"));
        assertEquals("70.0", data.get("avgMatchScore"));
        recommended.setBatchId(11L);
        confirmed.setBatchId(11L);
        assertEquals(1, DashboardOverview.distinctBatchCount(List.of(recommended, confirmed)));
    }
}
