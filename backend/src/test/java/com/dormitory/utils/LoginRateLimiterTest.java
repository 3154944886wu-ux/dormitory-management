package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginRateLimiterTest {

    @Test
    void blocksAfterMaxFailuresAndClearsOnSuccess() {
        LoginRateLimiter limiter = new LoginRateLimiter();
        for (int i = 0; i < LoginRateLimiter.MAX_FAILURES - 1; i++) {
            limiter.recordFailure("admin");
            assertFalse(limiter.isBlocked("admin"));
        }
        limiter.recordFailure("admin");
        assertTrue(limiter.isBlocked("admin"));
        limiter.recordSuccess("admin");
        assertFalse(limiter.isBlocked("admin"));
    }
}
