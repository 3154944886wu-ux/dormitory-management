package com.dormitory.config;

import com.dormitory.mapper.UserMapper;
import com.dormitory.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(value = "app.seed-demo.enabled", havingValue = "false", matchIfMissing = true)
public class DemoTeacherSanitizer implements CommandLineRunner {

    private static final List<String> DEMO_EMPLOYEE_NOS = List.of("010001", "010002");

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public DemoTeacherSanitizer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        for (String employeeNo : DEMO_EMPLOYEE_NOS) {
            User user = userMapper.findByUsername(employeeNo);
            if (user == null || user.getStatus() == null || user.getStatus() == 0) {
                continue;
            }
            if (user.getPassword() == null || !passwordEncoder.matches(employeeNo, user.getPassword())) {
                continue;
            }
            user.setStatus(0);
            userMapper.update(user);
            System.out.println("⚠ 已禁用默认口令示例教师: " + employeeNo);
        }
    }
}
