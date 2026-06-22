-- 访客表
CREATE TABLE IF NOT EXISTS visitors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL COMMENT '被访房间ID',
    visitor_name VARCHAR(50) NOT NULL COMMENT '访客姓名',
    visitor_phone VARCHAR(20) NOT NULL COMMENT '访客电话',
    visitor_id_card VARCHAR(18) COMMENT '访客身份证号',
    relation VARCHAR(50) COMMENT '与被访人关系',
    purpose VARCHAR(200) COMMENT '来访目的',
    visit_time DATETIME NOT NULL COMMENT '来访时间',
    leave_time DATETIME COMMENT '离开时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 1在访, 0已离开',
    note TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访客记录表';