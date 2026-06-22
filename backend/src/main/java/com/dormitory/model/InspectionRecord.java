package com.dormitory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 安全检查记录
 */
@Data
public class InspectionRecord {
    private Long id;
    private Long planId;                    // 关联计划ID
    private Long buildingId;                // 楼栋ID
    private Long roomId;                    // 房间ID
    private Long inspectorId;               // 检查人ID
    private String inspectorName;           // 检查人姓名
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inspectionTime;   // 检查时间
    private BigDecimal overallScore;        // 总评分 0-100
    private String result;                  // PASS(合格), FAIL(不合格)
    private String itemsJson;               // 检查项详情JSON
    private String photos;                  // 照片URL列表，逗号分隔
    private String remark;                  // 备注
    private Boolean needRectification;      // 是否需要整改
    private String rectificationStatus;     // NONE, PENDING, COMPLETED, VERIFIED
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rectificationDeadline; // 整改截止日期
    private String rectificationPhotos;     // 整改后照片URL列表
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rectificationTime; // 整改完成时间
    private String verifiedBy;              // 核实人
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verifiedTime;     // 核实时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 关联字段（非数据库字段，由 JOIN 查询填充）
    private String roomNumber;              // 房间号
    private String buildingName;            // 楼栋名称
    private String rectifyRemark;           // 整改备注(模型级字段)
}
