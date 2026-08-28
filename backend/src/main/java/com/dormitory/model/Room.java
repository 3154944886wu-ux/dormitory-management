package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Room {
    private Long id;
    private Long buildingId;        // 所属楼栋ID
    private String roomNumber;      // 房间号，如 "101", "201"
    private Integer floor;          // 楼层
    private Integer capacity;       // 容纳人数（床位数）
    private Integer currentCount;   // 当前入住人数（选宿 CAS 用列，可能漂移）
    private Integer occupancy;      // 实际在住人数（查询时 COUNT 在住学生）
    private Integer status;         // 1可用, 0停用
    private String roomType;        // 房间规格(如4人间/2人间)
    private Integer windowBedsCount;    // 靠窗床位数量
    private Integer corridorBedsCount;  // 靠走廊床位数量
    private String specialTag;      // 特殊标签(无障碍/伤病员)
    private Integer isActive;       // 是否启用(1是/0否)

    // 关联字段
    private String buildingName;    // 楼栋名称（查询时填充）
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    /** 闸门用实际在住人数，缺省才回退到 current_count。 */
    public int usedCount() {
        if (occupancy != null) {
            return occupancy;
        }
        return currentCount != null ? currentCount : 0;
    }

    public Integer getAvailableBeds() {
        if (capacity == null) {
            return 0;
        }
        return Math.max(0, capacity - usedCount());
    }
    
    public Boolean isFull() {
        return capacity != null && usedCount() >= capacity;
    }
}