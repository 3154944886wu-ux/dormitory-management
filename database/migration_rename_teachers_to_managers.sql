-- 若已执行过 migration_teachers.sql（teachers 表），执行本脚本重命名为 managers
-- 幂等：仅当 teachers 存在且 managers 不存在时才重命名，避免在新版 schema 上报错
SET @has_teachers = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'teachers');
SET @has_managers = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'managers');
SET @sql = IF(@has_teachers > 0 AND @has_managers = 0,
    'RENAME TABLE teachers TO managers',
    'SELECT 1 AS skipped');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
