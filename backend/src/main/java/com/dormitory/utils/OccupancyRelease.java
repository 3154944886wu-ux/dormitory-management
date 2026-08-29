package com.dormitory.utils;

/**
 * 选宿重置/删除时，只释放学生「当前仍占用」的推荐房间，避免调宿后误伤新床位。
 */
public final class OccupancyRelease {

    private OccupancyRelease() {
    }

    public static boolean stillInAllocatedRoom(Long studentRoomId, Long allocatedRoomId) {
        return studentRoomId != null && allocatedRoomId != null && studentRoomId.equals(allocatedRoomId);
    }

    public static boolean needsRoomIncrement(Long studentRoomId, Long targetRoomId) {
        if (targetRoomId == null) {
            return false;
        }
        return studentRoomId == null || !studentRoomId.equals(targetRoomId);
    }
}
