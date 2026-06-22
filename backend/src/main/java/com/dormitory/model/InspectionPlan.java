package com.dormitory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 安全卫生检查计划
 */
@Data
public class InspectionPlan {
    private Long id;
    private String name;                    // 计划名称
    private String description;             // 计划描述
    private String inspectionType;          // SAFETY(安全检查), HYGIENE(卫生检查), COMPREHENSIVE(综合检查)
    private String status;                  // DRAFT, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;        // 计划检查日期
    private String buildingIds;             // 检查楼栋ID列表，逗号分隔
    private String inspectorIds;            // 检查人员ID列表，逗号分隔
    private Integer totalRooms;             // 总房间数
    private Integer completedRooms;         // 已完成房间数
    private Long creatorId;                 // 创建人ID
    private String floorRange;              // 楼层范围(模型级字段，如 "1-6")

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 关联字段（非数据库字段，由 JOIN 查询填充）
    private String creatorName;             // 创建人姓名
    private String buildingNames;           // 楼栋名称（用于展示）
}
