package com.dormitory.utils;

import com.dormitory.model.CheckRule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaveReturnRestoreTest {

    private static CheckRule overnight() {
        CheckRule rule = new CheckRule();
        rule.setCheckStartTime(LocalTime.of(22, 0));
        rule.setCheckEndTime(LocalTime.of(23, 0));
        rule.setAbsentDeadline(LocalTime.of(0, 30));
        return rule;
    }

    @Test
    void daytimeReturnRestoresFromCalendarToday() {
        LocalDateTime afternoon = LocalDateTime.of(2026, 8, 26, 15, 0);
        assertEquals(LocalDate.of(2026, 8, 26), LeaveReturn.firstRestorableDate(afternoon, overnight()));
    }

    @Test
    void overnightReturnRestoresCurrentBusinessNight() {
        LocalDateTime afterMidnight = LocalDateTime.of(2026, 8, 27, 0, 10);
        assertEquals(LocalDate.of(2026, 8, 26), LeaveReturn.firstRestorableDate(afterMidnight, overnight()));
    }
}
