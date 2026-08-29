package com.dormitory.utils;

import com.dormitory.model.Room;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 按房源池最常见容量组队，避免写死 4 人间。
 */
public final class MatchingCapacity {

    private MatchingCapacity() {
    }

    public static int mostCommon(List<Room> rooms, int defaultCapacity) {
        if (rooms == null || rooms.isEmpty()) {
            return defaultCapacity;
        }
        Map<Integer, Integer> counts = new HashMap<>();
        int best = defaultCapacity;
        int bestCount = -1;
        for (Room room : rooms) {
            if (room == null || room.getCapacity() == null || room.getCapacity() <= 0) {
                continue;
            }
            int capacity = room.getCapacity();
            int seen = counts.merge(capacity, 1, Integer::sum);
            if (seen > bestCount || (seen == bestCount && capacity < best)) {
                best = capacity;
                bestCount = seen;
            }
        }
        return bestCount < 0 ? defaultCapacity : best;
    }
}
