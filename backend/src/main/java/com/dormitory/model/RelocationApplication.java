package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RelocationApplication {
    private Long id;
    private Long studentId;
    private Long batchId;
    private Long currentRoomId;
    private Long currentBedId;
    private String reason;
    private Long preferredBuildingId;
    private String status;
    private Long reviewedBy;
    private String reviewComment;
    private Long executedBy;
    private Long newRoomId;
    private Long newBedId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String studentName;
    private String studentNo;
    private String currentRoomNumber;
    private String currentBedNumber;
    private String currentBuildingName;
    private String newRoomNumber;
    private String newBedNumber;
    private String newBuildingName;
}
