package com.dormitory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 归寝异常记录
 */
@Data
public class CheckException {
    private Long id;
    private Long studentId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate exceptionDate;
    
    private Integer exceptionType;  // 1晚归 2未归 3缺卡
    private Long checkRecordId;
    private Integer handled;  // 0未处理 1已处理
    private Long handlerId;
    private String handleResult;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;
    
    private String handleNote;
    
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