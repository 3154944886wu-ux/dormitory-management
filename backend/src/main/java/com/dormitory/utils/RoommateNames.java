package com.dormitory.utils;

import com.dormitory.model.Student;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 从同房间学生列表生成室友摘要（排除本人）。
 */
public final class RoommateNames {

    private RoommateNames() {
    }

    public static List<Map<String, Object>> summaries(List<Student> occupants, Long selfId) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (occupants == null || occupants.isEmpty()) {
            return result;
        }
        for (Student s : occupants) {
            if (s == null || (selfId != null && selfId.equals(s.getId()))) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", s.getName());
            row.put("studentNo", s.getStudentNo());
            row.put("studentId", s.getStudentNo());
            row.put("bedNumber", s.getBedNumber());
            row.put("phone", s.getPhone());
            row.put("className", s.getClassName());
            result.add(row);
        }
        return result;
    }

    public static String display(List<Map<String, Object>> summaries) {
        if (summaries == null || summaries.isEmpty()) {
            return "";
        }
        List<String> names = new ArrayList<>();
        for (Map<String, Object> row : summaries) {
            Object name = row.get("name");
            if (name != null && !name.toString().isBlank()) {
                names.add(name.toString());
            }
        }
        return String.join("、", names);
    }
}
