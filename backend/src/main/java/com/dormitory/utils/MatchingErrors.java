package com.dormitory.utils;

public final class MatchingErrors {

    private MatchingErrors() {
    }

    public static String forClient(String message) {
        if (message == null || message.isBlank()) {
            return "匹配失败";
        }
        String trimmed = message.trim();
        if (trimmed.startsWith("匹配失败")) {
            return trimmed;
        }
        return "匹配失败: " + trimmed;
    }
}
