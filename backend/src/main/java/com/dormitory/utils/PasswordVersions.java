package com.dormitory.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * 将密码哈希映射为 JWT 中的短版本号，改密后旧 token 无法通过校验。
 */
public final class PasswordVersions {

    private PasswordVersions() {
    }

    public static String from(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isBlank()) {
            return "";
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(encodedPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest, 0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 不可用", e);
        }
    }
}
