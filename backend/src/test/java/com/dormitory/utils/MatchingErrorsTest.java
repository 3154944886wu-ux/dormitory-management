package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchingErrorsTest {

    @Test
    void doesNotDoublePrefix() {
        assertEquals("匹配失败: 批次内没有学生", MatchingErrors.forClient("批次内没有学生"));
        assertEquals("匹配失败: 批次内没有学生", MatchingErrors.forClient("匹配失败: 批次内没有学生"));
        assertEquals("匹配失败", MatchingErrors.forClient(null));
    }
}
