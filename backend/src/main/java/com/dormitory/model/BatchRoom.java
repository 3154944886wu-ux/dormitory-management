package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BatchRoom {
    private Long id;
    private Long batchId;
    private Long roomId;
    private LocalDateTime createTime;

    private String roomNumber;
    private String buildingName;
}
