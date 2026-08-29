-- 检查计划：创建人、楼层范围（与 Mapper / 模型对齐）
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'inspection_plans' AND COLUMN_NAME = 'creator_id');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE inspection_plans ADD COLUMN creator_id BIGINT COMMENT ''创建人ID'' AFTER completed_rooms',
    'SELECT 1 AS skipped');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'inspection_plans' AND COLUMN_NAME = 'floor_range');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE inspection_plans ADD COLUMN floor_range VARCHAR(50) COMMENT ''楼层范围'' AFTER creator_id',
    'SELECT 1 AS skipped');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
