package com.dormitory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 归寝打卡记录
 */
@Data
public class CheckInRecord {
    private Long id;
    private Long studentId;
    private Long roomId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;
    
    private Integer checkType;  // 0定位 1人脸 2手动
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal locationAccuracy;
    private String deviceInfo;
    private String ipAddress;
    private Integer status;  // 0正常 1晚归 2未归 3请假
    private String remark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    // 关联字段
    private String studentName;
    private String studentNo;
    private String roomNumber;
    private String buildingName;
    private Long buildingId;
    private String className;
}