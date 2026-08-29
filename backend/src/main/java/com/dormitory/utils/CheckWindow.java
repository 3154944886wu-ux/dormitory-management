package com.dormitory.utils;

import com.dormitory.model.CheckRule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * 归寝时间窗：业务日、打卡时段、未归截止。窗口可跨午夜（如 22:00–次日 00:30）。
 */
public final class CheckWindow {

    public static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private CheckWindow() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE);
    }

    public static LocalDate today() {
        return LocalDate.now(ZONE);
    }

    /**
     * 当前时刻所属归寝业务日：凌晨（时钟时间早于窗口开始）归属前一日。
     */
    public static LocalDate businessDate(LocalDateTime now, CheckRule rule) {
        LocalDate calendar = now.toLocalDate();
        if (rule == null || rule.getCheckStartTime() == null) {
            return calendar;
        }
        return isOvernightPortion(now.toLocalTime(), rule.getCheckStartTime())
                ? calendar.minusDays(1)
                : calendar;
    }

    /**
     * 学生端「今日」展示：凌晨看上一窗口，白天看当天日历。
     */
    public static LocalDate displayDate(LocalDateTime now, CheckRule rule) {
        return businessDate(now, rule);
    }

    public static boolean isOvernightPortion(LocalTime time, LocalTime windowStart) {
        if (time == null || windowStart == null) {
            return false;
        }
        int minutes = time.toSecondOfDay() / 60;
        int anchor = windowStart.toSecondOfDay() / 60;
        return minutes < anchor || (minutes == 0 && anchor > 0 && time.equals(LocalTime.MIDNIGHT));
    }

    public static int toWindowMinutes(LocalTime time, LocalTime windowStart) {
        int minutes = time.toSecondOfDay() / 60;
        int anchor = windowStart.toSecondOfDay() / 60;
        if (minutes <= anchor && !time.equals(windowStart)) {
            minutes += 24 * 60;
        }
        return minutes;
    }

    public static boolean isInCheckWindow(LocalTime time, CheckRule rule) {
        if (rule == null || time == null) {
            return false;
        }
        LocalTime start = rule.getCheckStartTime() != null ? rule.getCheckStartTime() : rule.getCheckEndTime();
        LocalTime absent = rule.getAbsentDeadline();
        if (start == null || absent == null) {
            return false;
        }
        int currentMin = toWindowMinutes(time, start);
        int startMin = toWindowMinutes(start, start);
        int absentMin = toWindowMinutes(absent, start);
        return currentMin >= startMin && currentMin <= absentMin;
    }

    public static boolean isPastAbsentDeadline(LocalTime time, CheckRule rule) {
        if (rule == null || time == null) {
            return false;
        }
        LocalTime start = rule.getCheckStartTime() != null ? rule.getCheckStartTime() : rule.getCheckEndTime();
        LocalTime absent = rule.getAbsentDeadline();
        if (start == null || absent == null) {
            return false;
        }
        return toWindowMinutes(time, start) > toWindowMinutes(absent, start);
    }

    /**
     * 0已归 / 1晚归 / 2未归。调用方先处理请假。
     */
    public static int statusOf(LocalTime time, CheckRule rule) {
        if (rule == null || time == null) {
            return 0;
        }
        LocalTime start = rule.getCheckStartTime() != null ? rule.getCheckStartTime() : rule.getCheckEndTime();
        LocalTime end = rule.getCheckEndTime();
        LocalTime absent = rule.getAbsentDeadline();
        if (start == null || end == null || absent == null) {
            return 0;
        }
        int currentMin = toWindowMinutes(time, start);
        int endMin = toWindowMinutes(end, start);
        int absentMin = toWindowMinutes(absent, start);
        if (currentMin > absentMin) {
            return 2;
        }
        if (currentMin > endMin) {
            return 1;
        }
        return 0;
    }

    public static LocalDateTime absentDeadlineInstant(LocalDate businessDate, CheckRule rule) {
        if (businessDate == null || rule == null) {
            return null;
        }
        LocalTime start = rule.getCheckStartTime() != null ? rule.getCheckStartTime() : rule.getCheckEndTime();
        LocalTime absent = rule.getAbsentDeadline();
        if (start == null || absent == null) {
            return null;
        }
        if (CheckAbsentWindow.crossesMidnight(start, absent)) {
            return businessDate.plusDays(1).atTime(absent);
        }
        return businessDate.atTime(absent);
    }

    public static boolean absentWindowClosed(LocalDate businessDate, CheckRule rule, LocalDateTime now) {
        LocalDateTime deadline = absentDeadlineInstant(businessDate, rule);
        return deadline != null && now != null && now.isAfter(deadline);
    }

    public static boolean appliesOn(LocalDate date, CheckRule rule) {
        if (rule == null || rule.getApplyDays() == null || rule.getApplyDays().isBlank()) {
            return true;
        }
        int dayOfWeek = date.getDayOfWeek().getValue();
        for (String day : rule.getApplyDays().split(",")) {
            if (!day.isBlank() && Integer.parseInt(day.trim()) == dayOfWeek) {
                return true;
            }
        }
        return false;
    }
}
