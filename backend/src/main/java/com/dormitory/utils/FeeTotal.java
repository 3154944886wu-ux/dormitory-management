package com.dormitory.utils;

import java.math.BigDecimal;

/**
 * 水电费合计：空值按 0 处理，避免部分更新时 NPE。
 */
public final class FeeTotal {

    private FeeTotal() {
    }

    public static BigDecimal of(BigDecimal electricityFee, BigDecimal waterFee) {
        BigDecimal electric = electricityFee == null ? BigDecimal.ZERO : electricityFee;
        BigDecimal water = waterFee == null ? BigDecimal.ZERO : waterFee;
        return electric.add(water);
    }
}
