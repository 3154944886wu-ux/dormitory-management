package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SurveyAnswersTest {

    @Test
    void requireIdReadsNumericValue() {
        Map<String, Object> row = new HashMap<>();
        row.put("qId", 12);
        row.put("optionId", "8");
        assertEquals(12L, SurveyAnswers.requireId(row, "qId"));
        assertEquals(8L, SurveyAnswers.requireId(row, "optionId"));
    }

    @Test
    void requireIdRejectsNull() {
        Map<String, Object> row = new HashMap<>();
        row.put("qId", null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> SurveyAnswers.requireId(row, "qId"));
        assertEquals("题目或选项不能为空", ex.getMessage());
        assertThrows(IllegalArgumentException.class, () -> SurveyAnswers.requireId(null, "qId"));
    }
}
