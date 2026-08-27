package com.dormitory.utils;

public final class BillingPeriod {

    private BillingPeriod() {
    }

    public static int[] yearMonth(String monthStr) {
        if (monthStr == null || monthStr.isBlank()) {
            throw new IllegalArgumentException("月份格式应为 YYYY-MM");
        }
        String[] parts = monthStr.trim().split("-");
        if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalArgumentException("月份格式应为 YYYY-MM");
        }
        try {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            if (month < 1 || month > 12) {
                throw new IllegalArgumentException("月份格式应为 YYYY-MM");
            }
            return new int[]{year, month};
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("月份格式应为 YYYY-MM");
        }
    }
}
