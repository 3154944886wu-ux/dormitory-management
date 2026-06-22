package com.dormitory.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

    /**
     * 单文件上传
     */
    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "文件不能为空"
            ));
        }

        try {
            String url = saveFile(file);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "上传成功",
                "url", url
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
    public ResponseEntity<?> uploadBatch(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    urls.add(saveFile(file));
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
