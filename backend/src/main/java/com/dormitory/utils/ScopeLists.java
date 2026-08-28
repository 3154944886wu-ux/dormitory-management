package com.dormitory.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 将班级名编码为 JSON 数组，避免 FIND_IN_SET 在班级名含逗号时误切分。
 */
public final class ScopeLists {

    private ScopeLists() {
    }

    public static String toJsonArray(List<String> names) {
        if (names == null || names.isEmpty()) {
            return null;
        }
        List<String> encoded = new ArrayList<>();
        for (String name : names) {
            if (name == null || name.isBlank()) {
                continue;
            }
            encoded.add("\"" + escape(name.trim()) + "\"");
        }
        if (encoded.isEmpty()) {
            return null;
        }
        return "[" + String.join(",", encoded) + "]";
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
