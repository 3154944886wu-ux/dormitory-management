package com.dormitory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.dormitory.mapper.UserMapper;
import com.dormitory.model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp", ".pdf"
    );

    private final UserMapper userMapper;

    public UploadController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 单文件上传
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, Authentication auth) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "文件不能为空"
            ));
        }

        try {
            validateFile(file);
            String url = saveFile(file, userId(auth));
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "上传成功",
                "url", url
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "上传失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 多文件上传
     */
    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<?> uploadBatch(@RequestParam("files") MultipartFile[] files, Authentication auth) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    validateFile(file);
                    urls.add(saveFile(file, userId(auth)));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", e.getMessage()
                    ));
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "上传失败: " + e.getMessage()
                    ));
                }
            }
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "上传成功",
            "urls", urls
        ));
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过 5MB");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.contains(".")) {
            throw new IllegalArgumentException("不支持的文件类型");
        }

        String suffix = originalName.substring(originalName.lastIndexOf(".")).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(suffix)) {
            throw new IllegalArgumentException("仅支持上传图片或 PDF 文件");
        }
    }

    private String saveFile(MultipartFile file, Long userId) throws IOException {
        // 按日期分目录
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Path uploadPath = Paths.get(UPLOAD_DIR, dateDir);
        Files.createDirectories(uploadPath);

        // 生成唯一文件名
        String originalName = file.getOriginalFilename();
        String suffix = "";
        if (originalName != null && originalName.contains(".")) {
            suffix = originalName.substring(originalName.lastIndexOf("."));
        }
        String ownerPrefix = userId == null ? "" : "u" + userId + "_";
        String newFileName = ownerPrefix + UUID.randomUUID() + suffix;

        // 保存文件
        Path filePath = uploadPath.resolve(newFileName);
        file.transferTo(filePath.toFile());

        // 返回访问URL
        return "/uploads/" + dateDir + "/" + newFileName;
    }

    private Long userId(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return null;
        }
        User user = userMapper.findByUsername(auth.getName());
        return user == null ? null : user.getId();
    }
}
