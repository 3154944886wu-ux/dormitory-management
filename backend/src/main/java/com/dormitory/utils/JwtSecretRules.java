package com.dormitory.utils;

public final class JwtSecretRules {

    public static final String DEFAULT_PLACEHOLDER = "change-me-in-local-config";
    public static final int MIN_LENGTH = 32;

    private JwtSecretRules() {
    }

    public static void validate(String secret) {
        if (secret == null || secret.isBlank() || DEFAULT_PLACEHOLDER.equals(secret.trim())) {
            throw new IllegalStateException("请在 application-local.yml 配置 jwt.secret，不能使用默认占位符");
        }
        if (secret.length() < MIN_LENGTH) {
            throw new IllegalStateException("jwt.secret 长度至少 " + MIN_LENGTH + " 位");
        }
    }
}
