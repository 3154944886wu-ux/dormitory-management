package com.dormitory.utils;

import com.dormitory.model.Bed;
import com.dormitory.model.Room;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BedLayoutTest {

    private Room room(Integer capacity, Integer window, Integer corridor) {
        Room r = new Room();
        r.setCapacity(capacity);
        r.setWindowBedsCount(window);
        r.setCorridorBedsCount(corridor);
        return r;
    }

    @Test
    void generatesWindowThenCorridorBedsByCounts() {
        List<Bed> beds = BedLayout.forRoom(room(4, 2, 2));
        assertEquals(4, beds.size());
        assertEquals("A", beds.get(0).getBedNumber());
        assertEquals("window", beds.get(0).getBedType());
        assertEquals("window", beds.get(1).getBedType());
        assertEquals("corridor", beds.get(2).getBedType());
        assertEquals("D", beds.get(3).getBedNumber());
        assertEquals("corridor", beds.get(3).getBedType());
        assertTrue(beds.stream().allMatch(b -> b.getIsOccupied() != null && b.getIsOccupied() == 0));
    }

    @Test
    void defaultsToCorridorWhenCountsMissing() {
        List<Bed> beds = BedLayout.forRoom(room(4, null, null));
        assertEquals(4, beds.size());
        assertTrue(beds.stream().allMatch(b -> "corridor".equals(b.getBedType())));
    }

    @Test
    void capacityIsAuthoritativeAndWindowCountIsClamped() {
        // 容量为 2，靠窗数配置为 5（超出容量）→ 只生成 2 张床且都算靠窗
        List<Bed> beds = BedLayout.forRoom(room(2, 5, 0));
        assertEquals(2, beds.size());
        assertTrue(beds.stream().allMatch(b -> "window".equals(b.getBedType())));
    }

    @Test
    void fallsBackToWindowPlusCorridorWhenNoCapacity() {
        List<Bed> beds = BedLayout.forRoom(room(null, 1, 3));
        assertEquals(4, beds.size());
        assertEquals("window", beds.get(0).getBedType());
        assertEquals("corridor", beds.get(1).getBedType());
        assertEquals("corridor", beds.get(3).getBedType());
    }

    @Test
    void emptyWhenNoCapacityAndNoCounts() {
        assertTrue(BedLayout.forRoom(room(null, null, null)).isEmpty());
        assertTrue(BedLayout.forRoom(room(0, null, null)).isEmpty());
        assertTrue(BedLayout.forRoom(null).isEmpty());
    }
}
