package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Building {
    private Long id;
    private String name;           // 楼栋名称，如 "1号楼"
    private Integer floors;        // 楼层数
    private Integer roomsPerFloor; // 每层房间数
    private String genderType;     // MALE, FEMALE, MIXED
    private String genderLimit;    // 性别限制: MALE/FEMALE/MIXED(通用)
    private String manager;        // 宿管姓名
    private String managerPhone;   // 宿管电话
    private String remark;         // 备注
    private Integer status;        // 1启用, 0停用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 计算总房间数
    public Integer getTotalRooms() {
        if (floors != null && roomsPerFloor != null) {
            return floors * roomsPerFloor;
        }
        return 0;
    }
}