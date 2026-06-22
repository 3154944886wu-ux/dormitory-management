package com.dormitory.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UtilityFee {
    private Long id;
    private Long roomId;              // 房间ID
    private Integer year;             // 年份
    private Integer month;            // 月份
    private BigDecimal electricityStart;  // 电表起始读数
    private BigDecimal electricityEnd;    // 电表结束读数
    private BigDecimal electricityUsage;  // 用电量
    private BigDecimal electricityFee;     // 电费
    private BigDecimal waterStart;        // 水表起始读数
    private BigDecimal waterEnd;          // 水表结束读数
    private BigDecimal waterUsage;         // 用水量
    private BigDecimal waterFee;           // 水费
    private BigDecimal totalFee;           // 总费用
    private Integer status;            // 0未缴费, 1已缴费
    private LocalDateTime payTime;     // 缴费时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private String buildingName;       // 楼栋名称
    private String roomNumber;         // 房间号
}