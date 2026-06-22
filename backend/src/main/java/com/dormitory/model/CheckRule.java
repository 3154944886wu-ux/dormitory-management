package com.dormitory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 归寝规则
 */
@Data
public class CheckRule {
    private Long id;
    private String name;
    private Long buildingId;
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkStartTime;
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkEndTime;
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime lateThreshold;
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime absentDeadline;
    
    private String applyDays;  // 如 "1,2,3,4,5" 表示周一到周五
    private Integer allowLateCount;  // 允许晚归次数/月
    private Integer isDefault;
    private Integer status;  // 1启用 0禁用
    private String remark;
    
    // 位置验证字段
    private BigDecimal allowedLatitude;  // 允许打卡纬度
    private BigDecimal allowedLongitude;  // 允许打卡经度
    private Integer allowedRadius;        // 允许范围半径(米)，默认500
    private Integer requireLocation;      // 是否必须上传定位: 1是 0否
    private Integer maxLocationAccuracy;  // 最大允许定位误差(米)
    private Integer exceptionThreshold;   // 异常预警阈值
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    // 关联字段
    private String buildingName;
}
