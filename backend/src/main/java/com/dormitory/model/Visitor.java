package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Visitor {
    private Long id;
    private Long roomId;              // 被访房间ID
    private String visitorName;      // 访客姓名
    private String visitorPhone;     // 访客电话
    private String visitorIdCard;    // 访客身份证号
    private String relation;         // 与被访人关系
    private String purpose;          // 来访目的
    private LocalDateTime visitTime; // 来访时间
    private LocalDateTime leaveTime; // 离开时间
    private Integer status;          // 1在访, 0已离开
    private String note;             // 备注
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private String buildingName;     // 楼栋名称
    private String roomNumber;       // 房间号
}