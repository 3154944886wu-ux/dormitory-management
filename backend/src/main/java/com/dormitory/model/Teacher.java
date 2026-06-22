package com.dormitory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Teacher {
    private Long id;
    private String employeeNo;
    private String name;
    private String phone;
    private String email;
    private Long userId;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 关联 users 表字段（查询时填充）
    private String username;
    private String nickname;
}
