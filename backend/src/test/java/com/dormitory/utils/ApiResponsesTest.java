package com.dormitory.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiResponsesTest {

    @Test
    void mapsJsonCodeToHttpStatus() {
        assertEquals(HttpStatus.OK, ApiResponses.json(Map.of("code", 200)).getStatusCode());
        assertEquals(HttpStatus.CREATED, ApiResponses.json(Map.of("code", 201)).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, ApiResponses.json(Map.of("code", 400, "message", "x")).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, ApiResponses.json(Map.of("code", 404, "message", "x")).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, ApiResponses.json(Map.of("code", 403, "message", "x")).getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ApiResponses.json(Map.of("code", 500, "message", "x")).getStatusCode());
        ResponseEntity<Map<String, Object>> missing = ApiResponses.json(Map.of("data", "ok"));
        assertEquals(HttpStatus.OK, missing.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, ApiResponses.forbidden("无权操作该范围外的数据").getStatusCode());
    }
}
