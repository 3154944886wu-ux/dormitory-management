package com.dormitory.controller;

import com.dormitory.model.Questionnaire;
import com.dormitory.model.QuestionOption;
import com.dormitory.service.QuestionnaireService;
import com.dormitory.utils.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questionnaires")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> list() {
        List<Questionnaire> list = questionnaireService.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return ApiResponses.json(result);
    }

    @GetMapping("/with-options")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> listWithOptions() {
        List<Questionnaire> list = questionnaireService.findAllWithOptions();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return ApiResponses.json(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Questionnaire q = questionnaireService.getWithOptions(id);
        Map<String, Object> result = new HashMap<>();
        if (q == null) {
            result.put("code", 404);
            result.put("message", "题目不存在");
            return ApiResponses.json(result);
        }
        result.put("code", 200);
        result.put("data", q);
        return ApiResponses.json(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            Questionnaire questionnaire = parseQuestion(body);
            List<QuestionOption> options = parseOptions(body);
            Questionnaire created = questionnaireService.createWithOptions(questionnaire, options);
            result.put("code", 201);
            result.put("message", "创建成功");
            result.put("data", created);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            Questionnaire questionnaire = parseQuestion(body);
            List<QuestionOption> options = parseOptions(body);
            Questionnaire updated = questionnaireService.updateWithOptions(id, questionnaire, options);
            result.put("code", 200);
            result.put("message", "更新成功");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer isActive = (Integer) body.get("isActive");
            Questionnaire updated = questionnaireService.updateQuestionStatus(id, isActive);
            result.put("code", 200);
            result.put("message", "状态更新成功");
            result.put("data", updated);
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
            questionnaireService.deleteQuestion(id);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    @SuppressWarnings("unchecked")
    private Questionnaire parseQuestion(Map<String, Object> body) {
        Questionnaire q = new Questionnaire();
        q.setQuestionText((String) body.get("questionText"));
        q.setQuestionType((String) body.getOrDefault("questionType", "match"));
        q.setIsRequired((Integer) body.getOrDefault("isRequired", 1));
        q.setWeight((Integer) body.getOrDefault("weight", 1));
        q.setIsActive((Integer) body.getOrDefault("isActive", 1));
        return q;
    }

    @SuppressWarnings("unchecked")
    private List<QuestionOption> parseOptions(Map<String, Object> body) {
        List<Map<String, Object>> optionsData = (List<Map<String, Object>>) body.get("options");
        if (optionsData == null) {
            return List.of();
        }
        return optionsData.stream().map(opt -> {
            QuestionOption option = new QuestionOption();
            option.setOptionText((String) opt.get("optionText"));
            option.setOptionValue(((Number) opt.getOrDefault("optionValue", 0)).intValue());
            return option;
        }).toList();
    }
}
