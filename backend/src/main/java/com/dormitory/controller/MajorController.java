package com.dormitory.controller;

import com.dormitory.model.Major;
import com.dormitory.mapper.MajorMapper;
import com.dormitory.utils.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/majors")
public class MajorController {

    private final MajorMapper majorMapper;

    public MajorController(MajorMapper majorMapper) {
        this.majorMapper = majorMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> list(@RequestParam(required = false) Long collegeId) {
        List<Major> list;
        if (collegeId != null) {
            list = majorMapper.findByCollegeId(collegeId);
        } else {
            list = majorMapper.findAll();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return ApiResponses.json(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Major major = majorMapper.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (major == null) {
            result.put("code", 404);
            result.put("message", "专业不存在");
            return ApiResponses.json(result);
        }
        result.put("code", 200);
        result.put("data", major);
        return ApiResponses.json(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Major major) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (major.getName() == null || major.getName().isBlank()) {
                result.put("code", 400);
                result.put("message", "专业名称不能为空");
                return ApiResponses.json(result);
            }
            if (major.getCollegeId() == null) {
                result.put("code", 400);
                result.put("message", "请选择所属学院");
                return ApiResponses.json(result);
            }
            majorMapper.insert(major);
            result.put("code", 201);
            result.put("message", "创建成功");
            result.put("data", major);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Major major) {
        Map<String, Object> result = new HashMap<>();
        try {
            Major existing = majorMapper.findById(id);
            if (existing == null) {
                result.put("code", 404);
                result.put("message", "专业不存在");
                return ApiResponses.json(result);
            }
            major.setId(id);
            majorMapper.update(major);
            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            majorMapper.deleteById(id);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }
}
