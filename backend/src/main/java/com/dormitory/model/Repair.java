package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Repair {
    private Long id;
    private Long studentId;        // 报修学生ID
    private Long roomId;           // 房间ID
    private String type;           // 报修类型：电器/水管/门窗/家具/其他
    private String description;    // 问题描述
    private String images;         // 图片URL（逗号分隔）
    private Integer status;        // 0待处理, 1处理中, 2已完成, 3已关闭
    private String handler;        // 处理人
    private String handlerNote;    // 处理备注
    private LocalDateTime handleTime;  // 处理时间
    private LocalDateTime completeTime;// 完成时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private String studentName;    // 学生姓名
    private String studentNo;      // 学号
    private String roomNumber;     // 房间号
    private String buildingName;   // 楼栋名称
    private Long buildingId;
    private String className;
}