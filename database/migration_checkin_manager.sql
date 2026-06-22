-- 归寝管理 manager 角色、电子围栏策略、异常闭环与审计升级脚本

ALTER TABLE users
    MODIFY role VARCHAR(20) DEFAULT 'STUDENT' COMMENT '角色: ADMIN, MANAGER, STUDENT';

CREATE TABLE IF NOT EXISTS manager_scope (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT 'manager账号ID',
    building_id BIGINT COMMENT '管理楼栋ID，为NULL表示不按楼栋限制',
    class_name VARCHAR(50) COMMENT '管理班级，为NULL表示不按班级限制',
    status TINYINT DEFAULT 1 COMMENT '状态: 1启用, 0停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE,
    INDEX idx_manager_scope_user (user_id),
    INDEX idx_manager_scope_building (building_id),
    INDEX idx_manager_scope_class (class_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='manager管理范围表';

ALTER TABLE check_rules
    ADD COLUMN absent_deadline TIME COMMENT '未归判定截止时间(如00:00)' AFTER late_threshold,
    ADD COLUMN require_location TINYINT DEFAULT 1 COMMENT '是否必须定位打卡: 1是, 0否' AFTER allowed_radius,
    ADD COLUMN max_location_accuracy INT DEFAULT 200 COMMENT '最大允许定位误差(米)' AFTER require_location,
    ADD COLUMN exception_threshold INT DEFAULT 3 COMMENT '异常预警阈值' AFTER max_location_accuracy;

ALTER TABLE check_exceptions
    ADD COLUMN handle_result VARCHAR(50) COMMENT '处理结果: safe_return/reported_stay_out/unreachable/other' AFTER handler_id;

UPDATE check_rules
SET absent_deadline = COALESCE(absent_deadline, check_end_time),
    require_location = COALESCE(require_location, 1),
    max_location_accuracy = COALESCE(max_location_accuracy, 200),
    exception_threshold = COALESCE(exception_threshold, 3);
