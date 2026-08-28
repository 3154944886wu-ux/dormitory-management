package com.dormitory.utils;

import com.dormitory.model.Bed;
import com.dormitory.model.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据房间配置推导应生成的床位清单（纯逻辑，便于单元测试）。
 *
 * 规则：
 * - 床位总数以容量 capacity 为准；capacity 为空时退化为 window+corridor 之和。
 * - 前 windowBedsCount 张为靠窗(window)，其余为靠走廊(corridor)；靠窗数被截断到总数以内。
 * - 未配置靠窗/靠走廊数量时全部按靠走廊处理（与 bed 表默认值一致）。
 * - 床位编号为 A、B、C…（正常宿舍不超过 26 张）。
 */
public final class BedLayout {

    private BedLayout() {
    }

    public static List<Bed> forRoom(Room room) {
        List<Bed> beds = new ArrayList<>();
        if (room == null) {
            return beds;
        }

        int window = room.getWindowBedsCount() != null ? Math.max(0, room.getWindowBedsCount()) : 0;
        int corridor = room.getCorridorBedsCount() != null ? Math.max(0, room.getCorridorBedsCount()) : 0;

        int total;
        if (room.getCapacity() != null && room.getCapacity() > 0) {
            total = room.getCapacity();
        } else {
            total = window + corridor;
        }
        if (total <= 0) {
            return beds;
        }

        int windowCount = Math.min(window, total);
        for (int i = 0; i < total; i++) {
            Bed bed = new Bed();
            bed.setBedNumber(label(i));
            bed.setBedType(i < windowCount ? "window" : "corridor");
            bed.setIsOccupied(0);
            beds.add(bed);
        }
        return beds;
    }

    private static String label(int index) {
        if (index < 26) {
            return String.valueOf((char) ('A' + index));
        }
        // 兜底：超过 26 张时用 A1、A2… 保证唯一
        return "A" + (index - 25);
    }
}
