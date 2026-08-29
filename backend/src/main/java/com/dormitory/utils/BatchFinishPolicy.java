package com.dormitory.utils;

/**
 * 自动截止批次：仍有 recommended 分配时不得标为 finished。
 */
public final class BatchFinishPolicy {

    private BatchFinishPolicy() {
    }

    public static boolean shouldMarkFinished(int remainingRecommended) {
        return remainingRecommended == 0;
    }
}
