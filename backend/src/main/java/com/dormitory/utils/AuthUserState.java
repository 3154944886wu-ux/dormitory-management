package com.dormitory.utils;

import com.dormitory.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public final class AuthUserState {

    private AuthUserState() {
    }

    public static boolean isUsable(User user) {
        return user != null && user.getStatus() != null && user.getStatus() == 1;
    }

    public static String databaseRole(User user) {
        if (!isUsable(user) || user.getRole() == null || user.getRole().isBlank()) {
            return null;
        }
        String role = user.getRole().trim().toUpperCase();
        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        return role.isBlank() ? null : role;
    }

    public static List<GrantedAuthority> authorities(User user) {
        String role = databaseRole(user);
        if (role == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
