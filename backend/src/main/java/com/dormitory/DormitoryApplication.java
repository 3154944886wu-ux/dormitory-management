package com.dormitory;

import com.dormitory.utils.CheckWindow;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@MapperScan("com.dormitory.mapper")
@EnableScheduling
@EnableAsync
public class DormitoryApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(CheckWindow.ZONE));
        SpringApplication.run(DormitoryApplication.class, args);
    }
}