package com.dormitory.config;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "code", 403,
                "success", false,
                "message", e.getMessage() != null ? e.getMessage() : "无权访问"
        ));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccess(DataAccessException e) {
        String message = e.getMostSpecificCause() != null
                ? e.getMostSpecificCause().getMessage()
                : e.getMessage();

        if (message != null && message.contains("Unknown column")) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "数据库表结构不完整，请执行 database/migration_check_rules_fix.sql 升级脚本后重试"
            ));
        }

        return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "数据库操作失败：" + (message != null ? message : "未知错误")
        ));
    }
}
