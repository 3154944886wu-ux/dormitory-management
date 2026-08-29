package com.dormitory.utils;

import com.dormitory.model.Room;
import com.dormitory.model.Student;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoomFillTest {

    @Test
    void occupancyByRoomCountsResidingStudentsOnly() {
        Student inA = student(1L, 1);
        Student alsoA = student(1L, 1);
        Student checkedOut = student(1L, 0);
        Student inB = student(2L, 1);
        Student noRoom = student(null, 1);
        Map<Long, Integer> map = RoomFill.occupancyByRoom(List.of(inA, alsoA, checkedOut, inB, noRoom));
        assertEquals(2, map.get(1L));
        assertEquals(1, map.get(2L));
        assertEquals(null, map.get(3L));
    }

    @Test
    void summarizeUsesLiveOccupancyNotStaleCounter() {
        Room empty = room(1L, 4, 9);
        Room partial = room(2L, 4, 9);
        Room full = room(3L, 4, 9);
        Map<Long, Integer> live = Map.of(2L, 2, 3L, 4);
        Map<String, Object> stats = RoomFill.summarize(List.of(empty, partial, full), live);
        assertEquals(3, stats.get("roomCount"));
        assertEquals(1, stats.get("freeRooms"));
        assertEquals(1, stats.get("partialRooms"));
        assertEquals(1, stats.get("fullRooms"));
        assertEquals(2, stats.get("occupiedRooms"));
    }

    @Test
    void occupancyRateIsOccupiedOverTotal() {
        assertEquals("0.0", RoomFill.occupancyRate(0, 0));
        assertEquals("50.0", RoomFill.occupancyRate(1, 2));
    }

    @Test
    void residingCountsStatusOne() {
        assertEquals(2, RoomFill.residing(List.of(
                student(1L, 1), student(2L, 0), student(3L, 1), student(4L, null))));
    }

    private static Student student(Long roomId, Integer status) {
        Student s = new Student();
        s.setRoomId(roomId);
        s.setStatus(status);
        return s;
    }

    private static Room room(Long id, int capacity, int staleCount) {
        Room room = new Room();
        room.setId(id);
        room.setCapacity(capacity);
        room.setCurrentCount(staleCount);
        return room;
    }
}
