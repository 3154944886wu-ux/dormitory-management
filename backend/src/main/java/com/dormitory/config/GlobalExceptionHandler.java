package com.dormitory.config;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccess(DataAccessException e) {
        String message = e.getMostSpecificCause() != null
                ? e.getMostSpecificCause().getMessage()
                : e.getMessage();

        if (message != null && message.contains("Unknown column")) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "数据库查询失败，请检查表结构或联系管理员"
            ));
        }

        return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "数据库操作失败"
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "success", false,
                "message", e.getMessage() != null ? e.getMessage() : "参数不合法"
        ));
    }
}
