package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DormBatch {
    private Long id;
    private String name;
    private Long collegeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime confirmDeadline;
    private Integer maxReallocation;
    private Integer allowMixMajor;
    private Integer majorBonus;
    private Integer preferSameFloor;
    private String matchStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String collegeName;

    private Integer roomCount;
    private Integer studentCount;
}
