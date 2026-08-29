-- 楼栋名称唯一 + 归寝异常 (学生,日期,类型) 唯一（幂等）
USE dormitory;

SET NAMES utf8mb4;

-- 重复楼栋名：保留最小 id，其余追加 -id 后缀
UPDATE buildings b
JOIN (
    SELECT name, MIN(id) AS keep_id
    FROM buildings
    GROUP BY name
    HAVING COUNT(*) > 1
) d ON b.name = d.name AND b.id <> d.keep_id
SET b.name = CONCAT(b.name, '-', b.id);

SET @uk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'buildings' AND INDEX_NAME = 'uk_building_name');
SET @sql = IF(@uk_exists = 0,
    'ALTER TABLE buildings ADD UNIQUE INDEX uk_building_name (name)',
    'SELECT 1 AS skipped');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 重复异常：保留最小 id
DELETE e1 FROM check_exceptions e1
INNER JOIN check_exceptions e2
    ON e1.student_id = e2.student_id
   AND e1.exception_date = e2.exception_date
   AND e1.exception_type = e2.exception_type
   AND e1.id > e2.id;

SET @uk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'check_exceptions' AND INDEX_NAME = 'uk_student_date_type');
SET @sql = IF(@uk_exists = 0,
    'ALTER TABLE check_exceptions ADD UNIQUE INDEX uk_student_date_type (student_id, exception_date, exception_type)',
    'SELECT 1 AS skipped');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
