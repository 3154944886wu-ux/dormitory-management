package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Major {
    private Long id;
    private String name;
    private Long collegeId;
    private LocalDateTime createTime;

    private String collegeName;
}
