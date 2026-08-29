package com.dormitory.utils;

import com.dormitory.model.CheckRule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 请假区间是否覆盖某个归寝业务日的打卡窗。
 */
public final class LeaveCoverage {

    private LeaveCoverage() {
    }

    public static boolean coversBusinessDate(LocalDateTime leaveStart, LocalDateTime leaveEnd,
                                            LocalDate businessDate, CheckRule rule) {
        if (leaveStart == null || leaveEnd == null || businessDate == null) {
            return false;
        }
        LocalDateTime[] window = checkWindow(businessDate, rule);
        return !leaveStart.isAfter(window[1]) && !leaveEnd.isBefore(window[0]);
    }

    private static LocalDateTime[] checkWindow(LocalDate businessDate, CheckRule rule) {
        LocalTime start = rule == null ? null : rule.getCheckStartTime();
        LocalTime absent = rule == null ? null : rule.getAbsentDeadline();
        if (start == null || absent == null) {
            return new LocalDateTime[]{businessDate.atStartOfDay(), businessDate.plusDays(1).atStartOfDay()};
        }
        if (CheckAbsentWindow.crossesMidnight(start, absent)) {
            return new LocalDateTime[]{businessDate.atTime(start), businessDate.plusDays(1).atTime(absent)};
        }
        return new LocalDateTime[]{businessDate.atTime(start), businessDate.atTime(absent)};
    }
}
