package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notification {
    private Long id;
    private Long recipientId;
    private Long batchId;
    private String type;
    private String content;
    private String channel;
    private String status;
    private LocalDateTime createTime;

    private String studentName;
    private String batchName;
}
