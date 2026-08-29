package com.dormitory.utils;

import com.dormitory.model.ManagerScope;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagerScopeJsonTest {

    @Test
    void encodesPerRowPairsNotFlattenedLists() {
        ManagerScope a = new ManagerScope();
        a.setBuildingId(5L);
        a.setClassName("计科2301");
        ManagerScope b = new ManagerScope();
        b.setBuildingId(9L);
        b.setClassName(null);
        String json = ManagerScopeJson.encode(List.of(a, b));
        assertTrue(json.contains("\"buildingId\":5"));
        assertTrue(json.contains("计科2301"));
        assertTrue(json.contains("\"buildingId\":9"));
        assertFalse(json.contains("5,9"));
    }

    @Test
    void emptyScopesEncodeAsNull() {
        assertEquals(null, ManagerScopeJson.encode(List.of()));
        assertEquals(null, ManagerScopeJson.encode(null));
    }
}
