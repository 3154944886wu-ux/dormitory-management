package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScopeListsTest {

    @Test
    void jsonArrayPreservesCommaInsideClassName() {
        String json = ScopeLists.toJsonArray(List.of("计科2023级1班,实验班", "软工2302"));
        assertTrue(json.contains("计科2023级1班,实验班"));
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
    }

    @Test
    void emptyOrNullBecomesNull() {
        assertNull(ScopeLists.toJsonArray(List.of()));
        assertNull(ScopeLists.toJsonArray(null));
    }

    @Test
    void skipsBlankNames() {
        String json = ScopeLists.toJsonArray(List.of("  ", "软工2302"));
        assertEquals("[\"软工2302\"]", json);
    }
}
