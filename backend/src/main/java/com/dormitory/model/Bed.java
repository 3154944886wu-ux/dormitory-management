package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Bed {
    private Long id;
    private String bedNumber;
    private Long roomId;
    private String bedType;
    private Integer isOccupied;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String roomNumber;
    private String buildingName;
}
