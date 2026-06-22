package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String role;  // STUDENT, ADMIN
    private Integer status;  // 1正常, 0禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}