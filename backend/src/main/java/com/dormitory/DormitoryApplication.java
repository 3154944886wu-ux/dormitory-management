package com.dormitory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.dormitory.mapper")
@EnableScheduling
@EnableAsync
public class DormitoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DormitoryApplication.class, args);
    }
}