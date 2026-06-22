package com.dormitory.model;

import lombok.Data;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Data
public class OperationLog {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Long id;
    private Long studentId;
    private String operatorType;
    private String operatorId;
    private String action;
    private String detail;
    private LocalDateTime createTime;

    private String studentName;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getDetailMap() {
        if (detail == null || detail.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.readValue(detail, Map.class);
        } catch (Exception e) {
            return Collections.singletonMap("raw", detail);
        }
    }
}
