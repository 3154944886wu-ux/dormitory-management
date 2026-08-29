package com.dormitory.utils;

import java.util.Map;

public final class SurveyAnswers {

    private SurveyAnswers() {
    }

    public static long requireId(Map<String, ?> row, String key) {
        if (row == null || key == null) {
            throw new IllegalArgumentException("题目或选项不能为空");
        }
        Object value = row.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("题目或选项不能为空");
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("题目或选项不能为空");
        }
    }
}
