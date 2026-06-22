package com.dormitory.config;

import com.dormitory.mapper.UserMapper;
import com.dormitory.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "app.init-admin.enabled", havingValue = "true")
public class DataInitializer implements CommandLineRunner {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) {
        // 初始化默认管理员账号
        User existingAdmin = userMapper.findByUsername("admin");
        if (existingAdmin == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("系统管理员");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            userMapper.insert(admin);
            System.out.println("✅ 默认管理员账号已创建: admin / admin123");
        } else {
            // 确保管理员密码正确（重置为 admin123）
            if (!passwordEncoder.matches("admin123", existingAdmin.getPassword())) {
                userMapper.updatePassword(existingAdmin.getId(), passwordEncoder.encode("admin123"));
                System.out.println("✅ 管理员密码已重置: admin / admin123");
            }
        }
    }
}