package com.dormitory.utils;

/**
 * 实际在住人数子查询。房间表别名必须为 {@code r}。
 */
public final class OccupancySql {

    public static final String LIVE_IN_ROOM =
            "(SELECT COUNT(*) FROM students occ WHERE occ.room_id = r.id AND occ.status = 1)";

    private OccupancySql() {
    }
}
