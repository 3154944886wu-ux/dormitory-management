package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BillingPeriodTest {

    @Test
    void parsesYearMonth() {
        assertArrayEquals(new int[]{2026, 8}, BillingPeriod.yearMonth("2026-08"));
    }

    @Test
    void rejectsShortOrInvalidMonth() {
        IllegalArgumentException blank = assertThrows(IllegalArgumentException.class,
                () -> BillingPeriod.yearMonth("2026"));
        assertEquals("月份格式应为 YYYY-MM", blank.getMessage());
        assertThrows(IllegalArgumentException.class, () -> BillingPeriod.yearMonth("2026-13"));
        assertThrows(IllegalArgumentException.class, () -> BillingPeriod.yearMonth(null));
        assertThrows(IllegalArgumentException.class, () -> BillingPeriod.yearMonth(""));
    }
}
