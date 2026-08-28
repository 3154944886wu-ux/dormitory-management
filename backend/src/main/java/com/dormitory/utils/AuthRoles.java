package com.dormitory.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public final class AuthRoles {

    private AuthRoles() {
    }

    public static boolean has(Authentication auth, String role) {
        if (auth == null || role == null || auth.getAuthorities() == null) {
            return false;
        }
        String expected = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(expected::equalsIgnoreCase);
    }

    public static boolean isManagerOnly(Authentication auth) {
        return has(auth, "MANAGER") && !has(auth, "ADMIN");
    }
}
