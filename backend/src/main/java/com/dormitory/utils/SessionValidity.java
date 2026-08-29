package com.dormitory.utils;

import com.dormitory.model.User;

import java.util.Locale;
import java.util.Objects;

/**
 * 登录后每次请求复查账号是否仍可使用：禁用、角色变更、改密后旧 token 均失效。
 */
public final class SessionValidity {

    private SessionValidity() {
    }

    public static boolean isActive(User user) {
        if (user == null) {
            return false;
        }
        return user.getStatus() == null || user.getStatus() != 0;
    }

    public static String normalizedRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring(5);
        }
        return normalized.isBlank() ? null : normalized;
    }

    public static boolean passwordVersionMatches(String encodedPassword, String tokenVersion) {
        if (tokenVersion == null || tokenVersion.isBlank()) {
            return false;
        }
        return Objects.equals(PasswordVersions.from(encodedPassword), tokenVersion);
    }
}
