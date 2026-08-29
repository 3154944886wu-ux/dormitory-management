package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class Student {
    private Long id;
    private String studentNo;      // 学号
    private String name;           // 姓名
    private String gender;         // 性别：男/女
    private String phone;          // 联系电话
    private String department;     // 院系
    private String className;      // 班级
    private Integer collegeId;     // 所属学院ID
    private Integer majorId;       // 所属专业ID
    private Long dormBatchId;      // 参与选宿批次ID
    private String idCard;         // 身份证号
    private Long userId;           // 关联用户ID
    private Long roomId;           // 入住房间ID
    private String bedNumber;      // 床位号
    private LocalDateTime checkInDate;   // 入住日期
    private LocalDateTime checkOutDate;  // 退宿日期
    private Integer status;        // 1在住, 0已退宿
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段（非数据库）
    private String buildingName;   // 楼栋名称
    private String roomNumber;     // 房间号
    private Long buildingId;
    private Integer floor;
    private String roomType;
    private Integer occupancy;
    private Integer capacity;
    private List<Map<String, Object>> roommates; // 同房间其他学生
    private String roommateNames;  // 室友姓名展示（顿号分隔）
}