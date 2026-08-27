package com.dormitory.utils;

/**
 * 报修完成状态机：0待处理 / 1处理中可完成；2已完成、3已关闭不可再完成。
 */
public final class RepairCompletion {

    private RepairCompletion() {
    }

    public static boolean canComplete(Integer status) {
        return status != null && (status == 0 || status == 1);
    }
}
