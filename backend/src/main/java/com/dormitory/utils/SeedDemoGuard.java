package com.dormitory.utils;

import java.util.Collection;
import java.util.Locale;

/**
 * 演示灌数会按日期清空打卡，生产/预发即使误开开关也不得执行。
 */
public final class SeedDemoGuard {

    private SeedDemoGuard() {
    }

    public static boolean allow(boolean enabled, boolean allowDataWipe, Collection<String> activeProfiles) {
        if (!enabled || !allowDataWipe) {
            return false;
        }
        if (activeProfiles == null) {
            return true;
        }
        for (String profile : activeProfiles) {
            if (profile == null) {
                continue;
            }
            String normalized = profile.trim().toLowerCase(Locale.ROOT);
            if ("prod".equals(normalized) || "production".equals(normalized) || "pre".equals(normalized)
                    || "staging".equals(normalized) || "preprod".equals(normalized)) {
                return false;
            }
        }
        return true;
    }
}
