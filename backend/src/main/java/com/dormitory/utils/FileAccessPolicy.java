package com.dormitory.utils;

import java.util.Locale;

/**
 * 上传文件读取权限：管理员可看全部；宿管与学生仅能看已判定归属的附件。
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
