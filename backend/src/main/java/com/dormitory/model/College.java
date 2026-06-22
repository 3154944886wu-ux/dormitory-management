package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class College {
    private Long id;
    private String name;
    private LocalDateTime createTime;
}
