package com.dormitory.utils;

import com.dormitory.model.CheckRule;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销假：仅已批准（status=1）可确认返回。
 */
public final class LeaveReturn {

    private LeaveReturn() {
    }

    public static boolean canConfirm(Integer status) {
        return status != null && status == 1;
    }

    /**
     * 销假后从哪一天开始清掉请假打卡：窗内销假从当前业务日起；白天销假从日历今天起。
     */
    public static LocalDate firstRestorableDate(LocalDateTime now, CheckRule rule) {
        if (now == null) {
            return null;
        }
        if (rule != null && CheckWindow.isInCheckWindow(now.toLocalTime(), rule)) {
            return CheckWindow.businessDate(now, rule);
        }
        return now.toLocalDate();
    }
}
