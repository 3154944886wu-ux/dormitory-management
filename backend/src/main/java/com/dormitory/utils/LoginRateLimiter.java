package com.dormitory.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录失败限流：同一用户名在窗口内失败次数超限则暂时拒绝。
 */
@Component
public final class LoginRateLimiter {

    public static final int MAX_FAILURES = 5;
    public static final long WINDOW_SECONDS = 600;

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public boolean isBlocked(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        prune();
        Window window = windows.get(username.trim());
        return window != null && window.failures >= MAX_FAILURES && !window.expired();
    }

    public void recordFailure(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        String key = username.trim();
        windows.compute(key, (k, current) -> {
            if (current == null || current.expired()) {
                return new Window(1, Instant.now().plusSeconds(WINDOW_SECONDS));
            }
            current.failures++;
            return current;
        });
    }

    public void recordSuccess(String username) {
        if (username != null) {
            windows.remove(username.trim());
        }
    }

    private void prune() {
        Iterator<Map.Entry<String, Window>> it = windows.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().expired()) {
                it.remove();
            }
        }
    }

    private static final class Window {
        private int failures;
        private final Instant expiresAt;

        private Window(int failures, Instant expiresAt) {
            this.failures = failures;
            this.expiresAt = expiresAt;
        }

        private boolean expired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
