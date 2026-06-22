-- 宿舍管理系统数据库初始化脚本
-- 使用说明：
-- 1. 确保MySQL服务已启动
-- 2. 使用 root 或有权限的用户执行：mysql -u root -p < schema.sql
-- 3. 或在MySQL客户端中执行：source /path/to/schema.sql

-- 创建数据库
CREATE DATABASE IF NOT EXISTS dormitory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dormitory;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    nickname VARCHAR(50) COMMENT '昵称',
    role VARCHAR(20) DEFAULT 'STUDENT' COMMENT '角色: ADMIN, MANAGER, STUDENT',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态: 1启用, 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 楼栋表
CREATE TABLE IF NOT EXISTS buildings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '楼栋名称',
    floors INT DEFAULT 6 COMMENT '楼层数',
    rooms_per_floor INT DEFAULT 20 COMMENT '每层房间数',
    gender_type VARCHAR(10) DEFAULT 'MIXED' COMMENT '性别类型: MALE, FEMALE, MIXED',
    gender_limit VARCHAR(10) DEFAULT 'MIXED' COMMENT '性别限制: MALE/FEMALE/MIXED(通用)',
    manager VARCHAR(50) COMMENT '宿管姓名',
    manager_phone VARCHAR(20) COMMENT '宿管电话',
    remark VARCHAR(500) COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态: 1启用, 0停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼栋表';

-- manager 管理范围表（宿管按楼栋、辅导员按班级）
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

-- 房间表
CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    building_id BIGINT NOT NULL COMMENT '所属楼栋ID',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    floor INT NOT NULL COMMENT '楼层',
    capacity INT DEFAULT 4 COMMENT '床位数',
    current_count INT DEFAULT 0 COMMENT '当前入住人数',
    status TINYINT DEFAULT 1 COMMENT '状态: 1可用, 0停用',
    room_type VARCHAR(20) DEFAULT '4人间' COMMENT '房间规格(如4人间/2人间)',
    window_beds_count INT DEFAULT 2 COMMENT '靠窗床位数量',
    corridor_beds_count INT DEFAULT 2 COMMENT '靠走廊床位数量',
    special_tag VARCHAR(50) COMMENT '特殊标签(无障碍/伤病员)',
    is_active TINYINT DEFAULT 1 COMMENT '是否启用(1是/0否)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE,
    UNIQUE KEY uk_building_room (building_id, room_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表';

-- 学生表
CREATE TABLE IF NOT EXISTS students (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    phone VARCHAR(20) COMMENT '联系电话',
    department VARCHAR(100) COMMENT '院系',
    class_name VARCHAR(50) COMMENT '班级',
    college_id INT COMMENT '所属学院ID',
    major_id INT COMMENT '所属专业ID',
    dorm_batch_id INT COMMENT '参与选宿批次ID',
    id_card VARCHAR(18) COMMENT '身份证号',
    user_id BIGINT COMMENT '关联用户ID',
    room_id BIGINT COMMENT '入住房间ID',
    bed_number VARCHAR(10) COMMENT '床位号',
    check_in_date DATETIME COMMENT '入住日期',
    check_out_date DATETIME COMMENT '退宿日期',
    status TINYINT DEFAULT 1 COMMENT '状态: 1在住, 0已退宿',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 管理人员表
CREATE TABLE IF NOT EXISTS managers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_no VARCHAR(6) NOT NULL UNIQUE COMMENT '工号（6位）',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    user_id BIGINT COMMENT '关联用户ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 1在职, 0停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY uk_manager_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理人员表';

-- 访客表
CREATE TABLE IF NOT EXISTS visitors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL COMMENT '被访房间ID',
    visitor_name VARCHAR(50) NOT NULL COMMENT '访客姓名',
    visitor_phone VARCHAR(20) NOT NULL COMMENT '访客电话',
    visitor_id_card VARCHAR(18) COMMENT '访客身份证号',
    relation VARCHAR(50) COMMENT '与被访人关系',
    purpose VARCHAR(200) COMMENT '来访目的',
    visit_time DATETIME NOT NULL COMMENT '来访时间',
    leave_time DATETIME COMMENT '离开时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 1在访, 0已离开',
    note TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访客记录表';

-- 报修表
CREATE TABLE IF NOT EXISTS repairs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL COMMENT '房间ID',
    student_id BIGINT COMMENT '报修学生ID',
    type VARCHAR(30) NOT NULL COMMENT '报修类型: 电器/水管/门窗/家具/其他',
    description TEXT NOT NULL COMMENT '问题描述',
    images VARCHAR(500) COMMENT '图片URL，逗号分隔',
    status TINYINT DEFAULT 0 COMMENT '状态: 0待处理, 1处理中, 2已完成, 3已关闭',
    handler VARCHAR(50) COMMENT '处理人',
    handler_note TEXT COMMENT '处理备注',
    handle_time DATETIME COMMENT '处理时间',
    complete_time DATETIME COMMENT '完成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修记录表';

-- 水电费表
CREATE TABLE IF NOT EXISTS utility_fees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL COMMENT '房间ID',
    year INT NOT NULL COMMENT '年份',
    month INT NOT NULL COMMENT '月份',
    electricity_start DECIMAL(10,2) COMMENT '电表起始读数',
    electricity_end DECIMAL(10,2) COMMENT '电表结束读数',
    electricity_usage DECIMAL(10,2) COMMENT '用电量(度)',
    electricity_fee DECIMAL(10,2) COMMENT '电费',
    water_start DECIMAL(10,2) COMMENT '水表起始读数',
    water_end DECIMAL(10,2) COMMENT '水表结束读数',
    water_usage DECIMAL(10,2) COMMENT '用水量(吨)',
    water_fee DECIMAL(10,2) COMMENT '水费',
    total_fee DECIMAL(10,2) COMMENT '总费用',
    status TINYINT DEFAULT 0 COMMENT '缴费状态: 0未缴, 1已缴',
    pay_time DATETIME COMMENT '缴费时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    UNIQUE KEY uk_room_year_month (room_id, year, month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='水电费表';

-- 归寝打卡记录表
CREATE TABLE IF NOT EXISTS check_in_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    room_id BIGINT COMMENT '房间ID',
    check_date DATE NOT NULL COMMENT '打卡日期',
    check_time DATETIME COMMENT '打卡时间',
    check_type TINYINT DEFAULT 0 COMMENT '打卡方式: 0定位, 1人脸, 2手动',
    latitude DECIMAL(10,7) COMMENT '纬度',
    longitude DECIMAL(10,7) COMMENT '经度',
    location_accuracy DECIMAL(10,2) COMMENT '定位精度(米)',
    device_info VARCHAR(200) COMMENT '设备信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    status TINYINT DEFAULT 0 COMMENT '状态: 0正常, 1晚归, 2未归, 3请假',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL,
    UNIQUE KEY uk_student_date (student_id, check_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='归寝打卡记录表';

-- 请假申请表
CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    leave_type TINYINT DEFAULT 0 COMMENT '请假类型: 0事假, 1病假, 2其他',
    reason VARCHAR(500) NOT NULL COMMENT '请假原因',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    destination VARCHAR(200) COMMENT '去向',
    attachment VARCHAR(500) COMMENT '附件URL(请假条等)',
    status TINYINT DEFAULT 0 COMMENT '状态: 0待审批, 1已批准, 2已拒绝, 3已撤销, 4已销假',
    approver_id BIGINT COMMENT '审批人ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approve_time DATETIME COMMENT '审批时间',
    approve_note VARCHAR(500) COMMENT '审批备注',
    actual_return_time DATETIME COMMENT '实际返回时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';

-- 归寝规则表
CREATE TABLE IF NOT EXISTS check_rules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '规则名称',
    building_id BIGINT COMMENT '适用楼栋ID，NULL表示全局规则',
    check_start_time TIME NOT NULL COMMENT '归寝开始时间(如22:00)',
    check_end_time TIME NOT NULL COMMENT '归寝结束时间(如23:00)',
    late_threshold TIME COMMENT '晚归判定时间(如23:30)',
    absent_deadline TIME COMMENT '未归判定截止时间(如00:00)',
    apply_days VARCHAR(20) DEFAULT '1,2,3,4,5' COMMENT '适用日期(周几): 1-7,逗号分隔',
    allow_late_count INT DEFAULT 3 COMMENT '允许晚归次数/月',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认规则',
    status TINYINT DEFAULT 1 COMMENT '状态: 1启用, 0禁用',
    remark VARCHAR(500) COMMENT '备注',
    allowed_latitude DECIMAL(10,7) COMMENT '允许打卡纬度',
    allowed_longitude DECIMAL(10,7) COMMENT '允许打卡经度',
    allowed_radius INT DEFAULT 500 COMMENT '允许范围半径(米)',
    require_location TINYINT DEFAULT 1 COMMENT '是否必须定位打卡: 1是, 0否',
    max_location_accuracy INT DEFAULT 200 COMMENT '最大允许定位误差(米)',
    exception_threshold INT DEFAULT 3 COMMENT '异常预警阈值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='归寝规则表';

-- 归寝异常记录表
CREATE TABLE IF NOT EXISTS check_exceptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    exception_date DATE NOT NULL COMMENT '异常日期',
    exception_type TINYINT NOT NULL COMMENT '异常类型: 1晚归, 2未归, 3缺卡',
    check_record_id BIGINT COMMENT '关联打卡记录ID',
    handled TINYINT DEFAULT 0 COMMENT '是否已处理: 0否, 1是',
    handler_id BIGINT COMMENT '处理人ID',
    handle_result VARCHAR(50) COMMENT '处理结果: safe_return/reported_stay_out/unreachable/other',
    handle_time DATETIME COMMENT '处理时间',
    handle_note VARCHAR(500) COMMENT '处理备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (check_record_id) REFERENCES check_in_records(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='归寝异常记录表';

-- 公告表
CREATE TABLE IF NOT EXISTS announcements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    type TINYINT DEFAULT 0 COMMENT '类型: 0普通公告, 1重要通知, 2紧急通知',
    status TINYINT DEFAULT 0 COMMENT '状态: 0草稿, 1已发布, 2已下线',
    publisher_id BIGINT COMMENT '发布人ID',
    publisher_name VARCHAR(50) COMMENT '发布人姓名',
    publish_time DATETIME COMMENT '发布时间',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶: 0否, 1是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 安全卫生检查计划表
CREATE TABLE IF NOT EXISTS inspection_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '计划名称',
    description VARCHAR(500) COMMENT '计划描述',
    inspection_type VARCHAR(20) NOT NULL COMMENT '检查类型: SAFETY-安全检查, HYGIENE-卫生检查, COMPREHENSIVE-综合检查',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, SCHEDULED-已安排, IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消',
    scheduled_date DATE COMMENT '计划检查日期',
    building_ids VARCHAR(500) COMMENT '检查楼栋ID列表，逗号分隔',
    inspector_ids VARCHAR(500) COMMENT '检查人员ID列表，逗号分隔',
    total_rooms INT DEFAULT 0 COMMENT '总房间数',
    completed_rooms INT DEFAULT 0 COMMENT '已完成房间数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全卫生检查计划表';

-- 安全卫生检查记录表
CREATE TABLE IF NOT EXISTS inspection_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT COMMENT '关联计划ID',
    building_id BIGINT NOT NULL COMMENT '楼栋ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    inspector_id BIGINT NOT NULL COMMENT '检查人ID',
    inspector_name VARCHAR(50) COMMENT '检查人姓名',
    inspection_time DATETIME NOT NULL COMMENT '检查时间',
    overall_score DECIMAL(5,2) COMMENT '总评分',
    result VARCHAR(20) DEFAULT 'PASS' COMMENT '检查结果: PASS-合格, FAIL-不合格',
    items_json TEXT COMMENT '检查项详情JSON',
    photos VARCHAR(1000) COMMENT '照片URL列表，逗号分隔',
    remark TEXT COMMENT '备注',
    need_rectification TINYINT DEFAULT 0 COMMENT '是否需要整改: 0否, 1是',
    rectification_status VARCHAR(20) DEFAULT 'NONE' COMMENT '整改状态: NONE-无需整改, PENDING-待整改, COMPLETED-已整改, VERIFIED-已核实',
    rectification_deadline DATE COMMENT '整改截止日期',
    rectification_photos VARCHAR(1000) COMMENT '整改后照片URL列表',
    rectification_time DATETIME COMMENT '整改完成时间',
    verified_by VARCHAR(50) COMMENT '核实人',
    verified_time DATETIME COMMENT '核实时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (plan_id) REFERENCES inspection_plans(id) ON DELETE SET NULL,
    FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全卫生检查记录表';

-- ================================================================
-- 智能选宿模块表
-- ================================================================

-- 学院表
CREATE TABLE IF NOT EXISTS college (
    id          INT           PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100)  NOT NULL COMMENT '学院名称',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院表';

-- 专业表
CREATE TABLE IF NOT EXISTS major (
    id          INT           PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100)  NOT NULL COMMENT '专业名称',
    college_id  INT           NOT NULL COMMENT '所属学院ID',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (college_id) REFERENCES college(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业表';

-- 床位表
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

-- 选宿批次表
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

-- 批次房源池关联表
CREATE TABLE IF NOT EXISTS batch_room (
    id          BIGINT        PRIMARY KEY AUTO_INCREMENT,
    batch_id    INT           NOT NULL COMMENT '批次ID',
    room_id     BIGINT        NOT NULL COMMENT '房间ID',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (batch_id) REFERENCES dorm_batch(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id)  REFERENCES rooms(id) ON DELETE CASCADE,
    UNIQUE KEY uk_batch_room (batch_id, room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批次房源池关联表';

-- 问卷题目表
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

-- 题目选项表
CREATE TABLE IF NOT EXISTS question_option (
    id           INT           PRIMARY KEY AUTO_INCREMENT,
    q_id         INT           NOT NULL COMMENT '所属题目ID',
    option_text  VARCHAR(100)  NOT NULL COMMENT '选项文本(如"早睡")',
    option_value INT           DEFAULT 0        COMMENT '选项匹配值(相同得该分值)',
    FOREIGN KEY (q_id) REFERENCES questionnaire(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项表';

-- 新生答案表
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

-- 室友组表
CREATE TABLE IF NOT EXISTS roommate_group (
    id          BIGINT        PRIMARY KEY AUTO_INCREMENT,
    batch_id    INT           NOT NULL COMMENT '所属批次ID',
    room_id     BIGINT        COMMENT '分配的宿舍ID',
    member_ids  VARCHAR(500)  COMMENT '成员学生ID列表(逗号分隔,对应students.id)',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (batch_id) REFERENCES dorm_batch(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id)  REFERENCES rooms(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='室友组表';

-- 分配结果表
CREATE TABLE IF NOT EXISTS allocation_result (
    id                BIGINT        PRIMARY KEY AUTO_INCREMENT,
    student_id        BIGINT        NOT NULL COMMENT '学生ID(关联students.id)',
    batch_id          INT           NOT NULL COMMENT '批次ID',
    roommate_group_id BIGINT        COMMENT '室友组ID',
    room_id           BIGINT        COMMENT '分配的宿舍ID',
    bed_id            BIGINT        COMMENT '分配的床位ID',
    match_score         DECIMAL(5,2)  DEFAULT 0.00 COMMENT '匹配度得分',
    reallocation_count  INT           DEFAULT 0 COMMENT '重新匹配次数',
    status              VARCHAR(20)   DEFAULT 'recommended' COMMENT '状态: recommended/confirmed/auto_confirmed/manual_assigned/adjusted',
    created_at        DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id)        REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id)          REFERENCES dorm_batch(id) ON DELETE CASCADE,
    FOREIGN KEY (roommate_group_id) REFERENCES roommate_group(id) ON DELETE SET NULL,
    FOREIGN KEY (room_id)           REFERENCES rooms(id) ON DELETE SET NULL,
    FOREIGN KEY (bed_id)            REFERENCES bed(id) ON DELETE SET NULL,
    UNIQUE KEY uk_student_batch (student_id, batch_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分配结果表';

-- 操作日志表
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

-- 调换申请表
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

-- 通知记录表
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
-- 初始数据
-- ================================================================
/*
-- 插入默认管理员账号（密码: admin123）
INSERT IGNORE INTO users (username, password, nickname, role, status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTx5.Z5CfF8pMKBvKBhQ0Uz9DHy', '系统管理员', 'admin', 1);

-- 插入测试楼栋
INSERT IGNORE INTO buildings (name, floors, rooms_per_floor, gender_type, gender_limit, manager, manager_phone) VALUES
('1号楼', 6, 20, 'MALE', 'MALE', '张三', '13800000001'),
('2号楼', 6, 20, 'FEMALE', 'FEMALE', '李四', '13800000002');

-- 插入测试房间（每栋楼每层5个房间作为示例）
INSERT IGNORE INTO rooms (building_id, room_number, floor, capacity, current_count, status, room_type, window_beds_count, corridor_beds_count, is_active)
SELECT
    b.id,
    CONCAT(LPAD(f.FLOOR, 2, '0'), LPAD(rn, 2, '0')) as room_number,
    f.FLOOR,
    4 as capacity,
    0 as current_count,
    1 as status,
    '4人间' as room_type,
    2 as window_beds_count,
    2 as corridor_beds_count,
    1 as is_active
FROM buildings b
CROSS JOIN (
    SELECT 1 as FLOOR UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6
) f
CROSS JOIN (
    SELECT 1 as rn UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
) room_nums;

-- 插入测试公告
INSERT IGNORE INTO announcements (title, content, type, status, publisher_name, publish_time, view_count, is_top) VALUES
('欢迎使用宿舍管理系统', '本系统用于管理学生宿舍信息，包括楼栋、房间、学生入住、访客登记、报修等功能。', 1, 1, 'admin', NOW(), 0, 0),
('宿舍安全须知', '请注意用电安全，禁止使用大功率电器。访客请在规定时间内登记来访。', 2, 1, 'admin', NOW(), 0, 1);

-- 示例学院
INSERT IGNORE INTO college (id, name) VALUES
(1, '计算机与人工智能学院'),
(2, '信息科学与技术学院'),
(3, '电气工程学院'),
(4, '机械工程学院'),
(5, '土木工程学院');

-- 示例专业
INSERT IGNORE INTO major (id, name, college_id) VALUES
(1,  '计算机科学与技术',          1),
(2,  '软件工程',                  1),
(3,  '人工智能',                  1),
(4,  '通信工程',                  2),
(5,  '电子信息工程',              2),
(6,  '电气工程及其自动化',        3),
(7,  '机械设计制造及其自动化',    4),
(8,  '土木工程',                  5);
*/