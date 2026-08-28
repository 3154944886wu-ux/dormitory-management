package com.dormitory.utils;

import java.util.Locale;

/**
 * 学生性别与楼栋类型是否匹配（纯逻辑）。
 * 楼栋：MALE / FEMALE / MIXED（大小写不敏感）；学生：男/女 或 male/female。
 */
public final class GenderMatcher {

    private GenderMatcher() {
    }

    public static boolean isCompatible(String studentGender, String buildingGenderType) {
        if (buildingGenderType == null || buildingGenderType.isBlank()) {
            return true;
        }
        String building = buildingGenderType.trim().toUpperCase(Locale.ROOT);
        String student = normalizeStudent(studentGender);
        return switch (building) {
            case "MALE", "男" -> "MALE".equals(student);
            case "FEMALE", "女" -> "FEMALE".equals(student);
            case "MIXED" -> true;
            default -> true;
        };
    }

    private static String normalizeStudent(String gender) {
        if (gender == null || gender.isBlank()) {
            return "";
        }
        String trimmed = gender.trim();
        String upper = trimmed.toUpperCase(Locale.ROOT);
        if ("男".equals(trimmed) || "MALE".equals(upper) || "M".equals(upper)) {
            return "MALE";
        }
        if ("女".equals(trimmed) || "FEMALE".equals(upper) || "F".equals(upper)) {
            return "FEMALE";
        }
        return upper;
    }
}
