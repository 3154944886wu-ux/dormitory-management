package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtSecretRulesTest {

    @Test
    void rejectsDefaultPlaceholder() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> JwtSecretRules.validate("change-me-in-local-config"));
        assertTrue(ex.getMessage().contains("默认占位符"));
    }

    @Test
    void rejectsBlankAndShortSecret() {
        assertThrows(IllegalStateException.class, () -> JwtSecretRules.validate(""));
        assertThrows(IllegalStateException.class, () -> JwtSecretRules.validate("short-secret"));
    }

    @Test
    void acceptsLongCustomSecret() {
        assertDoesNotThrow(() -> JwtSecretRules.validate("replace-with-a-long-random-secret-at-least-32"));
    }
}
