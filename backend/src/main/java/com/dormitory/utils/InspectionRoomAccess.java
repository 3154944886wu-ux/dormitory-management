package com.dormitory.utils;

import java.util.Locale;

/**
 * 房间检查记录读取权限：管理员/宿管可看任意房间；学生仅能看本人入住房间。
 */
public final class InspectionRoomAccess {

    private InspectionRoomAccess() {
    }

    public static boolean canView(String role, Long requestedRoomId, Long studentRoomId) {
        if (role == null || role.isBlank()) {
            return false;
        }
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring(5);
        }
        if ("ADMIN".equals(normalized) || "MANAGER".equals(normalized)) {
            return true;
        }
        if ("STUDENT".equals(normalized)) {
            return requestedRoomId != null && requestedRoomId.equals(studentRoomId);
        }
        return false;
    }
}
