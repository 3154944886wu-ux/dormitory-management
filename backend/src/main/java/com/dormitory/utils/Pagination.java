package com.dormitory.utils;

import java.util.List;

public final class Pagination {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 200;

    private Pagination() {
    }

    public static int size(int size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    public static int page(int page) {
        return page <= 0 ? 1 : page;
    }

    public static int offset(int page, int size) {
        return (page(page) - 1) * size(size);
    }

    public static <T> List<T> slice(List<T> items, int page, int size) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        int start = offset(page, size);
        if (start >= items.size()) {
            return List.of();
        }
        return List.copyOf(items.subList(start, Math.min(start + size(size), items.size())));
    }
}
