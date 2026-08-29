package com.dormitory.utils;

import com.dormitory.model.CheckRule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeaveCoverageTest {

    private static CheckRule overnight() {
        CheckRule rule = new CheckRule();
        rule.setCheckStartTime(LocalTime.of(22, 0));
        rule.setCheckEndTime(LocalTime.of(23, 0));
        rule.setAbsentDeadline(LocalTime.of(0, 30));
        return rule;
    }

    @Test
    void daytimeShortLeaveDoesNotCoverNightWindow() {
        CheckRule rule = overnight();
        LocalDateTime start = LocalDateTime.of(2026, 8, 29, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 29, 17, 0);
        assertFalse(LeaveCoverage.coversBusinessDate(start, end, LocalDate.of(2026, 8, 29), rule));
    }

    @Test
    void leaveUntilAfterMidnightCoversPreviousNightOnly() {
        CheckRule rule = overnight();
        LocalDateTime start = LocalDateTime.of(2026, 8, 28, 21, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 30, 0, 15);
        assertTrue(LeaveCoverage.coversBusinessDate(start, end, LocalDate.of(2026, 8, 29), rule));
        assertFalse(LeaveCoverage.coversBusinessDate(start, end, LocalDate.of(2026, 8, 30), rule));
    }
}
