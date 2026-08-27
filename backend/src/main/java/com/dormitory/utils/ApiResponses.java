package com.dormitory.utils;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public final class ApiResponses {

    private ApiResponses() {
    }

    public static ResponseEntity<Map<String, Object>> json(Map<String, Object> body) {
        Map<String, Object> payload = body == null ? new HashMap<>() : body;
        Object codeObj = payload.get("code");
        int code = codeObj instanceof Number number ? number.intValue() : 200;
        int http = switch (code) {
            case 201 -> 201;
            case 401 -> 401;
            case 403 -> 403;
            case 404 -> 404;
            case 500 -> 500;
            default -> code >= 400 ? 400 : 200;
        };
        return ResponseEntity.status(http).body(payload);
    }

    public static ResponseEntity<Map<String, Object>> forbidden(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 403);
        body.put("success", false);
        body.put("message", message == null || message.isBlank() ? "无权操作该范围外的数据" : message);
        return json(body);
    }
}
