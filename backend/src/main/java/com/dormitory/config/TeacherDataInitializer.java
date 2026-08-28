package com.dormitory.config;

import com.dormitory.mapper.TeacherMapper;
import com.dormitory.service.TeacherService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class TeacherDataInitializer implements CommandLineRunner {

    private final TeacherMapper teacherMapper;
    private final TeacherService teacherService;

    public TeacherDataInitializer(TeacherMapper teacherMapper, TeacherService teacherService) {
        this.teacherMapper = teacherMapper;
        this.teacherService = teacherService;
    }

    @Override
    public void run(String... args) {
        try {
            teacherMapper.countAll();
        } catch (Exception e) {
            System.out.println("⚠ managers 表尚未创建，请执行 database/migration_teachers.sql");
            return;
        }

        teacherService.ensureTeacher("010001", "张扬");
        teacherService.ensureTeacher("010002", "李昊");
        System.out.println("✅ 示例教师已就绪: 010001/张扬, 010002/李昊（初始密码为工号）");
    }
}
