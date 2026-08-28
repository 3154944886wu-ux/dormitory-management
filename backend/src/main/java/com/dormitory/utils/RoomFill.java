package com.dormitory.utils;

import com.dormitory.model.Room;
import com.dormitory.model.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RoomFill {

    private RoomFill() {
    }

    public static Map<Long, Integer> occupancyByRoom(List<Student> students) {
        Map<Long, Integer> map = new HashMap<>();
        if (students == null) {
            return map;
        }
        for (Student student : students) {
            if (student == null || student.getRoomId() == null || !isResiding(student)) {
                continue;
            }
            map.merge(student.getRoomId(), 1, Integer::sum);
        }
        return map;
    }

    public static int residing(List<Student> students) {
        if (students == null) {
            return 0;
        }
        int count = 0;
        for (Student student : students) {
            if (student != null && isResiding(student)) {
                count++;
            }
        }
        return count;
    }

    public static Map<String, Object> summarize(List<Room> rooms, Map<Long, Integer> occupancyByRoomId) {
        int total = rooms == null ? 0 : rooms.size();
        int free = 0;
        int partial = 0;
        int full = 0;
        if (rooms != null) {
            for (Room room : rooms) {
                int occupancy = occupancyOf(room, occupancyByRoomId);
                int capacity = room == null || room.getCapacity() == null ? 0 : room.getCapacity();
                if (occupancy <= 0) {
                    free++;
                } else if (capacity > 0 && occupancy >= capacity) {
                    full++;
                } else {
                    partial++;
                }
            }
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("roomCount", total);
        stats.put("freeRooms", free);
        stats.put("partialRooms", partial);
        stats.put("fullRooms", full);
        stats.put("occupiedRooms", partial + full);
        return stats;
    }

    public static String occupancyRate(int occupiedRooms, int totalRooms) {
        if (totalRooms <= 0) {
            return "0.0";
        }
        return String.format("%.1f", occupiedRooms * 100.0 / totalRooms);
    }

    public static int occupancyOf(Room room, Map<Long, Integer> occupancyByRoomId) {
        if (room == null) {
            return 0;
        }
        if (occupancyByRoomId != null) {
            if (room.getId() == null) {
                return 0;
            }
            return Math.max(0, occupancyByRoomId.getOrDefault(room.getId(), 0));
        }
        if (room.getOccupancy() != null) {
            return Math.max(0, room.getOccupancy());
        }
        if (room.getCurrentCount() != null) {
            return Math.max(0, room.getCurrentCount());
        }
        return 0;
    }

    private static boolean isResiding(Student student) {
        return student.getStatus() != null && student.getStatus() == 1;
    }
}
