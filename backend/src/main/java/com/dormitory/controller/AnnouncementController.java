package com.dormitory.controller;

import com.dormitory.model.Announcement;
import com.dormitory.model.User;
import com.dormitory.service.AnnouncementService;
import com.dormitory.utils.AnnouncementAccess;
import com.dormitory.utils.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    
    private final AnnouncementService announcementService;
    private final JwtUtils jwtUtils;
    
    public AnnouncementController(AnnouncementService announcementService, JwtUtils jwtUtils) {
        this.announcementService = announcementService;
        this.jwtUtils = jwtUtils;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String title,
            Authentication auth) {
        List<Announcement> announcements;
        if (isStudent(auth)) {
            announcements = announcementService.getPublishedAnnouncements();
        } else {
            announcements = announcementService.getAllAnnouncements();
            if (status != null) {
                announcements = announcements.stream()
                        .filter(a -> a.getStatus().equals(status))
                        .toList();
            }
        }

        if (type != null) {
            announcements = announcements.stream()
                    .filter(a -> a.getType().equals(type))
                    .toList();
        }
        if (title != null && !title.isBlank()) {
            String keyword = title.trim();
            announcements = announcements.stream()
                    .filter(a -> a.getTitle() != null && a.getTitle().contains(keyword))
                    .toList();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        if (isStudent(auth) && pageNum != null) {
            int total = announcements.size();
            List<Announcement> page = com.dormitory.utils.Pagination.slice(announcements, pageNum, pageSize == null ? 10 : pageSize);
            result.put("data", Map.of("list", page, "total", total));
        } else {
            result.put("data", announcements);
        }
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/published")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getPublished() {
        List<Announcement> announcements = announcementService.getPublishedAnnouncements();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", announcements);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id, Authentication auth) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        Map<String, Object> result = new HashMap<>();
        if (announcement == null || (isStudent(auth) && !AnnouncementAccess.isPublished(announcement))) {
            result.put("code", 404);
            result.put("message", "公告不存在");
            return ResponseEntity.status(404).body(result);
        }
        result.put("code", 200);
        result.put("data", announcement);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(
            @RequestBody Announcement announcement,
            @RequestHeader("Authorization") String token) {
        
        Long userId = getUserIdFromToken(token);
        Announcement created = announcementService.createAnnouncement(announcement, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", created);
        result.put("message", "创建成功");
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @RequestBody Announcement announcement) {
        
        try {
            Announcement updated = announcementService.updateAnnouncement(id, announcement);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("data", updated);
            result.put("message", "更新成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 404);
            result.put("message", e.getMessage());
            return ResponseEntity.status(404).body(result);
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> publish(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.publishAnnouncement(id);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("data", announcement);
            result.put("message", "发布成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 404);
            result.put("message", e.getMessage());
            return ResponseEntity.status(404).body(result);
        }
    }
    
    @PutMapping("/{id}/offline")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> offline(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.offlineAnnouncement(id);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("data", announcement);
            result.put("message", "已下线");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 404);
            result.put("message", e.getMessage());
            return ResponseEntity.status(404).body(result);
        }
    }
    
    @PutMapping("/{id}/top")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleTop(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.toggleTop(id);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("data", announcement);
            result.put("message", announcement.getIsTop() == 1 ? "已置顶" : "已取消置顶");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 404);
            result.put("message", e.getMessage());
            return ResponseEntity.status(404).body(result);
        }
    }
    
    private Long getUserIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtils.getUserIdFromToken(token);
    }

    private boolean isStudent(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_STUDENT".equals(a.getAuthority()));
    }
}