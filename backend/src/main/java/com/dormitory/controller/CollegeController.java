package com.dormitory.controller;

import com.dormitory.model.College;
import com.dormitory.mapper.CollegeMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/colleges")
public class CollegeController {

    private final CollegeMapper collegeMapper;

    public CollegeController(CollegeMapper collegeMapper) {
        this.collegeMapper = collegeMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public Map<String, Object> list() {
        List<College> list = collegeMapper.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getById(@PathVariable Long id) {
        College college = collegeMapper.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (college == null) {
            result.put("code", 404);
            result.put("message", "学院不存在");
            return result;
        }
        result.put("code", 200);
        result.put("data", college);
        return result;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> create(@RequestBody College college) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (college.getName() == null || college.getName().isBlank()) {
                result.put("code", 400);
                result.put("message", "学院名称不能为空");
                return result;
            }
            collegeMapper.insert(college);
            result.put("code", 201);
            result.put("message", "创建成功");
            result.put("data", college);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody College college) {
        Map<String, Object> result = new HashMap<>();
        try {
            College existing = collegeMapper.findById(id);
            if (existing == null) {
                result.put("code", 404);
                result.put("message", "学院不存在");
                return result;
            }
            college.setId(id);
            collegeMapper.update(college);
            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            collegeMapper.deleteById(id);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
