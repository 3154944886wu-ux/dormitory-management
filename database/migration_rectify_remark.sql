-- 检查整改说明独立列（幂等），避免覆盖 inspection_records.remark
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

CALL add_column_if_missing('inspection_records', 'rectify_remark', "TEXT COMMENT '整改说明（与检查备注分离）' AFTER rectification_time");

DROP PROCEDURE IF EXISTS add_column_if_missing;
