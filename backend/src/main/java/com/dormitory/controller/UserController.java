package com.dormitory.controller;

import com.dormitory.mapper.UserMapper;
import com.dormitory.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    public UserController(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }
    
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userMapper.findByUsername(username);
        
        Map<String, Object> result = new HashMap<>();
        if (user == null) {
            result.put("code", 404);
            result.put("message", "用户不存在");
            return ResponseEntity.status(404).body(result);
        }
        
        // 不返回密码
        user.setPassword(null);
        
        result.put("code", 200);
        result.put("data", user);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateCurrentUser(
            Authentication authentication,
            @RequestBody User userUpdate) {
        
        String username = authentication.getName();
        User existingUser = userMapper.findByUsername(username);
        
        if (existingUser == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 404);
            result.put("message", "用户不存在");
            return ResponseEntity.status(404).body(result);
        }
        
        // 只允许更新特定字段
        existingUser.setNickname(userUpdate.getNickname());
        existingUser.setPhone(userUpdate.getPhone());
        existingUser.setEmail(userUpdate.getEmail());
        
        // 如果要更新密码
        if (userUpdate.getPassword() != null && !userUpdate.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
        }
        
        userMapper.update(existingUser);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "更新成功");
        return ResponseEntity.ok(result);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> list() {
        List<User> users = userMapper.findAll();
        
        // 不返回密码
        users.forEach(u -> u.setPassword(null));
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", users);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        if (userMapper.countByUsername(user.getUsername()) > 0) {
            result.put("code", 400);
            result.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(result);
        }

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            result.put("code", 400);
            result.put("message", "密码长度至少6位");
            return ResponseEntity.badRequest().body(result);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(normalizeRole(user.getRole()));
        user.setStatus(user.getStatus() == null ? 1 : user.getStatus());
        userMapper.insert(user);

        user.setPassword(null);
        result.put("code", 201);
        result.put("message", "创建成功");
        result.put("data", user);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        User user = userMapper.findById(id);
        
        Map<String, Object> result = new HashMap<>();
        if (user == null) {
            result.put("code", 404);
            result.put("message", "用户不存在");
            return ResponseEntity.status(404).body(result);
        }
        
        user.setPassword(null);
        result.put("code", 200);
        result.put("data", user);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestBody User userUpdate) {
        
        User existingUser = userMapper.findById(id);
        if (existingUser == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 404);
            result.put("message", "用户不存在");
            return ResponseEntity.status(404).body(result);
        }
        
        existingUser.setNickname(userUpdate.getNickname());
        existingUser.setPhone(userUpdate.getPhone());
        existingUser.setEmail(userUpdate.getEmail());
        
        if (userUpdate.getRole() != null) {
            existingUser.setRole(normalizeRole(userUpdate.getRole()));
        }

        if (userUpdate.getStatus() != null) {
            existingUser.setStatus(userUpdate.getStatus());
        } else if (existingUser.getStatus() == null) {
            existingUser.setStatus(1);
        }
        
        if (userUpdate.getPassword() != null && !userUpdate.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
        }
        
        userMapper.update(existingUser);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "更新成功");
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 404);
            result.put("message", "用户不存在");
            return ResponseEntity.status(404).body(result);
        }
        
        userMapper.deleteById(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "STUDENT";
        }
        return role.trim().toUpperCase();
    }
}