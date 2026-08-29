package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaginationTest {

    @Test
    void invalidSizeFallsBackToDefault() {
        assertEquals(20, Pagination.size(0));
        assertEquals(20, Pagination.size(-3));
        assertEquals(20, Pagination.size(20));
        assertEquals(50, Pagination.size(50));
        assertEquals(200, Pagination.size(500));
    }

    @Test
    void invalidPageFallsBackToFirst() {
        assertEquals(1, Pagination.page(0));
        assertEquals(1, Pagination.page(-1));
        assertEquals(3, Pagination.page(3));
    }

    @Test
    void offsetUsesNormalizedPageAndSize() {
        assertEquals(0, Pagination.offset(0, 0));
        assertEquals(40, Pagination.offset(3, 20));
    }

    @Test
    void sliceUsesNormalizedPageAndSize() {
        assertEquals(java.util.List.of(1, 2), Pagination.slice(java.util.List.of(1, 2, 3, 4, 5), 1, 2));
        assertEquals(java.util.List.of(5), Pagination.slice(java.util.List.of(1, 2, 3, 4, 5), 3, 2));
        assertEquals(java.util.List.of(), Pagination.slice(java.util.List.of(1, 2), 9, 20));
        assertEquals(java.util.List.of(1, 2, 3), Pagination.slice(java.util.List.of(1, 2, 3), 0, 0));
    }
}
