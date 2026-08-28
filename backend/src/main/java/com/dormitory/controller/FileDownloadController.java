package com.dormitory.controller;

import com.dormitory.service.FileAccessService;
import com.dormitory.utils.UploadPath;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 上传文件下载：需登录；管理员可看全部，宿管仅看范围内附件，学生仅看自己有归属的附件。
 */
@RestController
public class FileDownloadController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads";

    private final FileAccessService fileAccessService;

    public FileDownloadController(FileAccessService fileAccessService) {
        this.fileAccessService = fileAccessService;
    }

    @GetMapping("/uploads/**")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> download(HttpServletRequest request, Authentication auth) {
        String relative;
        try {
            relative = UploadPath.relativeFile(request.getRequestURI());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        String publicUrl = "/uploads/" + relative.replace('\\', '/');
        String role = roleOf(auth);
        if (!fileAccessService.canAccess(auth.getName(), role, publicUrl)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Path root = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
        Path file = root.resolve(relative).normalize();
        if (!file.startsWith(root) || !Files.isRegularFile(file)) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = MediaTypeFactory.getMediaType(file.getFileName().toString())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(new FileSystemResource(file));
    }

    private static String roleOf(Authentication auth) {
        if (auth == null) {
            return null;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse(null);
    }
}
