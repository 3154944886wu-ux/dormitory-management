package com.dormitory.utils;

import com.dormitory.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthUserStateTest {

    @Test
    void disabledUserIsNotUsable() {
        User user = user("ADMIN", 0);
        assertFalse(AuthUserState.isUsable(user));
        assertTrue(AuthUserState.authorities(user).isEmpty());
    }

    @Test
    void missingUserIsNotUsable() {
        assertFalse(AuthUserState.isUsable(null));
        assertTrue(AuthUserState.authorities(null).isEmpty());
    }

    @Test
    void authoritiesComeFromDatabaseRoleNotTokenClaim() {
        User user = user("MANAGER", 1);
        List<GrantedAuthority> authorities = AuthUserState.authorities(user);
        assertEquals(1, authorities.size());
        assertEquals("ROLE_MANAGER", authorities.get(0).getAuthority());
        assertEquals("MANAGER", AuthUserState.databaseRole(user));
    }

    private User user(String role, int status) {
        User user = new User();
        user.setUsername("demo");
        user.setRole(role);
        user.setStatus(status);
        return user;
    }
}
