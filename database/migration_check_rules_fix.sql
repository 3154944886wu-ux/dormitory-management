-- 补全 check_rules 表缺失字段（幂等，可重复执行）
-- 用法: mysql -u root -p dormitory < migration_check_rules_fix.sql

USE dormitory;

SET NAMES utf8mb4;

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

CALL add_column_if_missing('check_rules', 'absent_deadline', "TIME NULL COMMENT '未归判定截止时间(如00:00)' AFTER late_threshold");
CALL add_column_if_missing('check_rules', 'require_location', "TINYINT DEFAULT 1 COMMENT '是否必须定位打卡: 1是, 0否' AFTER allowed_radius");
CALL add_column_if_missing('check_rules', 'max_location_accuracy', "INT DEFAULT 200 COMMENT '最大允许定位误差(米)' AFTER require_location");
CALL add_column_if_missing('check_rules', 'exception_threshold', "INT DEFAULT 3 COMMENT '异常预警阈值' AFTER max_location_accuracy");

UPDATE check_rules
SET absent_deadline = COALESCE(absent_deadline, check_end_time),
    require_location = COALESCE(require_location, 1),
    max_location_accuracy = COALESCE(max_location_accuracy, 200),
    exception_threshold = COALESCE(exception_threshold, 3);

DROP PROCEDURE IF EXISTS add_column_if_missing;
