package com.dormitory.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 跨午夜归寝窗口：未归记录必须记在「当晚」的 check_date，白天不得给「今天」灌未归。
 */
public final class CheckAbsentWindow {

    private CheckAbsentWindow() {
    }

    public static boolean crossesMidnight(LocalTime windowStart, LocalTime absentDeadline) {
        if (windowStart == null || absentDeadline == null) {
            return false;
        }
        return !absentDeadline.isAfter(windowStart);
    }

    /**
     * @return 应记未归的日期；尚未到未归截止则返回 null
     */
    public static LocalDate targetCheckDate(LocalDateTime now, LocalTime windowStart, LocalTime absentDeadline) {
        if (now == null || windowStart == null || absentDeadline == null) {
            return null;
        }
        LocalTime current = now.toLocalTime();
        LocalDate today = now.toLocalDate();
        if (crossesMidnight(windowStart, absentDeadline)) {
            if (!current.isBefore(windowStart)) {
                return null;
            }
            return today.minusDays(1);
        }
        if (!current.isBefore(windowStart) && !current.isBefore(absentDeadline)) {
            return today;
        }
        if (current.isBefore(windowStart)) {
            return today.minusDays(1);
        }
        return null;
    }
}
