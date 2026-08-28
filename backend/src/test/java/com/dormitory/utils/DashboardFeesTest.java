package com.dormitory.utils;

import com.dormitory.model.UtilityFee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardFeesTest {

    @Test
    void sumsWaterAndElectricAndPaidSplit() {
        UtilityFee unpaid = new UtilityFee();
        unpaid.setWaterFee(new BigDecimal("1.50"));
        unpaid.setElectricityFee(new BigDecimal("2.50"));
        unpaid.setTotalFee(new BigDecimal("4.00"));
        unpaid.setStatus(0);
        UtilityFee paid = new UtilityFee();
        paid.setWaterFee(new BigDecimal("3.00"));
        paid.setElectricityFee(new BigDecimal("1.00"));
        paid.setTotalFee(new BigDecimal("4.00"));
        paid.setStatus(1);
        Map<String, String> stats = DashboardFees.summarize(List.of(unpaid, paid));
        assertEquals("4.50", stats.get("waterFee"));
        assertEquals("3.50", stats.get("electricFee"));
        assertEquals("4.00", stats.get("paidAmount"));
        assertEquals("4.00", stats.get("unpaidAmount"));
    }
}
