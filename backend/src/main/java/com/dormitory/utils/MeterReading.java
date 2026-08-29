package com.dormitory.utils;

import java.math.BigDecimal;

public final class MeterReading {

    private MeterReading() {
    }

    public static BigDecimal usage(BigDecimal start, BigDecimal end) {
        if (start == null || end == null) {
            return null;
        }
        if (end.compareTo(start) < 0) {
            throw new IllegalArgumentException("止码不能小于起码");
        }
        return end.subtract(start);
    }
}
