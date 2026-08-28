package com.dormitory.utils;

import com.dormitory.model.Room;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchingCapacityAndGroupsTest {

    @Test
    void mostCommonCapacityPrefersMajorityThenSmaller() {
        assertEquals(2, MatchingCapacity.mostCommon(List.of(room(2), room(2), room(4)), 4));
        assertEquals(4, MatchingCapacity.mostCommon(List.of(), 4));
    }

    @Test
    void leftoverDoesNotOverflowLastGroup() {
        List<List<Integer>> groups = new ArrayList<>();
        groups.add(new ArrayList<>(List.of(1, 2, 3, 4)));
        MatchingGroups.appendLeftovers(groups, List.of(5), 4);
        assertEquals(2, groups.size());
        assertEquals(List.of(1, 2, 3, 4), groups.get(0));
        assertEquals(List.of(5), groups.get(1));
    }

    @Test
    void leftoverFillsRemainingSlotsThenNewGroup() {
        List<List<Integer>> groups = new ArrayList<>();
        groups.add(new ArrayList<>(List.of(1, 2, 3)));
        MatchingGroups.appendLeftovers(groups, List.of(4, 5, 6), 4);
        assertEquals(List.of(1, 2, 3, 4), groups.get(0));
        assertEquals(List.of(5, 6), groups.get(1));
    }

    private static Room room(int capacity) {
        Room room = new Room();
        room.setCapacity(capacity);
        return room;
    }
}
