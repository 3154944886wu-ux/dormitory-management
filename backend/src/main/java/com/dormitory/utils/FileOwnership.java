package com.dormitory.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 判断请求的上传 URL 是否出现在逗号分隔的已存附件列表中，并解析上传者用户 ID。
 */
public final class FileOwnership {

    private static final Pattern OWNER_FILE = Pattern.compile("(?:^|.*/)u(\\d+)_[0-9a-fA-F-]+\\.[A-Za-z0-9]+$");

    private FileOwnership() {
    }

    public static boolean containsUrl(String storedCsv, String requestedUrl) {
        if (storedCsv == null || storedCsv.isBlank() || requestedUrl == null || requestedUrl.isBlank()) {
            return false;
        }
        String wanted = requestedUrl.trim();
        for (String part : storedCsv.split(",")) {
            if (wanted.equals(part.trim())) {
                return true;
            }
        }
        return false;
    }

    public static Long ownerUserId(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) {
            return null;
        }
        Matcher matcher = OWNER_FILE.matcher(publicUrl.trim());
        if (!matcher.find()) {
            return null;
        }
        return Long.parseLong(matcher.group(1));
    }

    public static boolean uploadedBy(String publicUrl, Long userId) {
        if (userId == null) {
            return false;
        }
        return userId.equals(ownerUserId(publicUrl));
    }

    public static String keepOwned(String storedCsv, Long userId) {
        if (storedCsv == null || storedCsv.isBlank()) {
            return storedCsv;
        }
        List<String> kept = new ArrayList<>();
        for (String part : storedCsv.split(",")) {
            String url = part.trim();
            if (url.isEmpty()) {
                continue;
            }
            if (uploadedBy(url, userId)) {
                kept.add(url);
            }
        }
        return String.join(",", kept);
    }
}
