package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeeTotalTest {

    @Test
    void sumsBothFees() {
        assertEquals(new BigDecimal("11.50"),
                FeeTotal.of(new BigDecimal("5.00"), new BigDecimal("6.50")));
    }

    @Test
    void treatsNullFeeAsZero() {
        assertEquals(new BigDecimal("5.00"), FeeTotal.of(new BigDecimal("5.00"), null));
        assertEquals(new BigDecimal("6.50"), FeeTotal.of(null, new BigDecimal("6.50")));
        assertEquals(BigDecimal.ZERO, FeeTotal.of(null, null));
    }

    @Test
    void rejectsNegativeFee() {
        assertThrows(IllegalArgumentException.class,
                () -> FeeTotal.of(new BigDecimal("-1"), BigDecimal.ZERO));
    }
}
