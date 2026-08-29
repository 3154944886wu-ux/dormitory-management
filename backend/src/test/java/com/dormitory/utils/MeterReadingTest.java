package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MeterReadingTest {

    @Test
    void subtractsEndMinusStart() {
        assertEquals(new BigDecimal("12.5"),
                MeterReading.usage(new BigDecimal("10.0"), new BigDecimal("22.5")));
    }

    @Test
    void rejectsNegativeUsage() {
        assertThrows(IllegalArgumentException.class,
                () -> MeterReading.usage(new BigDecimal("20"), new BigDecimal("10")));
    }

    @Test
    void nullReadingsYieldNullUsage() {
        assertNull(MeterReading.usage(null, BigDecimal.ONE));
        assertNull(MeterReading.usage(BigDecimal.ONE, null));
    }
}
