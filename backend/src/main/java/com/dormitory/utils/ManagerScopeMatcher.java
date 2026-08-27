package com.dormitory.utils;

import com.dormitory.model.ManagerScope;

import java.util.List;

/**
 * 判断一条数据（按其所属楼栋与班级）是否落在某个宿管/辅导员的管理范围内（纯逻辑，便于单元测试）。
 *
 * 规则：
 * - 管理员范围为多条，命中任意一条即可见。
 * - 单条范围中 buildingId 为空表示不限楼栋；className 为空/空白表示不限班级。
 * - 无任何范围（空/ null）视为看不到任何数据。
 */
public final class ManagerScopeMatcher {

    private ManagerScopeMatcher() {
    }

    public static boolean isVisible(List<ManagerScope> scopes, Long buildingId, String className) {
        if (scopes == null || scopes.isEmpty()) {
            return false;
        }
        for (ManagerScope scope : scopes) {
            boolean buildingOk = scope.getBuildingId() == null
                    || scope.getBuildingId().equals(buildingId);
            boolean classOk = scope.getClassName() == null
                    || scope.getClassName().isBlank()
                    || scope.getClassName().equals(className);
            if (buildingOk && classOk) {
                return true;
            }
        }
        return false;
    }
}
