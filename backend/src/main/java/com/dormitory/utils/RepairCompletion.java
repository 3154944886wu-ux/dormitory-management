package com.dormitory.utils;

/**
 * 报修完成状态机：仅处理中(1)可完成；待处理须先开始处理。
 */
public final class RepairCompletion {

    private RepairCompletion() {
    }

    public static boolean canComplete(Integer status) {
        return status != null && status == 1;
    }

    /** 待处理可作废、处理中可关闭；已完成/已关闭不可再关。 */
    public static boolean canClose(Integer status) {
        return status != null && (status == 0 || status == 1);
    }
}
