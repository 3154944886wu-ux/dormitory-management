package com.dormitory.utils;

import com.dormitory.model.CheckRule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckWindowTest {

    private static CheckRule overnightRule() {
        CheckRule rule = new CheckRule();
        rule.setCheckStartTime(LocalTime.of(22, 0));
        rule.setCheckEndTime(LocalTime.of(23, 0));
        rule.setAbsentDeadline(LocalTime.MIDNIGHT);
        rule.setApplyDays("1,2,3,4,5,6,7");
        return rule;
    }

    @Test
    void businessDateBeforeStartIsPreviousCalendarDay() {
        CheckRule rule = overnightRule();
        LocalDateTime morning = LocalDateTime.of(2026, 8, 28, 3, 30);
        assertEquals(LocalDate.of(2026, 8, 27), CheckWindow.businessDate(morning, rule));
        LocalDateTime evening = LocalDateTime.of(2026, 8, 28, 22, 10);
        assertEquals(LocalDate.of(2026, 8, 28), CheckWindow.businessDate(evening, rule));
    }

    @Test
    void inWindowFromStartUntilAbsentDeadline() {
        CheckRule rule = overnightRule();
        assertTrue(CheckWindow.isInCheckWindow(LocalTime.of(22, 0), rule));
        assertTrue(CheckWindow.isInCheckWindow(LocalTime.of(23, 30), rule));
        assertTrue(CheckWindow.isInCheckWindow(LocalTime.MIDNIGHT, rule));
        assertFalse(CheckWindow.isInCheckWindow(LocalTime.of(0, 1), rule));
        assertFalse(CheckWindow.isInCheckWindow(LocalTime.of(3, 30), rule));
        assertFalse(CheckWindow.isInCheckWindow(LocalTime.of(21, 0), rule));
    }

    @Test
    void pastAbsentAfterMidnight() {
        CheckRule rule = overnightRule();
        assertFalse(CheckWindow.isPastAbsentDeadline(LocalTime.of(23, 59), rule));
        assertFalse(CheckWindow.isPastAbsentDeadline(LocalTime.MIDNIGHT, rule));
        assertTrue(CheckWindow.isPastAbsentDeadline(LocalTime.of(0, 1), rule));
        assertTrue(CheckWindow.isPastAbsentDeadline(LocalTime.of(3, 30), rule));
    }

    @Test
    void statusNormalLateAbsent() {
        CheckRule rule = overnightRule();
        assertEquals(0, CheckWindow.statusOf(LocalTime.of(22, 30), rule));
        assertEquals(1, CheckWindow.statusOf(LocalTime.of(23, 30), rule));
        assertEquals(2, CheckWindow.statusOf(LocalTime.of(0, 10), rule));
    }
}
