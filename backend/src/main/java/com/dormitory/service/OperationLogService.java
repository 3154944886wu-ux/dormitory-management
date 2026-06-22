package com.dormitory.service;

import com.dormitory.mapper.OperationLogMapper;
import com.dormitory.model.OperationLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper;

    public OperationLogService(OperationLogMapper operationLogMapper, ObjectMapper objectMapper) {
        this.operationLogMapper = operationLogMapper;
        this.objectMapper = objectMapper;
    }

    public void log(Long studentId, String operatorType, String operatorId, String action, Map<String, Object> detail) {
        OperationLog log = new OperationLog();
        log.setStudentId(studentId);
        log.setOperatorType(operatorType);
        log.setOperatorId(operatorId);
        log.setAction(action);
        try {
            log.setDetail(objectMapper.writeValueAsString(detail == null ? Map.of() : detail));
        } catch (Exception e) {
            log.setDetail(String.valueOf(detail));
        }
        operationLogMapper.insert(log);
    }

    public Map<String, Object> search(String operatorType, String action, String keyword, int page, int size) {
        int offset = Math.max(page - 1, 0) * size;
        List<OperationLog> logs = operationLogMapper.search(blankToNull(operatorType), blankToNull(action), blankToNull(keyword), offset, size);
        int total = operationLogMapper.countSearch(blankToNull(operatorType), blankToNull(action), blankToNull(keyword));

        Map<String, Object> result = new HashMap<>();
        result.put("data", logs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
