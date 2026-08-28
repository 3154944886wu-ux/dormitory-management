-- CONVENIENCE SNAPSHOT ONLY — NOT a second source of truth.
-- Canonical init path: database/schema.sql (+ test_data.sql / migrations per database/MIGRATIONS.md).
-- Regenerated from the golden schema (schema-only, no environment INSERT data).
-- Do not add payment_status or other unused dump columns here.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `allocation_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `allocation_result` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID(关联students.id)',
  `batch_id` int NOT NULL COMMENT '批次ID',
  `roommate_group_id` bigint DEFAULT NULL COMMENT '室友组ID',
  `room_id` bigint DEFAULT NULL COMMENT '分配的宿舍ID',
  `bed_id` bigint DEFAULT NULL COMMENT '分配的床位ID',
  `match_score` decimal(5,2) DEFAULT '0.00' COMMENT '匹配度得分',
  `reallocation_count` int DEFAULT '0' COMMENT '重新匹配次数',
  `status` varchar(20) DEFAULT 'recommended' COMMENT '状态: recommended/confirmed/auto_confirmed/manual_assigned/adjusted',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_batch` (`student_id`,`batch_id`),
  KEY `batch_id` (`batch_id`),
  KEY `roommate_group_id` (`roommate_group_id`),
  KEY `room_id` (`room_id`),
  KEY `bed_id` (`bed_id`),
  CONSTRAINT `allocation_result_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `allocation_result_ibfk_2` FOREIGN KEY (`batch_id`) REFERENCES `dorm_batch` (`id`) ON DELETE CASCADE,
  CONSTRAINT `allocation_result_ibfk_3` FOREIGN KEY (`roommate_group_id`) REFERENCES `roommate_group` (`id`) ON DELETE SET NULL,
  CONSTRAINT `allocation_result_ibfk_4` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL,
  CONSTRAINT `allocation_result_ibfk_5` FOREIGN KEY (`bed_id`) REFERENCES `bed` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='分配结果表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `announcements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcements` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `type` tinyint DEFAULT '0' COMMENT '类型: 0普通公告, 1重要通知, 2紧急通知',
  `status` tinyint DEFAULT '0' COMMENT '状态: 0草稿, 1已发布, 2已下线',
  `publisher_id` bigint DEFAULT NULL COMMENT '发布人ID',
  `publisher_name` varchar(50) DEFAULT NULL COMMENT '发布人姓名',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `is_top` tinyint DEFAULT '0' COMMENT '是否置顶: 0否, 1是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `batch_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_room` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `batch_id` int NOT NULL COMMENT '批次ID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_batch_room` (`batch_id`,`room_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `batch_room_ibfk_1` FOREIGN KEY (`batch_id`) REFERENCES `dorm_batch` (`id`) ON DELETE CASCADE,
  CONSTRAINT `batch_room_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='批次房源池关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `bed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bed` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bed_number` varchar(10) NOT NULL COMMENT '床位编号(A/B/C/D)',
  `room_id` bigint NOT NULL COMMENT '所属房间ID',
  `bed_type` varchar(10) NOT NULL DEFAULT 'corridor' COMMENT '床型: window(靠窗)/corridor(靠走廊)',
  `is_occupied` tinyint DEFAULT '0' COMMENT '是否已被占用(0空/1占)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_bed` (`room_id`,`bed_number`),
  CONSTRAINT `bed_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='床位表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `buildings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `buildings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '楼栋名称',
  `floors` int DEFAULT '6' COMMENT '楼层数',
  `rooms_per_floor` int DEFAULT '20' COMMENT '每层房间数',
  `gender_type` varchar(10) DEFAULT 'MIXED' COMMENT '性别类型: MALE, FEMALE, MIXED',
  `gender_limit` varchar(10) DEFAULT 'MIXED' COMMENT '性别限制: MALE/FEMALE/MIXED(通用)',
  `manager` varchar(50) DEFAULT NULL COMMENT '宿管姓名',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '宿管电话',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1启用, 0停用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='楼栋表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `check_exceptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `check_exceptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `exception_date` date NOT NULL COMMENT '异常日期',
  `exception_type` tinyint NOT NULL COMMENT '异常类型: 1晚归, 2未归, 3缺卡',
  `check_record_id` bigint DEFAULT NULL COMMENT '关联打卡记录ID',
  `handled` tinyint DEFAULT '0' COMMENT '是否已处理: 0否, 1是',
  `handler_id` bigint DEFAULT NULL COMMENT '处理人ID',
  `handle_result` varchar(50) DEFAULT NULL COMMENT '处理结果: safe_return/reported_stay_out/unreachable/other',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_note` varchar(500) DEFAULT NULL COMMENT '处理备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_date_type` (`student_id`,`exception_date`,`exception_type`),
  KEY `check_record_id` (`check_record_id`),
  CONSTRAINT `check_exceptions_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `check_exceptions_ibfk_2` FOREIGN KEY (`check_record_id`) REFERENCES `check_in_records` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='归寝异常记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `check_in_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `check_in_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `room_id` bigint DEFAULT NULL COMMENT '房间ID',
  `check_date` date NOT NULL COMMENT '打卡日期',
  `check_time` datetime DEFAULT NULL COMMENT '打卡时间',
  `check_type` tinyint DEFAULT '0' COMMENT '打卡方式: 0定位, 1人脸, 2手动',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `location_accuracy` decimal(10,2) DEFAULT NULL COMMENT '定位精度(米)',
  `device_info` varchar(200) DEFAULT NULL COMMENT '设备信息',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `status` tinyint DEFAULT '0' COMMENT '状态: 0正常, 1晚归, 2未归, 3请假',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_date` (`student_id`,`check_date`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `check_in_records_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `check_in_records_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='归寝打卡记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `check_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `check_rules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '规则名称',
  `building_id` bigint DEFAULT NULL COMMENT '适用楼栋ID，NULL表示全局规则',
  `check_start_time` time NOT NULL COMMENT '归寝开始时间(如22:00)',
  `check_end_time` time NOT NULL COMMENT '归寝结束时间(如23:00)',
  `late_threshold` time DEFAULT NULL COMMENT '晚归判定时间(如23:30)',
  `absent_deadline` time DEFAULT NULL COMMENT '未归判定截止时间(如00:00)',
  `apply_days` varchar(20) DEFAULT '1,2,3,4,5' COMMENT '适用日期(周几): 1-7,逗号分隔',
  `allow_late_count` int DEFAULT '3' COMMENT '允许晚归次数/月',
  `is_default` tinyint DEFAULT '0' COMMENT '是否默认规则',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1启用, 0禁用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `allowed_latitude` decimal(10,7) DEFAULT NULL COMMENT '允许打卡纬度',
  `allowed_longitude` decimal(10,7) DEFAULT NULL COMMENT '允许打卡经度',
  `allowed_radius` int DEFAULT '500' COMMENT '允许范围半径(米)',
  `require_location` tinyint DEFAULT '1' COMMENT '是否必须定位打卡: 1是, 0否',
  `max_location_accuracy` int DEFAULT '200' COMMENT '最大允许定位误差(米)',
  `exception_threshold` int DEFAULT '3' COMMENT '异常预警阈值',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `building_id` (`building_id`),
  CONSTRAINT `check_rules_ibfk_1` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='归寝规则表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `college`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `college` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '学院名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学院表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `dorm_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_batch` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '批次名称(如"计算机学院2026级")',
  `college_id` int NOT NULL COMMENT '绑定的学院ID',
  `start_time` datetime DEFAULT NULL COMMENT '问卷开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '问卷结束时间',
  `confirm_deadline` datetime DEFAULT NULL COMMENT '确认截止时间',
  `max_reallocation` int DEFAULT '1' COMMENT '允许重分配最大次数',
  `allow_mix_major` tinyint DEFAULT '0' COMMENT '是否允许跨专业混住(0否/1是)',
  `major_bonus` int DEFAULT '10' COMMENT '同专业匹配加分值',
  `prefer_same_floor` tinyint DEFAULT '1' COMMENT '是否优先同楼层分配(0否/1是)',
  `match_status` varchar(20) DEFAULT 'pending' COMMENT '批次状态: pending/running/finished',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `college_id` (`college_id`),
  CONSTRAINT `dorm_batch_ibfk_1` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='选宿批次表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `inspection_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inspection_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '检查项名称',
  `category` varchar(50) DEFAULT NULL COMMENT '检查类别: SAFETY-安全, HYGIENE-卫生',
  `standard` varchar(500) DEFAULT NULL COMMENT '检查标准描述',
  `max_score` decimal(5,2) DEFAULT '10.00' COMMENT '最高分值',
  `status` tinyint DEFAULT '1' COMMENT '状态: 0禁用, 1启用',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='安全检查项模板表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `inspection_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inspection_plans` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '计划名称',
  `description` varchar(500) DEFAULT NULL COMMENT '计划描述',
  `inspection_type` varchar(20) NOT NULL COMMENT '检查类型: SAFETY-安全检查, HYGIENE-卫生检查, COMPREHENSIVE-综合检查',
  `status` varchar(20) DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, SCHEDULED-已安排, IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消',
  `scheduled_date` date DEFAULT NULL COMMENT '计划检查日期',
  `building_ids` varchar(500) DEFAULT NULL COMMENT '检查楼栋ID列表，逗号分隔',
  `inspector_ids` varchar(500) DEFAULT NULL COMMENT '检查人员ID列表，逗号分隔',
  `total_rooms` int DEFAULT '0' COMMENT '总房间数',
  `completed_rooms` int DEFAULT '0' COMMENT '已完成房间数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='安全卫生检查计划表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `inspection_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inspection_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_id` bigint DEFAULT NULL COMMENT '关联计划ID',
  `building_id` bigint NOT NULL COMMENT '楼栋ID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `inspector_id` bigint NOT NULL COMMENT '检查人ID',
  `inspector_name` varchar(50) DEFAULT NULL COMMENT '检查人姓名',
  `inspection_time` datetime NOT NULL COMMENT '检查时间',
  `overall_score` decimal(5,2) DEFAULT NULL COMMENT '总评分',
  `result` varchar(20) DEFAULT 'PASS' COMMENT '检查结果: PASS-合格, FAIL-不合格',
  `items_json` text COMMENT '检查项详情JSON',
  `photos` varchar(1000) DEFAULT NULL COMMENT '照片URL列表，逗号分隔',
  `remark` text COMMENT '备注',
  `need_rectification` tinyint DEFAULT '0' COMMENT '是否需要整改: 0否, 1是',
  `rectification_status` varchar(20) DEFAULT 'NONE' COMMENT '整改状态: NONE-无需整改, PENDING-待整改, COMPLETED-已整改, VERIFIED-已核实',
  `rectification_deadline` date DEFAULT NULL COMMENT '整改截止日期',
  `rectification_photos` varchar(1000) DEFAULT NULL COMMENT '整改后照片URL列表',
  `rectification_time` datetime DEFAULT NULL COMMENT '整改完成时间',
  `rectify_remark` text COMMENT '整改说明（与检查备注分离）',
  `verified_by` varchar(50) DEFAULT NULL COMMENT '核实人',
  `verified_time` datetime DEFAULT NULL COMMENT '核实时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `plan_id` (`plan_id`),
  KEY `building_id` (`building_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `inspection_records_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `inspection_plans` (`id`) ON DELETE SET NULL,
  CONSTRAINT `inspection_records_ibfk_2` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`) ON DELETE CASCADE,
  CONSTRAINT `inspection_records_ibfk_3` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='安全卫生检查记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `leave_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leave_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `leave_type` tinyint DEFAULT '0' COMMENT '请假类型: 0事假, 1病假, 2其他',
  `reason` varchar(500) NOT NULL COMMENT '请假原因',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `destination` varchar(200) DEFAULT NULL COMMENT '去向',
  `attachment` varchar(500) DEFAULT NULL COMMENT '附件URL(请假条等)',
  `status` tinyint DEFAULT '0' COMMENT '状态: 0待审批, 1已批准, 2已拒绝, 3已撤销, 4已销假',
  `approver_id` bigint DEFAULT NULL COMMENT '审批人ID',
  `approver_name` varchar(50) DEFAULT NULL COMMENT '审批人姓名',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_note` varchar(500) DEFAULT NULL COMMENT '审批备注',
  `actual_return_time` datetime DEFAULT NULL COMMENT '实际返回时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `leave_requests_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='请假申请表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `major` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '专业名称',
  `college_id` int NOT NULL COMMENT '所属学院ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `college_id` (`college_id`),
  CONSTRAINT `major_ibfk_1` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专业表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `manager_scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `manager_scope` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT 'manager账号ID',
  `building_id` bigint DEFAULT NULL COMMENT '管理楼栋ID，为NULL表示不按楼栋限制',
  `class_name` varchar(50) DEFAULT NULL COMMENT '管理班级，为NULL表示不按班级限制',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1启用, 0停用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_manager_scope_user` (`user_id`),
  KEY `idx_manager_scope_building` (`building_id`),
  KEY `idx_manager_scope_class` (`class_name`),
  CONSTRAINT `manager_scope_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `manager_scope_ibfk_2` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='manager管理范围表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `managers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `managers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `employee_no` varchar(6) NOT NULL COMMENT '工号（6位）',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `user_id` bigint DEFAULT NULL COMMENT '关联用户ID',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1在职, 0停用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `employee_no` (`employee_no`),
  UNIQUE KEY `uk_manager_user_id` (`user_id`),
  CONSTRAINT `managers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理人员表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `recipient_id` bigint DEFAULT NULL COMMENT '接收学生ID(关联students.id)',
  `batch_id` int DEFAULT NULL COMMENT '所属批次ID',
  `type` varchar(30) NOT NULL COMMENT '通知类型(推荐生成/确认提醒/分配结果)',
  `content` text COMMENT '通知内容',
  `channel` varchar(10) DEFAULT 'inner' COMMENT '发送渠道: sms/email/inner',
  `status` varchar(10) DEFAULT 'pending' COMMENT '发送状态: pending/sent/failed',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `recipient_id` (`recipient_id`),
  KEY `batch_id` (`batch_id`),
  CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`recipient_id`) REFERENCES `students` (`id`) ON DELETE SET NULL,
  CONSTRAINT `notification_ibfk_2` FOREIGN KEY (`batch_id`) REFERENCES `dorm_batch` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint DEFAULT NULL COMMENT '被操作学生ID(关联students.id)',
  `operator_type` varchar(20) NOT NULL COMMENT '操作者类型: student/admin/system',
  `operator_id` varchar(50) DEFAULT NULL COMMENT '操作者标识(学生学号或管理员用户名)',
  `action` varchar(50) NOT NULL COMMENT '动作描述(确认宿舍/智能重匹配/手动调换/管理员修改)',
  `detail` text COMMENT '操作详情JSON',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `operation_log_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `question_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_option` (
  `id` int NOT NULL AUTO_INCREMENT,
  `q_id` int NOT NULL COMMENT '所属题目ID',
  `option_text` varchar(100) NOT NULL COMMENT '选项文本(如"早睡")',
  `option_value` int DEFAULT '0' COMMENT '选项匹配值(相同得该分值)',
  PRIMARY KEY (`id`),
  KEY `q_id` (`q_id`),
  CONSTRAINT `question_option_ibfk_1` FOREIGN KEY (`q_id`) REFERENCES `questionnaire` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题目选项表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `questionnaire`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questionnaire` (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_text` varchar(255) NOT NULL COMMENT '题目内容',
  `question_type` varchar(10) NOT NULL DEFAULT 'match' COMMENT '类型: match(匹配类)/bed(床位类)',
  `is_required` tinyint DEFAULT '1' COMMENT '是否必填(0否/1是)',
  `weight` int DEFAULT '1' COMMENT '权重系数',
  `is_active` tinyint DEFAULT '1' COMMENT '是否启用(0否/1是)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='问卷题目表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `relocation_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `relocation_application` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '申请人ID(关联students.id)',
  `batch_id` int NOT NULL COMMENT '所属批次ID',
  `current_room_id` bigint DEFAULT NULL COMMENT '当前房间ID(快照)',
  `current_bed_id` bigint DEFAULT NULL COMMENT '当前床位ID(快照)',
  `reason` text NOT NULL COMMENT '申请理由',
  `preferred_building_id` bigint DEFAULT NULL COMMENT '偏好楼栋ID(可选)',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态: pending/approved/rejected/executed',
  `reviewed_by` bigint DEFAULT NULL COMMENT '审核人(关联users.id)',
  `review_comment` text COMMENT '审核意见',
  `executed_by` bigint DEFAULT NULL COMMENT '执行人(关联users.id)',
  `new_room_id` bigint DEFAULT NULL COMMENT '执行后新房间ID',
  `new_bed_id` bigint DEFAULT NULL COMMENT '执行后新床位ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  KEY `batch_id` (`batch_id`),
  KEY `current_room_id` (`current_room_id`),
  KEY `current_bed_id` (`current_bed_id`),
  KEY `reviewed_by` (`reviewed_by`),
  KEY `executed_by` (`executed_by`),
  KEY `new_room_id` (`new_room_id`),
  KEY `new_bed_id` (`new_bed_id`),
  CONSTRAINT `relocation_application_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `relocation_application_ibfk_2` FOREIGN KEY (`batch_id`) REFERENCES `dorm_batch` (`id`) ON DELETE CASCADE,
  CONSTRAINT `relocation_application_ibfk_3` FOREIGN KEY (`current_room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL,
  CONSTRAINT `relocation_application_ibfk_4` FOREIGN KEY (`current_bed_id`) REFERENCES `bed` (`id`) ON DELETE SET NULL,
  CONSTRAINT `relocation_application_ibfk_5` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  CONSTRAINT `relocation_application_ibfk_6` FOREIGN KEY (`executed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  CONSTRAINT `relocation_application_ibfk_7` FOREIGN KEY (`new_room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL,
  CONSTRAINT `relocation_application_ibfk_8` FOREIGN KEY (`new_bed_id`) REFERENCES `bed` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调换申请表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `repairs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repairs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `student_id` bigint DEFAULT NULL COMMENT '报修学生ID',
  `type` varchar(30) NOT NULL COMMENT '报修类型: 电器/水管/门窗/家具/其他',
  `description` text NOT NULL COMMENT '问题描述',
  `images` varchar(500) DEFAULT NULL COMMENT '图片URL，逗号分隔',
  `status` tinyint DEFAULT '0' COMMENT '状态: 0待处理, 1处理中, 2已完成, 3已关闭',
  `handler` varchar(50) DEFAULT NULL COMMENT '处理人',
  `handler_note` text COMMENT '处理备注',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `room_id` (`room_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `repairs_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE,
  CONSTRAINT `repairs_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报修记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `roommate_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roommate_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `batch_id` int NOT NULL COMMENT '所属批次ID',
  `room_id` bigint DEFAULT NULL COMMENT '分配的宿舍ID',
  `member_ids` varchar(500) DEFAULT NULL COMMENT '成员学生ID列表(逗号分隔,对应students.id)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `batch_id` (`batch_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `roommate_group_ibfk_1` FOREIGN KEY (`batch_id`) REFERENCES `dorm_batch` (`id`) ON DELETE CASCADE,
  CONSTRAINT `roommate_group_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='室友组表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `building_id` bigint NOT NULL COMMENT '所属楼栋ID',
  `room_number` varchar(20) NOT NULL COMMENT '房间号',
  `floor` int NOT NULL COMMENT '楼层',
  `capacity` int DEFAULT '4' COMMENT '床位数',
  `current_count` int DEFAULT '0' COMMENT '当前入住人数',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1可用, 0停用',
  `room_type` varchar(20) DEFAULT '4人间' COMMENT '房间规格(如4人间/2人间)',
  `window_beds_count` int DEFAULT '2' COMMENT '靠窗床位数量',
  `corridor_beds_count` int DEFAULT '2' COMMENT '靠走廊床位数量',
  `special_tag` varchar(50) DEFAULT NULL COMMENT '特殊标签(无障碍/伤病员)',
  `is_active` tinyint DEFAULT '1' COMMENT '是否启用(1是/0否)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_building_room` (`building_id`,`room_number`),
  CONSTRAINT `rooms_ibfk_1` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房间表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `student_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_answer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID(关联students.id)',
  `q_id` int NOT NULL COMMENT '题目ID',
  `option_id` int NOT NULL COMMENT '选择的选项ID',
  `submit_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_question` (`student_id`,`q_id`),
  KEY `q_id` (`q_id`),
  KEY `option_id` (`option_id`),
  CONSTRAINT `student_answer_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `student_answer_ibfk_2` FOREIGN KEY (`q_id`) REFERENCES `questionnaire` (`id`) ON DELETE CASCADE,
  CONSTRAINT `student_answer_ibfk_3` FOREIGN KEY (`option_id`) REFERENCES `question_option` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='新生答案表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_no` varchar(20) NOT NULL COMMENT '学号',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `department` varchar(100) DEFAULT NULL COMMENT '院系',
  `class_name` varchar(50) DEFAULT NULL COMMENT '班级',
  `college_id` int DEFAULT NULL COMMENT '所属学院ID',
  `major_id` int DEFAULT NULL COMMENT '所属专业ID',
  `dorm_batch_id` int DEFAULT NULL COMMENT '参与选宿批次ID',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `user_id` bigint DEFAULT NULL COMMENT '关联用户ID',
  `room_id` bigint DEFAULT NULL COMMENT '入住房间ID',
  `bed_number` varchar(10) DEFAULT NULL COMMENT '床位号',
  `check_in_date` datetime DEFAULT NULL COMMENT '入住日期',
  `check_out_date` datetime DEFAULT NULL COMMENT '退宿日期',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1在住, 0已退宿',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_no` (`student_no`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `students_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL,
  CONSTRAINT `students_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `role` varchar(20) DEFAULT 'STUDENT' COMMENT '角色: ADMIN, MANAGER, STUDENT',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1启用, 0禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `utility_fees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utility_fees` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `year` int NOT NULL COMMENT '年份',
  `month` int NOT NULL COMMENT '月份',
  `electricity_start` decimal(10,2) DEFAULT NULL COMMENT '电表起始读数',
  `electricity_end` decimal(10,2) DEFAULT NULL COMMENT '电表结束读数',
  `electricity_usage` decimal(10,2) DEFAULT NULL COMMENT '用电量(度)',
  `electricity_fee` decimal(10,2) DEFAULT NULL COMMENT '电费',
  `water_start` decimal(10,2) DEFAULT NULL COMMENT '水表起始读数',
  `water_end` decimal(10,2) DEFAULT NULL COMMENT '水表结束读数',
  `water_usage` decimal(10,2) DEFAULT NULL COMMENT '用水量(吨)',
  `water_fee` decimal(10,2) DEFAULT NULL COMMENT '水费',
  `total_fee` decimal(10,2) DEFAULT NULL COMMENT '总费用',
  `status` tinyint DEFAULT '0' COMMENT '缴费状态: 0未缴, 1已缴',
  `pay_time` datetime DEFAULT NULL COMMENT '缴费时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_year_month` (`room_id`,`year`,`month`),
  CONSTRAINT `utility_fees_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='水电费表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `visitors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `visitors` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL COMMENT '被访房间ID',
  `visitor_name` varchar(50) NOT NULL COMMENT '访客姓名',
  `visitor_phone` varchar(20) NOT NULL COMMENT '访客电话',
  `visitor_id_card` varchar(18) DEFAULT NULL COMMENT '访客身份证号',
  `relation` varchar(50) DEFAULT NULL COMMENT '与被访人关系',
  `purpose` varchar(200) DEFAULT NULL COMMENT '来访目的',
  `visit_time` datetime NOT NULL COMMENT '来访时间',
  `leave_time` datetime DEFAULT NULL COMMENT '离开时间',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1在访, 0已离开',
  `note` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `visitors_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='访客记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

