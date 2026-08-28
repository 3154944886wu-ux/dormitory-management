-- 位置验证功能迁移脚本（幂等，可重复执行）
-- 为 check_rules 表添加位置验证字段
-- 西南交通大学犀浦校区

USE dormitory;

SET NAMES utf8mb4;

-- 辅助存储过程：仅在列缺失时添加（幂等）
DROP PROCEDURE IF EXISTS add_column_if_missing;
DELIMITER $$
CREATE PROCEDURE add_column_if_missing(
    IN tbl_name  VARCHAR(128),
    IN col_name  VARCHAR(128),
    IN col_def   TEXT
)
BEGIN
    DECLARE col_exists INT DEFAULT 0;
    SELECT COUNT(*) INTO col_exists
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = tbl_name
      AND COLUMN_NAME = col_name;

    IF col_exists = 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', tbl_name, ' ADD COLUMN ', col_name, ' ', col_def);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

-- 添加位置验证字段（若已存在则跳过）
CALL add_column_if_missing('check_rules', 'allowed_latitude', "DECIMAL(10,7) COMMENT '允许打卡纬度' AFTER remark");
CALL add_column_if_missing('check_rules', 'allowed_longitude', "DECIMAL(10,7) COMMENT '允许打卡经度' AFTER allowed_latitude");
CALL add_column_if_missing('check_rules', 'allowed_radius', "INT DEFAULT 500 COMMENT '允许范围半径(米)' AFTER allowed_longitude");

DROP PROCEDURE IF EXISTS add_column_if_missing;

-- 更新默认规则，设置西南交通大学犀浦校区位置
-- 犀浦校区坐标：纬度 30.7617，经度 103.9656
-- 允许范围：1000米（覆盖主要校园区域）
UPDATE check_rules 
SET allowed_latitude = 30.7617000, 
    allowed_longitude = 103.9656000, 
    allowed_radius = 1000 
WHERE is_default = 1;
