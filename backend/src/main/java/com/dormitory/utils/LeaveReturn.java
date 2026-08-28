package com.dormitory.utils;

/**
 * 销假：仅已批准（status=1）可确认返回。
 */
public final class LeaveReturn {

    private LeaveReturn() {
    }

    public static boolean canConfirm(Integer status) {
        return status != null && status == 1;
    }
}
