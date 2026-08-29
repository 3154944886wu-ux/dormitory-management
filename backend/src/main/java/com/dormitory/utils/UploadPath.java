package com.dormitory.utils;

/**
 * 将公开 URL 规范为 uploads 目录下的相对路径，拒绝目录穿越。
 */
public final class UploadPath {

    private UploadPath() {
    }

    public static String relativeFile(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("文件路径无效");
        }
        String path = raw.trim().replace('\\', '/');
        if (path.startsWith("/uploads/")) {
            path = path.substring("/uploads/".length());
        } else if (path.startsWith("uploads/")) {
            path = path.substring("uploads/".length());
        }
        if (path.startsWith("/") || path.contains("..") || path.isBlank()) {
            throw new IllegalArgumentException("非法文件路径");
        }
        return path;
    }
}
