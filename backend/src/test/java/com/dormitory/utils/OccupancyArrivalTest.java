package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OccupancyArrivalTest {

    @Test
    void morningCheckInDoesNotCountAsLastNightResident() {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 27, 10, 0);
        assertFalse(OccupancyArrival.residingOnBusinessDate(checkIn, LocalDate.of(2026, 8, 26)));
        assertTrue(OccupancyArrival.residingOnBusinessDate(checkIn, LocalDate.of(2026, 8, 27)));
    }

    @Test
    void unknownCheckInDateTreatedAsResiding() {
        assertTrue(OccupancyArrival.residingOnBusinessDate(null, LocalDate.of(2026, 8, 26)));
    }
}
