-- 增量迁移：安全检查项模板表（若已从旧版 schema.sql 初始化且缺少该表）
CREATE TABLE IF NOT EXISTS inspection_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '检查项名称',
    category VARCHAR(50) COMMENT '检查类别: SAFETY-安全, HYGIENE-卫生',
    standard VARCHAR(500) COMMENT '检查标准描述',
    max_score DECIMAL(5, 2) DEFAULT 10.00 COMMENT '最高分值',
    status TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1启用',
    sort_order INT DEFAULT 0 COMMENT '排序序号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全检查项模板表';

INSERT IGNORE INTO inspection_items (id, name, category, standard, max_score, status, sort_order) VALUES
(1, '地面清洁', 'HYGIENE', '地面无垃圾、无污渍，清扫干净', 10.00, 1, 1),
(2, '床铺整理', 'HYGIENE', '被褥叠放整齐，床单平整', 10.00, 1, 2),
(3, '物品摆放', 'HYGIENE', '个人物品摆放整齐有序，不占用公共通道', 10.00, 1, 3),
(4, '门窗玻璃', 'HYGIENE', '门窗玻璃干净明亮，无灰尘', 5.00, 1, 4),
(5, '卫生间清洁', 'HYGIENE', '卫生间无异味、无污垢，洁具干净', 10.00, 1, 5),
(6, '违规电器', 'SAFETY', '无电炉、热得快、电热毯等违规电器', 15.00, 1, 6),
(7, '电线线路', 'SAFETY', '无私拉乱接电线，线路整齐规范', 10.00, 1, 7),
(8, '消防设施', 'SAFETY', '灭火器、消防栓完好，消防通道畅通', 10.00, 1, 8),
(9, '阳台安全', 'SAFETY', '阳台无堆放易燃物，栏杆牢固', 10.00, 1, 9),
(10, '门窗锁具', 'SAFETY', '门窗锁具完好，能正常使用', 10.00, 1, 10);
