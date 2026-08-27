package com.dormitory.utils;

/**
 * 判断请求的上传 URL 是否出现在逗号分隔的已存附件列表中。
 */
public final class FileOwnership {

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
}
