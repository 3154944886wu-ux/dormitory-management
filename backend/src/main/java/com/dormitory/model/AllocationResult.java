package com.dormitory.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AllocationResult {
    private Long id;
    private Long studentId;
    private Long batchId;
    private Long roommateGroupId;
    private Long roomId;
    private Long bedId;
    private BigDecimal matchScore;
    private Integer reallocationCount;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String studentName;
    private String studentNo;
    private String roomNumber;
    private String bedNumber;
    private String batchName;
}
