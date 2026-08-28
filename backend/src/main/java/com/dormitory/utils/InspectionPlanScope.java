package com.dormitory.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 检查计划 building_ids（逗号分隔）与宿管楼栋范围是否相交。
 */
public final class InspectionPlanScope {

    private InspectionPlanScope() {
    }

    public static boolean visibleToManager(String planBuildingIds, Collection<Long> managerBuildingIds) {
        if (managerBuildingIds == null || managerBuildingIds.isEmpty()) {
            return false;
        }
        Set<Long> planIds = parseIds(planBuildingIds);
        if (planIds.isEmpty()) {
            return false;
        }
        for (Long id : managerBuildingIds) {
            if (id != null && planIds.contains(id)) {
                return true;
            }
        }
        return false;
    }

    public static Set<Long> parseIds(String csv) {
        Set<Long> ids = new HashSet<>();
        if (csv == null || csv.isBlank()) {
            return ids;
        }
        for (String part : csv.split(",")) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                ids.add(Long.parseLong(trimmed));
            } catch (NumberFormatException ignored) {
                // 忽略脏数据
            }
        }
        return ids;
    }
}
