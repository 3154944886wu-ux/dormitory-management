package com.dormitory.service;

import com.dormitory.mapper.UserMapper;
import com.dormitory.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    public User findById(Long id) {
        return userMapper.findById(id);
    }
    
    public boolean exists(String username) {
        return userMapper.countByUsername(username) > 0;
    }
    
    public Long register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole() == null ? "STUDENT" : user.getRole());
        user.setStatus(1);
        userMapper.insert(user);
        return user.getId();
    }
    
    public void updateProfile(User user) {
        userMapper.update(user);
    }
    
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public void updatePassword(Long userId, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(userId, encodedPassword);
    }
    
    public List<User> findAll() {
        return userMapper.findAll();
    }
}