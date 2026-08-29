package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckAbsentWindowTest {

    private static final LocalTime START = LocalTime.of(22, 0);
    private static final LocalTime ABSENT_MIDNIGHT = LocalTime.MIDNIGHT;
    private static final LocalTime ABSENT_SAME_DAY = LocalTime.of(23, 30);

    @Test
    void midnightCrossingWindowUsesYesterdayAfterDeadline() {
        LocalDateTime at0005 = LocalDateTime.of(2026, 6, 21, 0, 5);
        assertEquals(LocalDate.of(2026, 6, 20),
                CheckAbsentWindow.targetCheckDate(at0005, START, ABSENT_MIDNIGHT));
    }

    @Test
    void doesNotMarkTodayAbsentDuringDaytime() {
        LocalDateTime at1000 = LocalDateTime.of(2026, 6, 21, 10, 0);
        assertEquals(LocalDate.of(2026, 6, 20),
                CheckAbsentWindow.targetCheckDate(at1000, START, ABSENT_MIDNIGHT));
    }

    @Test
    void eveningBeforeMidnightIsStillInsideCheckWindow() {
        LocalDateTime at2230 = LocalDateTime.of(2026, 6, 20, 22, 30);
        assertNull(CheckAbsentWindow.targetCheckDate(at2230, START, ABSENT_MIDNIGHT));
    }

    @Test
    void sameDayDeadlineUsesTonightAfterCutoff() {
        LocalDateTime at2345 = LocalDateTime.of(2026, 6, 20, 23, 45);
        assertEquals(LocalDate.of(2026, 6, 20),
                CheckAbsentWindow.targetCheckDate(at2345, LocalTime.of(21, 0), ABSENT_SAME_DAY));
    }

    @Test
    void sameDayDeadlineMorningStillTargetsLastNight() {
        LocalDateTime at0800 = LocalDateTime.of(2026, 6, 21, 8, 0);
        assertEquals(LocalDate.of(2026, 6, 20),
                CheckAbsentWindow.targetCheckDate(at0800, LocalTime.of(21, 0), ABSENT_SAME_DAY));
    }

    @Test
    void detectsMidnightCrossing() {
        assertTrue(CheckAbsentWindow.crossesMidnight(START, ABSENT_MIDNIGHT));
    }

    @Test
    void thirtyPastMidnightStillOpenAt0005() {
        LocalDateTime at0005 = LocalDateTime.of(2026, 6, 21, 0, 5);
        assertNull(CheckAbsentWindow.targetCheckDate(at0005, START, LocalTime.of(0, 30)));
    }

    @Test
    void thirtyPastMidnightMarksYesterdayAfterCutoff() {
        LocalDateTime at0031 = LocalDateTime.of(2026, 6, 21, 0, 31);
        assertEquals(LocalDate.of(2026, 6, 20),
                CheckAbsentWindow.targetCheckDate(at0031, START, LocalTime.of(0, 30)));
    }
}
