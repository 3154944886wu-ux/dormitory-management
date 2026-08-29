package com.dormitory.controller;

import com.dormitory.service.DormSelectionService;
import com.dormitory.service.NotificationService;
import com.dormitory.utils.ApiResponses;
import com.dormitory.utils.SurveyAnswers;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dorm-selection")
@PreAuthorize("hasRole('STUDENT')")
public class DormSelectionController {

    private final DormSelectionService dormSelectionService;
    private final NotificationService notificationService;

    public DormSelectionController(DormSelectionService dormSelectionService,
                                   NotificationService notificationService) {
        this.dormSelectionService = dormSelectionService;
        this.notificationService = notificationService;
    }

    /** 学生查看自己的通知 */
    @GetMapping("/my-notifications")
    public ResponseEntity<Map<String, Object>> myNotifications(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<?> notifications = notificationService.getMyNotifications(authentication.getName());
            result.put("code", 200);
            result.put("data", notifications);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    @GetMapping("/my-survey")
    public ResponseEntity<Map<String, Object>> mySurvey(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> data = dormSelectionService.mySurvey(authentication.getName());
            result.put("code", 200);
            result.put("data", data);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    @PostMapping("/submit-answers")
    public ResponseEntity<Map<String, Object>> submitAnswers(Authentication authentication,
                                              @RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> answersList = (List<Map<String, Object>>) body.get("answers");
            if (answersList == null || answersList.isEmpty()) {
                result.put("code", 400);
                result.put("message", "答案不能为空");
                return ApiResponses.json(result);
            }

            List<DormSelectionService.AnswerItem> answers = answersList.stream().map(m -> {
                DormSelectionService.AnswerItem item = new DormSelectionService.AnswerItem();
                item.setQId(SurveyAnswers.requireId(m, "qId"));
                item.setOptionId(SurveyAnswers.requireId(m, "optionId"));
                return item;
            }).toList();

            Map<String, Object> data = dormSelectionService.submitAnswers(authentication.getName(), answers);
            result.put("code", 200);
            result.put("message", "提交成功");
            result.put("data", data);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    @PostMapping("/confirm-allocation")
    public ResponseEntity<Map<String, Object>> confirmAllocation(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> data = dormSelectionService.confirmAllocation(authentication.getName());
            result.put("code", 200);
            result.put("message", "确认成功");
            result.put("data", data);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }

    @PostMapping("/request-reallocation")
    public ResponseEntity<Map<String, Object>> requestReallocation(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> data = dormSelectionService.requestReallocation(authentication.getName());
            result.put("code", 200);
            result.put("message", data.get("message"));
            result.put("data", data);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return ApiResponses.json(result);
    }
}
