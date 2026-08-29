package com.dormitory.utils;

import com.dormitory.model.ManagerScope;

import java.util.List;

public final class ManagerScopeJson {

    private ManagerScopeJson() {
    }

    public static String encode(List<ManagerScope> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < scopes.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            ManagerScope scope = scopes.get(i);
            sb.append("{\"buildingId\":");
            sb.append(scope.getBuildingId() == null ? "null" : scope.getBuildingId());
            sb.append(",\"className\":");
            String className = scope.getClassName();
            if (className == null || className.isBlank()) {
                sb.append("null");
            } else {
                sb.append('"').append(escape(className.trim())).append('"');
            }
            sb.append('}');
        }
        sb.append(']');
        return sb.toString();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
