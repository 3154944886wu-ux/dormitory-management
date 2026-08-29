-- 探索式测试缺陷收口：异常唯一键、检查记录每计划每房一次、请假紧急联系人
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'check_exceptions' AND INDEX_NAME = 'uk_student_date_type');
SET @sql := IF(@exist = 0,
    'ALTER TABLE check_exceptions ADD UNIQUE INDEX uk_student_date_type (student_id, exception_date, exception_type)',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.statistics
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'inspection_records' AND INDEX_NAME = 'uk_plan_room');
SET @sql := IF(@exist = 0,
    'ALTER TABLE inspection_records ADD UNIQUE INDEX uk_plan_room (plan_id, room_id)',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.columns
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'leave_requests' AND COLUMN_NAME = 'emergency_contact');
SET @sql := IF(@exist = 0,
    'ALTER TABLE leave_requests ADD COLUMN emergency_contact VARCHAR(100) COMMENT ''紧急联系人'' AFTER contact_phone',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
