package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Announcement {
    private Long id;
    private String title;          // 公告标题
    private String content;         // 公告内容
    private Integer type;           // 类型：0普通公告, 1重要通知, 2紧急通知
    private Integer status;         // 状态：0草稿, 1已发布, 2已下线
    private Long publisherId;      // 发布人ID
    private String publisherName;   // 发布人姓名
    private LocalDateTime publishTime; // 发布时间
    private Integer viewCount;      // 浏览次数
    private Integer isTop;          // 是否置顶：0否, 1是
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}