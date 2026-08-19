package com.dormitory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 单文件上传
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STUDENT')")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "文件不能为空"
            ));
        }

        try {
            validateFile(file);
            String url = saveFile(file);
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
    public ResponseEntity<?> uploadBatch(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    validateFile(file);
                    urls.add(saveFile(file));
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

    private String saveFile(MultipartFile file) throws IOException {
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
        String newFileName = UUID.randomUUID().toString() + suffix;

        // 保存文件
        Path filePath = uploadPath.resolve(newFileName);
        file.transferTo(filePath.toFile());

        // 返回访问URL
        return "/uploads/" + dateDir + "/" + newFileName;
    }
}
