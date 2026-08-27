-- ================================================================
-- 智能选宿系统 - 数据库迁移脚本
-- 基于: 数据库结构说明.txt + 业务逻辑.txt
-- 日期: 2026-05-19
-- 说明: 在现有 dormitory 数据库基础上新增智能选宿相关表与字段
-- 用法: mysql -u root -p < migration_smart_dorm.sql
-- 幂等: 可安全重复执行,已存在的列/表会跳过
-- ================================================================

USE dormitory;

-- 确保使用 utf8mb4 编码，避免中文字符乱码
SET NAMES utf8mb4;


-- ================================================================
-- 辅助存储过程: 安全添加列 (幂等)
-- ================================================================
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


-- ================================================================
-- 第一部分: 现有表字段补充
-- ================================================================

-- 1. buildings 表增加 gender_limit 字段
SET @cd = 'VARCHAR(10) DEFAULT ''MIXED'' COMMENT ''性别限制: MALE/FEMALE/MIXED(通用)'' AFTER gender_type';
CALL add_column_if_missing('buildings', 'gender_limit', @cd);
-- 修复过往非 utf8mb4 连接导致的 COMMENT 乱码
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'buildings' AND COLUMN_NAME = 'gender_limit');
SET @sql = IF(@col_exists > 0,
    CONCAT('ALTER TABLE buildings MODIFY COLUMN gender_limit ', @cd),
    'SELECT 1 AS skipped');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
UPDATE buildings SET gender_limit = gender_type WHERE gender_limit IS NULL;


-- 2. rooms 表增加智能选宿相关字段
SET @cd = 'VARCHAR(20) DEFAULT ''4人间'' COMMENT ''房间规格(如4人间/2人间)'' AFTER status';
CALL add_column_if_missing('rooms', 'room_type', @cd);
SET @cd = 'INT DEFAULT 2 COMMENT ''靠窗床位数量'' AFTER room_type';
CALL add_column_if_missing('rooms', 'window_beds_count', @cd);
SET @cd = 'INT DEFAULT 2 COMMENT ''靠走廊床位数量'' AFTER window_beds_count';
CALL add_column_if_missing('rooms', 'corridor_beds_count', @cd);
SET @cd = 'VARCHAR(50) COMMENT ''特殊标签(无障碍/伤病员)'' AFTER corridor_beds_count';
CALL add_column_if_missing('rooms', 'special_tag', @cd);
SET @cd = 'TINYINT DEFAULT 1 COMMENT ''是否启用(1是/0否)'' AFTER special_tag';
CALL add_column_if_missing('rooms', 'is_active', @cd);

-- 已有房间默认启用
UPDATE rooms SET is_active = 1 WHERE is_active IS NULL;


-- 3. students 表增加学院/专业/缴费/批次字段
SET @cd = 'INT COMMENT ''所属学院ID'' AFTER class_name';
CALL add_column_if_missing('students', 'college_id', @cd);
SET @cd = 'INT COMMENT ''所属专业ID'' AFTER college_id';
CALL add_column_if_missing('students', 'major_id', @cd);
SET @cd = 'INT COMMENT ''参与选宿批次ID'' AFTER major_id';
CALL add_column_if_missing('students', 'dorm_batch_id', @cd);
-- 增加 user_id 关联 users 表
SET @cd = 'BIGINT COMMENT ''关联用户ID'' AFTER id_card';
CALL add_column_if_missing('students', 'user_id', @cd);
-- 外键约束：user_id -> users.id（幂等）
SET @fk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'students' AND CONSTRAINT_NAME = 'fk_student_user');
SET @sql = IF(@fk_exists = 0,
    'ALTER TABLE students ADD CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL',
    'SELECT 1 AS skipped');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
-- 唯一约束：一个学生只能注册一个账号（幂等）
SET @uk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'students' AND INDEX_NAME = 'uk_user_id');
SET @sql = IF(@uk_exists = 0,
    'ALTER TABLE students ADD UNIQUE INDEX uk_user_id (user_id)',
    'SELECT 1 AS skipped');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


-- 4. check_rules 表增加位置验证字段 (若已执行 migration_add_location.sql 可跳过)
SET @cd = 'DECIMAL(10,7) COMMENT ''允许打卡纬度'' AFTER remark';
CALL add_column_if_missing('check_rules', 'allowed_latitude', @cd);
SET @cd = 'DECIMAL(10,7) COMMENT ''允许打卡经度'' AFTER allowed_latitude';
CALL add_column_if_missing('check_rules', 'allowed_longitude', @cd);
SET @cd = 'INT DEFAULT 500 COMMENT ''允许范围半径(米)'' AFTER allowed_longitude';
CALL add_column_if_missing('check_rules', 'allowed_radius', @cd);


-- ================================================================
-- 第二部分: 新建表
-- ================================================================

-- 5. 学院表
CREATE TABLE IF NOT EXISTS college (
    id          INT           PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100)  NOT NULL COMMENT '学院名称',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院表';


-- 6. 专业表
CREATE TABLE IF NOT EXISTS major (
    id          INT           PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100)  NOT NULL COMMENT '专业名称',
    college_id  INT           NOT NULL COMMENT '所属学院ID',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (college_id) REFERENCES college(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业表';


-- 7. 床位表
CREATE TABLE IF NOT EXISTS bed (
    id          BIGINT        PRIMARY KEY AUTO_INCREMENT,
    bed_number  VARCHAR(10)   NOT NULL COMMENT '床位编号(A/B/C/D)',
    room_id     BIGINT        NOT NULL COMMENT '所属房间ID',
    bed_type    VARCHAR(10)   NOT NULL DEFAULT 'corridor' COMMENT '床型: window(靠窗)/corridor(靠走廊)',
    is_occupied TINYINT       DEFAULT 0        COMMENT '是否已被占用(0空/1占)',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    UNIQUE KEY uk_room_bed (room_id, bed_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='床位表';


-- 8. 选宿批次表
CREATE TABLE IF NOT EXISTS dorm_batch (
    id                INT           PRIMARY KEY AUTO_INCREMENT,
    name              VARCHAR(100)  NOT NULL COMMENT '批次名称(如"计算机学院2026级")',
    college_id        INT           NOT NULL COMMENT '绑定的学院ID',
    start_time        DATETIME      COMMENT '问卷开始时间',
    end_time          DATETIME      COMMENT '问卷结束时间',
    confirm_deadline  DATETIME      COMMENT '确认截止时间',
    max_reallocation  INT           DEFAULT 1       COMMENT '允许重分配最大次数',
    allow_mix_major   TINYINT       DEFAULT 0       COMMENT '是否允许跨专业混住(0否/1是)',
    major_bonus       INT           DEFAULT 10      COMMENT '同专业匹配加分值',
    prefer_same_floor TINYINT       DEFAULT 1       COMMENT '是否优先同楼层分配(0否/1是)',
    match_status      VARCHAR(20)   DEFAULT 'pending' COMMENT '批次状态: pending/running/finished',
    create_time       DATETIME      DEFAULT CURRENT_TIMESTAMP,
    update_time       DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (college_id) REFERENCES college(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选宿批次表';


-- 9. 批次房源池关联表
CREATE TABLE IF NOT EXISTS batch_room (
    id          BIGINT        PRIMARY KEY AUTO_INCREMENT,
    batch_id    INT           NOT NULL COMMENT '批次ID',
    room_id     BIGINT        NOT NULL COMMENT '房间ID',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (batch_id) REFERENCES dorm_batch(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id)  REFERENCES rooms(id) ON DELETE CASCADE,
    UNIQUE KEY uk_batch_room (batch_id, room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批次房源池关联表';


-- 10. 问卷题目表
CREATE TABLE IF NOT EXISTS questionnaire (
    id            INT           PRIMARY KEY AUTO_INCREMENT,
    question_text VARCHAR(255)  NOT NULL COMMENT '题目内容',
    question_type VARCHAR(10)   NOT NULL DEFAULT 'match' COMMENT '类型: match(匹配类)/bed(床位类)',
    is_required   TINYINT       DEFAULT 1        COMMENT '是否必填(0否/1是)',
    weight        INT           DEFAULT 1        COMMENT '权重系数',
    is_active     TINYINT       DEFAULT 1        COMMENT '是否启用(0否/1是)',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问卷题目表';


-- 11. 题目选项表
CREATE TABLE IF NOT EXISTS question_option (
    id           INT           PRIMARY KEY AUTO_INCREMENT,
    q_id         INT           NOT NULL COMMENT '所属题目ID',
    option_text  VARCHAR(100)  NOT NULL COMMENT '选项文本(如"早睡")',
    option_value INT           DEFAULT 0        COMMENT '选项匹配值(相同得该分值)',
    FOREIGN KEY (q_id) REFERENCES questionnaire(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项表';


-- 12. 新生答案表
CREATE TABLE IF NOT EXISTS student_answer (
    id          BIGINT        PRIMARY KEY AUTO_INCREMENT,
    student_id  BIGINT        NOT NULL COMMENT '学生ID(关联students.id)',
    q_id        INT           NOT NULL COMMENT '题目ID',
    option_id   INT           NOT NULL COMMENT '选择的选项ID',
    submit_time DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (q_id)       REFERENCES questionnaire(id) ON DELETE CASCADE,
    FOREIGN KEY (option_id)  REFERENCES question_option(id) ON DELETE CASCADE,
    UNIQUE KEY uk_student_question (student_id, q_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新生答案表';


-- 13. 室友组表
CREATE TABLE IF NOT EXISTS roommate_group (
    id          BIGINT        PRIMARY KEY AUTO_INCREMENT,
    batch_id    INT           NOT NULL COMMENT '所属批次ID',
    room_id     BIGINT        COMMENT '分配的宿舍ID',
    member_ids  VARCHAR(500)  COMMENT '成员学生ID列表(逗号分隔,对应students.id)',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (batch_id) REFERENCES dorm_batch(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id)  REFERENCES rooms(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='室友组表';


-- 14. 分配结果表
CREATE TABLE IF NOT EXISTS allocation_result (
    id                BIGINT        PRIMARY KEY AUTO_INCREMENT,
    student_id        BIGINT        NOT NULL COMMENT '学生ID(关联students.id)',
    batch_id          INT           NOT NULL COMMENT '批次ID',
    roommate_group_id BIGINT        COMMENT '室友组ID',
    room_id           BIGINT        COMMENT '分配的宿舍ID',
    bed_id            BIGINT        COMMENT '分配的床位ID',
    match_score       DECIMAL(5,2)  DEFAULT 0.00 COMMENT '匹配度得分',
    reallocation_count INT          DEFAULT 0 COMMENT '重新匹配次数',
    status            VARCHAR(20)   DEFAULT 'recommended' COMMENT '状态: recommended/confirmed/auto_confirmed/manual_assigned/adjusted',
    created_at        DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id)        REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id)          REFERENCES dorm_batch(id) ON DELETE CASCADE,
    FOREIGN KEY (roommate_group_id) REFERENCES roommate_group(id) ON DELETE SET NULL,
    FOREIGN KEY (room_id)           REFERENCES rooms(id) ON DELETE SET NULL,
    FOREIGN KEY (bed_id)            REFERENCES bed(id) ON DELETE SET NULL,
    UNIQUE KEY uk_student_batch (student_id, batch_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分配结果表';

-- 兼容已存在 allocation_result 的旧库：补充 reallocation_count 列
CALL add_column_if_missing('allocation_result', 'reallocation_count', "INT DEFAULT 0 COMMENT '重新匹配次数' AFTER match_score");


-- 15. 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id            BIGINT        PRIMARY KEY AUTO_INCREMENT,
    student_id    BIGINT        COMMENT '被操作学生ID(关联students.id)',
    operator_type VARCHAR(20)   NOT NULL COMMENT '操作者类型: student/admin/system',
    operator_id   VARCHAR(50)   COMMENT '操作者标识(学生学号或管理员用户名)',
    action        VARCHAR(50)   NOT NULL COMMENT '动作描述(确认宿舍/智能重匹配/手动调换/管理员修改)',
    detail        TEXT          COMMENT '操作详情JSON',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';


-- 15b. 调换申请表
CREATE TABLE IF NOT EXISTS relocation_application (
    id                  BIGINT        PRIMARY KEY AUTO_INCREMENT,
    student_id          BIGINT        NOT NULL COMMENT '申请人ID(关联students.id)',
    batch_id            INT           NOT NULL COMMENT '所属批次ID',
    current_room_id     BIGINT        COMMENT '当前房间ID(快照)',
    current_bed_id      BIGINT        COMMENT '当前床位ID(快照)',
    reason              TEXT          NOT NULL COMMENT '申请理由',
    preferred_building_id BIGINT      COMMENT '偏好楼栋ID(可选)',
    status              VARCHAR(20)   DEFAULT 'pending' COMMENT '状态: pending/approved/rejected/executed',
    reviewed_by         BIGINT        COMMENT '审核人(关联users.id)',
    review_comment      TEXT          COMMENT '审核意见',
    executed_by         BIGINT        COMMENT '执行人(关联users.id)',
    new_room_id         BIGINT        COMMENT '执行后新房间ID',
    new_bed_id          BIGINT        COMMENT '执行后新床位ID',
    created_at          DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id)          REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id)            REFERENCES dorm_batch(id) ON DELETE CASCADE,
    FOREIGN KEY (current_room_id)     REFERENCES rooms(id) ON DELETE SET NULL,
    FOREIGN KEY (current_bed_id)      REFERENCES bed(id) ON DELETE SET NULL,
    FOREIGN KEY (reviewed_by)         REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (executed_by)         REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (new_room_id)         REFERENCES rooms(id) ON DELETE SET NULL,
    FOREIGN KEY (new_bed_id)          REFERENCES bed(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调换申请表';


-- 16. 通知记录表
CREATE TABLE IF NOT EXISTS notification (
    id           BIGINT        PRIMARY KEY AUTO_INCREMENT,
    recipient_id BIGINT        COMMENT '接收学生ID(关联students.id)',
    batch_id     INT           COMMENT '所属批次ID',
    type         VARCHAR(30)   NOT NULL COMMENT '通知类型(推荐生成/确认提醒/分配结果)',
    content      TEXT          COMMENT '通知内容',
    channel      VARCHAR(10)   DEFAULT 'inner' COMMENT '发送渠道: sms/email/inner',
    status       VARCHAR(10)   DEFAULT 'pending' COMMENT '发送状态: pending/sent/failed',
    create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_id) REFERENCES students(id) ON DELETE SET NULL,
    FOREIGN KEY (batch_id)     REFERENCES dorm_batch(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';


-- ================================================================
-- 第三部分: 预置基础数据
-- ================================================================
/*
-- 示例学院
INSERT IGNORE INTO college (id, name) VALUES
(1, '计算机与人工智能学院'),
(2, '信息科学与技术学院'),
(3, '电气工程学院'),
(4, '机械工程学院'),
(5, '土木工程学院');

-- 示例专业 (若 college 表已有匹配 id)
INSERT IGNORE INTO major (id, name, college_id) VALUES
(1,  '计算机科学与技术',          1),
(2,  '软件工程',                  1),
(3,  '人工智能',                  1),
(4,  '通信工程',                  2),
(5,  '电子信息工程',              2),
(6,  '电气工程及其自动化',        3),
(7,  '机械设计制造及其自动化',    4),
(8,  '土木工程',                  5);


-- ================================================================
-- 第四部分: 为现有房间自动补齐床位记录
-- ================================================================

INSERT IGNORE INTO bed (bed_number, room_id, bed_type)
SELECT
    bed_labels.label,
    r.id,
    CASE WHEN bed_labels.idx <= r.window_beds_count THEN 'window' ELSE 'corridor' END
FROM rooms r
CROSS JOIN (
    SELECT 'A' AS label, 1 AS idx UNION ALL
    SELECT 'B', 2 UNION ALL
    SELECT 'C', 3 UNION ALL
    SELECT 'D', 4
) bed_labels
WHERE r.is_active = 1
  AND bed_labels.idx <= (r.window_beds_count + r.corridor_beds_count);
*/

-- ================================================================
-- 清理辅助存储过程
-- ================================================================
DROP PROCEDURE IF EXISTS add_column_if_missing;


-- ================================================================
-- 汇总
-- ================================================================
-- 原系统 14 张表:
--   users, buildings, rooms, students, visitors, repairs,
--   utility_fees, check_in_records, check_rules, check_exceptions,
--   leave_requests, announcements, inspection_plans, inspection_records
--
-- 智能选宿新增 12 张表:
--   college, major, bed, dorm_batch, batch_room,
--   questionnaire, question_option, student_answer,
--   roommate_group, allocation_result, operation_log, notification
--
-- ALTER 补充: buildings(+1), rooms(+5), students(+4), check_rules(+3)
-- ================================================================