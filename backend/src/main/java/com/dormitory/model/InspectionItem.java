package com.dormitory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 检查项模板
 */
@Data
public class InspectionItem {
    private Long id;
    private String name;                    // 检查项名称
    private String category;                // SAFETY(安全), HYGIENE(卫生)
    private String standard;                // 检查标准描述
    private BigDecimal maxScore;            // 最高分值
    private Integer status;                 // 0禁用, 1启用
    private Integer sortOrder;              // 排序序号

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
