package com.dormitory.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 匹配剩余学生：不得超过房间容量地并入末组。
 */
public final class MatchingGroups {

    private MatchingGroups() {
    }

    public static <T> void appendLeftovers(List<List<T>> groups, List<T> leftover, int capacity) {
        if (leftover == null || leftover.isEmpty()) {
            return;
        }
        int cap = Math.max(capacity, 1);
        List<T> remaining = new ArrayList<>(leftover);
        if (!groups.isEmpty()) {
            List<T> last = groups.get(groups.size() - 1);
            int room = cap - last.size();
            if (room > 0) {
                int take = Math.min(room, remaining.size());
                last.addAll(remaining.subList(0, take));
                remaining = new ArrayList<>(remaining.subList(take, remaining.size()));
            }
        }
        for (int i = 0; i < remaining.size(); i += cap) {
            List<T> group = new ArrayList<>(remaining.subList(i, Math.min(i + cap, remaining.size())));
            groups.add(group);
        }
    }

    /**
     * 单独成组，不填入调用方已有末组。禁止混专业时每个专业必须各打一份。
     */
    public static <T> List<List<T>> packIsolated(List<T> leftover, int capacity) {
        List<List<T>> groups = new ArrayList<>();
        appendLeftovers(groups, leftover, capacity);
        return groups;
    }
}
