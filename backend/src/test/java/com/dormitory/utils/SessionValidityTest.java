package com.dormitory.utils;

import com.dormitory.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionValidityTest {

    @Test
    void disabledOrMissingUserIsInactive() {
        assertFalse(SessionValidity.isActive(null));
        User disabled = new User();
        disabled.setStatus(0);
        assertFalse(SessionValidity.isActive(disabled));
    }

    @Test
    void nullOrOneStatusIsActive() {
        User user = new User();
        assertTrue(SessionValidity.isActive(user));
        user.setStatus(1);
        assertTrue(SessionValidity.isActive(user));
    }

    @Test
    void passwordVersionChangesAfterPasswordChange() {
        String oldHash = "$2a$10$oldhashvalue0000000000000000000000000000000000";
        String newHash = "$2a$10$newhashvalue0000000000000000000000000000000000";
        String tokenPv = PasswordVersions.from(oldHash);
        assertTrue(SessionValidity.passwordVersionMatches(oldHash, tokenPv));
        assertFalse(SessionValidity.passwordVersionMatches(newHash, tokenPv));
        assertFalse(SessionValidity.passwordVersionMatches(oldHash, null));
        assertFalse(SessionValidity.passwordVersionMatches(oldHash, ""));
    }

    @Test
    void normalizesRolePrefix() {
        assertEquals("MANAGER", SessionValidity.normalizedRole("ROLE_MANAGER"));
        assertEquals("STUDENT", SessionValidity.normalizedRole("student"));
    }
}
