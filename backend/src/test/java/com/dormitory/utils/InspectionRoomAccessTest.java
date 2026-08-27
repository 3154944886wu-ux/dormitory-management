package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InspectionRoomAccessTest {

    @Test
    void adminAndManagerCanViewAnyRoom() {
        assertTrue(InspectionRoomAccess.canView("ADMIN", 9L, null));
        assertTrue(InspectionRoomAccess.canView("MANAGER", 9L, 1L));
        assertTrue(InspectionRoomAccess.canView("ROLE_ADMIN", 3L, 99L));
    }

    @Test
    void studentCanViewOwnRoomOnly() {
        assertTrue(InspectionRoomAccess.canView("STUDENT", 1L, 1L));
        assertTrue(InspectionRoomAccess.canView("student", 8L, 8L));
        assertFalse(InspectionRoomAccess.canView("STUDENT", 1L, 2L));
        assertFalse(InspectionRoomAccess.canView("STUDENT", 1L, null));
        assertFalse(InspectionRoomAccess.canView("STUDENT", null, 1L));
    }

    @Test
    void unknownOrBlankRoleDenied() {
        assertFalse(InspectionRoomAccess.canView(null, 1L, 1L));
        assertFalse(InspectionRoomAccess.canView("", 1L, 1L));
        assertFalse(InspectionRoomAccess.canView("GUEST", 1L, 1L));
    }
}
