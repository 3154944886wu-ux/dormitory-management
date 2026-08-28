package com.dormitory.controller;

import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.Student;
import com.dormitory.model.User;
import com.dormitory.service.UserService;
import com.dormitory.utils.AuthMessages;
import com.dormitory.utils.JwtUtils;
import com.dormitory.utils.LoginRateLimiter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final StudentMapper studentMapper;
    private final LoginRateLimiter loginRateLimiter;

    public AuthController(UserService userService, JwtUtils jwtUtils, StudentMapper studentMapper,
                          LoginRateLimiter loginRateLimiter) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.studentMapper = studentMapper;
        this.loginRateLimiter = loginRateLimiter;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();

        String studentNo = body.get("studentNo");
        String name = body.get("name");
        String password = body.get("password");

        // 参数校验
        if (studentNo == null || studentNo.trim().isEmpty()) {
            result.put("code", 400);
            result.put("message", "请输入学号");
            return ResponseEntity.badRequest().body(result);
        }
        if (name == null || name.trim().isEmpty()) {
            result.put("code", 400);
            result.put("message", "请输入姓名");
            return ResponseEntity.badRequest().body(result);
        }
        if (password == null || password.length() < 6) {
            result.put("code", 400);
            result.put("message", "密码长度至少6位");
            return ResponseEntity.badRequest().body(result);
        }

        // 验证学生身份：学号 + 姓名（失败统一文案，避免枚举学号）
        Student student = studentMapper.findByStudentNo(studentNo.trim());
        if (userService.exists(studentNo.trim())
                || student == null
                || !student.getName().equals(name.trim())
                || student.getUserId() != null) {
            result.put("code", 400);
            result.put("message", AuthMessages.REGISTER_IDENTITY_FAILED);
            return ResponseEntity.badRequest().body(result);
        }

        // 创建用户（学号作为用户名）
        User user = new User();
        user.setUsername(studentNo.trim());
        user.setPassword(password);
        user.setNickname(student.getName());
        user.setRole("STUDENT");
        user.setPhone(student.getPhone());
        user.setStatus(1);

        Long userId = userService.register(user);

        // 关联学生记录到用户
        studentMapper.updateUserId(student.getId(), userId);

        result.put("code", 201);
        result.put("message", "注册成功");
        result.put("data", Map.of("userId", userId));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> result = new HashMap<>();
        String username = loginData.get("username");
        String password = loginData.get("password");

        if (loginRateLimiter.isBlocked(username)) {
            result.put("code", 429);
            result.put("message", "登录失败次数过多，请10分钟后再试");
            return ResponseEntity.status(429).body(result);
        }

        User user = userService.findByUsername(username);

        if (user == null) {
            loginRateLimiter.recordFailure(username);
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
            return ResponseEntity.status(401).body(result);
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            result.put("code", 401);
            result.put("message", "账号已被禁用，请联系管理员");
            return ResponseEntity.status(401).body(result);
        }

        if (!userService.verifyPassword(password, user.getPassword())) {
            loginRateLimiter.recordFailure(username);
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
            return ResponseEntity.status(401).body(result);
        }

        loginRateLimiter.recordSuccess(username);
        String token = jwtUtils.generateToken(user);

        result.put("code", 200);
        result.put("message", "登录成功");

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("username", user.getUsername());
        userData.put("nickname", user.getNickname() != null ? user.getNickname() : user.getUsername());
        userData.put("role", user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userData);

        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        User user = currentUserOrNull();
        Map<String, Object> result = new HashMap<>();
        if (user == null) {
            result.put("code", 401);
            result.put("message", "未登录");
            return ResponseEntity.status(401).body(result);
        }
        user.setPassword(null);
        result.put("code", 200);
        result.put("data", user);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, String> profileData) {
        User user = currentUserOrNull();
        Map<String, Object> result = new HashMap<>();
        if (user == null) {
            result.put("code", 401);
            result.put("message", "未登录");
            return ResponseEntity.status(401).body(result);
        }

        user.setNickname(profileData.get("nickname"));
        user.setPhone(profileData.get("phone"));
        user.setEmail(profileData.get("email"));

        userService.updateProfile(user);

        User updated = userService.findById(user.getId());
        updated.setPassword(null);

        result.put("code", 200);
        result.put("message", "信息更新成功");
        result.put("data", updated);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> passwordData) {
        User user = currentUserOrNull();
        Map<String, Object> result = new HashMap<>();
        if (user == null) {
            result.put("code", 401);
            result.put("message", "未登录");
            return ResponseEntity.status(401).body(result);
        }

        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");

        if (newPassword == null || newPassword.length() < 6) {
            result.put("code", 400);
            result.put("message", "密码长度至少6位");
            return ResponseEntity.badRequest().body(result);
        }

        if (!userService.verifyPassword(oldPassword, user.getPassword())) {
            result.put("code", 400);
            result.put("message", "原密码错误");
            return ResponseEntity.badRequest().body(result);
        }

        userService.updatePassword(user.getId(), newPassword);

        result.put("code", 200);
        result.put("message", "密码修改成功");
        return ResponseEntity.ok(result);
    }

    private User currentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        return userService.findByUsername(auth.getName());
    }
}