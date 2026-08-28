package com.dormitory.utils;

import java.util.Locale;

/**
 * 上传文件读取权限：管理员/宿管可看全部；学生仅能看自己有归属的附件。
 */
public final class FileAccessPolicy {

    private FileAccessPolicy() {
    }

    public static boolean canRead(String role, boolean allowed) {
        if (role == null || role.isBlank()) {
            return false;
        }
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        if ("ADMIN".equals(normalized)) {
            return true;
        }
        if ("MANAGER".equals(normalized) || "STUDENT".equals(normalized)) {
            return allowed;
        }
        return false;
    }
}
