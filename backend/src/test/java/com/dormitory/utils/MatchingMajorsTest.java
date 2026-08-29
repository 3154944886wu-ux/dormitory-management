package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchingMajorsTest {

    @Test
    void mixAllowedIgnoresMajor() {
        assertTrue(MatchingMajors.canGroup(1, 10, 20));
        assertTrue(MatchingMajors.canGroup(null, 10, 20));
    }

    @Test
    void mixForbiddenRequiresSameMajor() {
        assertTrue(MatchingMajors.canGroup(0, 10, 10));
        assertFalse(MatchingMajors.canGroup(0, 10, 20));
        assertTrue(MatchingMajors.canGroup(0, null, null));
    }
}
