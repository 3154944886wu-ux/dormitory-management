package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OccupancyReleaseTest {

    @Test
    void onlyReleasesWhenStudentStillInAllocatedRoom() {
        assertTrue(OccupancyRelease.stillInAllocatedRoom(8L, 8L));
        assertFalse(OccupancyRelease.stillInAllocatedRoom(9L, 8L));
        assertFalse(OccupancyRelease.stillInAllocatedRoom(null, 8L));
        assertFalse(OccupancyRelease.stillInAllocatedRoom(8L, null));
    }

    @Test
    void incrementOnlyWhenMovingIntoANewRoom() {
        assertTrue(OccupancyRelease.needsRoomIncrement(null, 8L));
        assertTrue(OccupancyRelease.needsRoomIncrement(3L, 8L));
        assertFalse(OccupancyRelease.needsRoomIncrement(8L, 8L));
        assertFalse(OccupancyRelease.needsRoomIncrement(8L, null));
    }
}
