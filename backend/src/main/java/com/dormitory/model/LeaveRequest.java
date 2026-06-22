package com.dormitory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 请假申请
 */
@Data
public class LeaveRequest {
    private Long id;
    private Long studentId;
    private Integer leaveType;  // 0事假 1病假 2其他
    private String reason;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    private String contactPhone;
    private String destination;
    private String attachment;
    private Integer status;  // 0待审批 1已批准 2已拒绝 3已撤销 4已销假
    private Long approverId;
    private String approverName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approveTime;
    
    private String approveNote;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualReturnTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    // 关联字段
    private String studentName;
    private String studentNo;
    private String department;
    private String className;
    private String roomNumber;
    private String buildingName;
}