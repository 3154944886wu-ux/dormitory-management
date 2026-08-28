package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenderMatcherTest {

    @Test
    void maleBuildingAcceptsMaleStudentRegardlessOfCase() {
        assertTrue(GenderMatcher.isCompatible("男", "MALE"));
        assertTrue(GenderMatcher.isCompatible("男", "male"));
        assertTrue(GenderMatcher.isCompatible("male", "MALE"));
    }

    @Test
    void femaleBuildingRejectsMaleStudent() {
        assertFalse(GenderMatcher.isCompatible("男", "FEMALE"));
        assertFalse(GenderMatcher.isCompatible("男", "female"));
    }

    @Test
    void femaleBuildingAcceptsFemaleStudent() {
        assertTrue(GenderMatcher.isCompatible("女", "FEMALE"));
        assertTrue(GenderMatcher.isCompatible("女", "female"));
        assertTrue(GenderMatcher.isCompatible("FEMALE", "FEMALE"));
    }

    @Test
    void mixedOrBlankBuildingAcceptsAnyone() {
        assertTrue(GenderMatcher.isCompatible("男", "MIXED"));
        assertTrue(GenderMatcher.isCompatible("女", "mixed"));
        assertTrue(GenderMatcher.isCompatible("男", null));
        assertTrue(GenderMatcher.isCompatible("女", "  "));
    }
}
