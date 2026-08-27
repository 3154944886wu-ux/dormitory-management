package com.dormitory.utils;

import com.dormitory.model.UtilityFee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DashboardFees {

    private DashboardFees() {
    }

    public static Map<String, String> summarize(List<UtilityFee> fees) {
        BigDecimal water = BigDecimal.ZERO;
        BigDecimal electric = BigDecimal.ZERO;
        BigDecimal paid = BigDecimal.ZERO;
        BigDecimal unpaid = BigDecimal.ZERO;
        if (fees != null) {
            for (UtilityFee fee : fees) {
                if (fee == null) {
                    continue;
                }
                water = water.add(nvl(fee.getWaterFee()));
                electric = electric.add(nvl(fee.getElectricityFee()));
                BigDecimal total = nvl(fee.getTotalFee());
                if (fee.getStatus() != null && fee.getStatus() == 1) {
                    paid = paid.add(total);
                } else {
                    unpaid = unpaid.add(total);
                }
            }
        }
        Map<String, String> stats = new HashMap<>();
        stats.put("waterFee", money(water));
        stats.put("electricFee", money(electric));
        stats.put("paidAmount", money(paid));
        stats.put("unpaidAmount", money(unpaid));
        return stats;
    }

    private static BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static String money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
