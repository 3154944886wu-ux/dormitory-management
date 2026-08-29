package com.dormitory.utils;

import java.util.Objects;

/**
 * 批次 allowMixMajor=0 时，同屋必须同专业。
 */
public final class MatchingMajors {

    private MatchingMajors() {
    }

    public static boolean canGroup(Integer allowMixMajor, Integer majorA, Integer majorB) {
        if (allowMixMajor == null || allowMixMajor != 0) {
            return true;
        }
        return Objects.equals(normalize(majorA), normalize(majorB));
    }

    private static Integer normalize(Integer majorId) {
        return majorId == null ? 0 : majorId;
    }
}
