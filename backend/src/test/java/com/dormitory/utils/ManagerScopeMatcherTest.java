package com.dormitory.utils;

import com.dormitory.model.ManagerScope;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagerScopeMatcherTest {

    private ManagerScope scope(Long buildingId, String className) {
        ManagerScope s = new ManagerScope();
        s.setBuildingId(buildingId);
        s.setClassName(className);
        return s;
    }

    @Test
    void buildingOnlyScopeMatchesAnyClassInThatBuilding() {
        List<ManagerScope> scopes = List.of(scope(5L, null));
        assertTrue(ManagerScopeMatcher.isVisible(scopes, 5L, "计科2301"));
        assertTrue(ManagerScopeMatcher.isVisible(scopes, 5L, "软工2302"));
        assertFalse(ManagerScopeMatcher.isVisible(scopes, 6L, "计科2301"));
        assertFalse(ManagerScopeMatcher.isVisible(scopes, null, "计科2301"));
    }

    @Test
    void classOnlyScopeMatchesThatClassInAnyBuilding() {
        List<ManagerScope> scopes = List.of(scope(null, "计科2301"));
        assertTrue(ManagerScopeMatcher.isVisible(scopes, 5L, "计科2301"));
        assertTrue(ManagerScopeMatcher.isVisible(scopes, null, "计科2301"));
        assertFalse(ManagerScopeMatcher.isVisible(scopes, 5L, "软工2302"));
    }

    @Test
    void bothBuildingAndClassMustMatch() {
        List<ManagerScope> scopes = List.of(scope(5L, "计科2301"));
        assertTrue(ManagerScopeMatcher.isVisible(scopes, 5L, "计科2301"));
        assertFalse(ManagerScopeMatcher.isVisible(scopes, 5L, "软工2302"));
        assertFalse(ManagerScopeMatcher.isVisible(scopes, 6L, "计科2301"));
    }

    @Test
    void visibleWhenAnyScopeMatches() {
        List<ManagerScope> scopes = List.of(scope(5L, null), scope(null, "软工2302"));
        assertTrue(ManagerScopeMatcher.isVisible(scopes, 5L, "计科2301")); // 命中第一条
        assertTrue(ManagerScopeMatcher.isVisible(scopes, 9L, "软工2302")); // 命中第二条
        assertFalse(ManagerScopeMatcher.isVisible(scopes, 9L, "机械2303"));
    }

    @Test
    void blankClassNameTreatedAsAllClasses() {
        List<ManagerScope> scopes = List.of(scope(5L, "   "));
        assertTrue(ManagerScopeMatcher.isVisible(scopes, 5L, "计科2301"));
    }

    @Test
    void emptyOrNullScopeSeesNothing() {
        assertFalse(ManagerScopeMatcher.isVisible(List.of(), 5L, "计科2301"));
        assertFalse(ManagerScopeMatcher.isVisible(null, 5L, "计科2301"));
    }
}
