package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepairCompletionTest {

    @Test
    void pendingAndProcessingCanComplete() {
        assertTrue(RepairCompletion.canComplete(0));
        assertTrue(RepairCompletion.canComplete(1));
    }

    @Test
    void completedAndClosedCannotComplete() {
        assertFalse(RepairCompletion.canComplete(2));
        assertFalse(RepairCompletion.canComplete(3));
        assertFalse(RepairCompletion.canComplete(null));
    }
}
