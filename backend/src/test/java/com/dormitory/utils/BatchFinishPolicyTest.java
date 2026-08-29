package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BatchFinishPolicyTest {

    @Test
    void finishesOnlyWhenNoRecommendedRemain() {
        assertTrue(BatchFinishPolicy.shouldMarkFinished(0));
        assertFalse(BatchFinishPolicy.shouldMarkFinished(1));
        assertFalse(BatchFinishPolicy.shouldMarkFinished(3));
    }
}
