package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeedDemoGuardTest {

    @Test
    void requiresExplicitWipeFlag() {
        assertFalse(SeedDemoGuard.allow(true, false, List.of("local")));
        assertTrue(SeedDemoGuard.allow(true, true, List.of("local")));
    }

    @Test
    void blocksProductionProfilesEvenIfWipeEnabled() {
        assertFalse(SeedDemoGuard.allow(true, true, List.of("prod")));
        assertFalse(SeedDemoGuard.allow(true, true, List.of("production")));
        assertFalse(SeedDemoGuard.allow(true, true, List.of("staging")));
    }
}
