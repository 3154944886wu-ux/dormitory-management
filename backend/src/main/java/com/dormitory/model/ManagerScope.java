package com.dormitory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 宿管/辅导员的数据管理范围。
 */
@Data
public class ManagerScope {
    private Long id;
    private Long userId;
    private Long buildingId;
    private String className;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String username;
    private String nickname;
    private String buildingName;
}
