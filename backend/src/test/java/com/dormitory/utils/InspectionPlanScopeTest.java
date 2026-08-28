package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InspectionPlanScopeTest {

    @Test
    void visibleWhenBuildingIntersects() {
        assertTrue(InspectionPlanScope.visibleToManager("1,2,3", Set.of(2L)));
        assertFalse(InspectionPlanScope.visibleToManager("2,3", Set.of(1L)));
        assertFalse(InspectionPlanScope.visibleToManager("1", List.of()));
        assertFalse(InspectionPlanScope.visibleToManager("", Set.of(1L)));
    }
}
