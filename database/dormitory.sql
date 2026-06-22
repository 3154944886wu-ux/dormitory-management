/*
 Navicat Premium Dump SQL

 Source Server         : Mysql93
 Source Server Type    : MySQL
 Source Server Version : 90300 (9.3.0)
 Source Host           : localhost:3306
 Source Schema         : dormitory

 Target Server Type    : MySQL
 Target Server Version : 90300 (9.3.0)
 File Encoding         : 65001

 Date: 12/06/2026 14:38:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for allocation_result
-- ----------------------------
DROP TABLE IF EXISTS `allocation_result`;
CREATE TABLE `allocation_result`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID(关联students.id)',
  `batch_id` int NOT NULL COMMENT '批次ID',
  `roommate_group_id` bigint NULL DEFAULT NULL COMMENT '室友组ID',
  `room_id` bigint NULL DEFAULT NULL COMMENT '分配的宿舍ID',
  `bed_id` bigint NULL DEFAULT NULL COMMENT '分配的床位ID',
  `match_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '匹配度得分',
  `reallocation_count` int NULL DEFAULT 0 COMMENT '重新匹配次数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'recommended' COMMENT '状态: recommended/confirmed/auto_confirmed/manual_assigned/adjusted',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_batch`(`student_id` ASC, `batch_id` ASC) USING BTREE,
  INDEX `batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `roommate_group_id`(`roommate_group_id` ASC) USING BTREE,
  INDEX `room_id`(`room_id` ASC) USING BTREE,
  INDEX `bed_id`(`bed_id` ASC) USING BTREE,
  CONSTRAINT `allocation_result_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `allocation_result_ibfk_2` FOREIGN KEY (`batch_id`) REFERENCES `dorm_batch` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `allocation_result_ibfk_3` FOREIGN KEY (`roommate_group_id`) REFERENCES `roommate_group` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `allocation_result_ibfk_4` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `allocation_result_ibfk_5` FOREIGN KEY (`bed_id`) REFERENCES `bed` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '分配结果表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of allocation_result
-- ----------------------------
INSERT INTO `allocation_result` VALUES (22, 39, 32, NULL, 4059, 20698, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (23, 40, 32, NULL, 4059, 20699, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (24, 41, 32, NULL, 4059, 20700, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (25, 42, 32, NULL, 4059, 20701, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (26, 43, 32, NULL, 4060, 20702, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (27, 49, 32, NULL, 4061, 20706, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (28, 50, 32, NULL, 4061, 20707, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (29, 51, 32, NULL, 4061, 20708, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (30, 52, 32, NULL, 4061, 20709, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (31, 53, 32, NULL, 4062, 20710, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (32, 34, 32, 18, 3940, 20222, 0.00, 1, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (33, 35, 32, NULL, 3940, 20223, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (34, 36, 32, NULL, 3940, 20224, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (35, 37, 32, NULL, 3940, 20225, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (36, 38, 32, NULL, 3941, 20226, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (37, 44, 32, NULL, 3942, 20230, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (38, 45, 32, NULL, 3942, 20231, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (39, 46, 32, NULL, 3942, 20232, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (40, 47, 32, NULL, 3942, 20233, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (41, 48, 32, NULL, 3943, 20234, 0.00, 0, 'auto_confirmed', '2026-06-07 00:52:14', '2026-06-07 01:05:51');
INSERT INTO `allocation_result` VALUES (42, 9, 35, NULL, 4064, 20718, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (43, 10, 35, NULL, 4064, 20719, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (44, 11, 35, NULL, 4064, 20720, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (45, 12, 35, NULL, 4064, 20721, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (46, 13, 35, NULL, 4065, 20722, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (47, 19, 35, NULL, 4066, 20726, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (48, 20, 35, NULL, 4066, 20727, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (49, 21, 35, NULL, 4066, 20728, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (50, 22, 35, NULL, 4066, 20729, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (51, 23, 35, NULL, 4067, 20730, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (52, 29, 35, NULL, 4068, 20734, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (53, 30, 35, NULL, 4068, 20735, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (54, 31, 35, NULL, 4068, 20736, 0.00, 0, 'confirmed', '2026-06-07 00:55:56', '2026-06-07 00:56:59');
INSERT INTO `allocation_result` VALUES (55, 32, 35, NULL, 4068, 20737, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (56, 33, 35, NULL, 4069, 20738, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (57, 4, 35, NULL, 3822, 19750, 0.00, 0, 'confirmed', '2026-06-07 00:55:56', '2026-06-07 00:56:38');
INSERT INTO `allocation_result` VALUES (58, 5, 35, NULL, 3822, 19751, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (59, 6, 35, NULL, 3822, 19752, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (60, 7, 35, NULL, 3822, 19753, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (61, 8, 35, NULL, 3823, 19754, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (62, 14, 35, NULL, 3824, 19758, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (63, 15, 35, NULL, 3824, 19759, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (64, 16, 35, NULL, 3824, 19760, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (65, 17, 35, NULL, 3824, 19761, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (66, 18, 35, NULL, 1, 4, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (67, 24, 35, NULL, 3825, 19762, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (68, 25, 35, NULL, 3825, 19763, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (69, 26, 35, NULL, 3825, 19764, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (70, 27, 35, NULL, 3825, 19765, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');
INSERT INTO `allocation_result` VALUES (71, 28, 35, NULL, 3826, 19766, 0.00, 0, 'auto_confirmed', '2026-06-07 00:55:56', '2026-06-07 00:57:14');

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `type` tinyint NULL DEFAULT 0 COMMENT '类型: 0普通公告, 1重要通知, 2紧急通知',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0草稿, 1已发布, 2已下线',
  `publisher_id` bigint NULL DEFAULT NULL COMMENT '发布人ID',
  `publisher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发布人姓名',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `is_top` tinyint NULL DEFAULT 0 COMMENT '是否置顶: 0否, 1是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 125 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcements
-- ----------------------------
INSERT INTO `announcements` VALUES (1, '欢迎使用宿舍管理系统', '本系统用于管理学生宿舍信息，包括楼栋、房间、学生入住、访客登记、报修等功能。', 1, 2, NULL, 'admin', '2026-05-11 17:49:45', 0, 0, '2026-05-11 17:49:45', '2026-05-27 21:20:36');
INSERT INTO `announcements` VALUES (2, '宿舍安全须知', '请注意用电安全，禁止使用大功率电器。访客请在规定时间内登记来访。', 2, 2, NULL, 'admin', '2026-05-11 17:49:45', 0, 1, '2026-05-11 17:49:45', '2026-05-29 14:18:18');
INSERT INTO `announcements` VALUES (121, '欢迎使用宿舍管理系统', '本系统用于管理学生宿舍信息，包括楼栋、房间、学生入住、访客登记、报修等功能。', 1, 1, NULL, 'admin', '2026-06-06 22:38:28', 0, 0, '2026-06-06 22:38:28', '2026-06-06 22:38:28');
INSERT INTO `announcements` VALUES (122, '宿舍安全须知', '请注意用电安全，禁止使用大功率电器。访客请在规定时间内登记来访。', 2, 1, NULL, 'admin', '2026-06-06 22:38:28', 0, 1, '2026-06-06 22:38:28', '2026-06-06 22:38:28');
INSERT INTO `announcements` VALUES (123, '欢迎使用宿舍管理系统', '本系统用于管理学生宿舍信息，包括楼栋、房间、学生入住、访客登记、报修等功能。', 1, 1, NULL, 'admin', '2026-06-06 22:48:42', 0, 0, '2026-06-06 22:48:42', '2026-06-06 22:48:42');
INSERT INTO `announcements` VALUES (124, '宿舍安全须知', '请注意用电安全，禁止使用大功率电器。访客请在规定时间内登记来访。', 2, 1, NULL, 'admin', '2026-06-06 22:48:42', 0, 1, '2026-06-06 22:48:42', '2026-06-06 22:48:42');

-- ----------------------------
-- Table structure for batch_room
-- ----------------------------
DROP TABLE IF EXISTS `batch_room`;
CREATE TABLE `batch_room`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `batch_id` int NOT NULL COMMENT '批次ID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_batch_room`(`batch_id` ASC, `room_id` ASC) USING BTREE,
  INDEX `room_id`(`room_id` ASC) USING BTREE,
  CONSTRAINT `batch_room_ibfk_1` FOREIGN KEY (`batch_id`) REFERENCES `dorm_batch` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `batch_room_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '批次房源池关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of batch_room
-- ----------------------------
INSERT INTO `batch_room` VALUES (6, 32, 3940, '2026-06-06 21:57:06');
INSERT INTO `batch_room` VALUES (7, 32, 3941, '2026-06-06 21:57:06');
INSERT INTO `batch_room` VALUES (8, 32, 3942, '2026-06-06 21:57:29');
INSERT INTO `batch_room` VALUES (9, 32, 4059, '2026-06-06 21:57:29');
INSERT INTO `batch_room` VALUES (10, 32, 4060, '2026-06-06 21:57:29');
INSERT INTO `batch_room` VALUES (11, 32, 4061, '2026-06-06 21:57:29');
INSERT INTO `batch_room` VALUES (12, 32, 3943, '2026-06-07 00:37:13');
INSERT INTO `batch_room` VALUES (13, 32, 3944, '2026-06-07 00:37:13');
INSERT INTO `batch_room` VALUES (14, 32, 4062, '2026-06-07 00:37:13');
INSERT INTO `batch_room` VALUES (15, 32, 4063, '2026-06-07 00:37:13');
INSERT INTO `batch_room` VALUES (16, 35, 3822, '2026-06-07 00:55:38');
INSERT INTO `batch_room` VALUES (17, 35, 3823, '2026-06-07 00:55:38');
INSERT INTO `batch_room` VALUES (18, 35, 3824, '2026-06-07 00:55:38');
INSERT INTO `batch_room` VALUES (19, 35, 1, '2026-06-07 00:55:38');
INSERT INTO `batch_room` VALUES (20, 35, 3825, '2026-06-07 00:55:38');
INSERT INTO `batch_room` VALUES (21, 35, 3826, '2026-06-07 00:55:38');
INSERT INTO `batch_room` VALUES (22, 35, 4064, '2026-06-07 00:55:39');
INSERT INTO `batch_room` VALUES (23, 35, 4065, '2026-06-07 00:55:39');
INSERT INTO `batch_room` VALUES (24, 35, 4066, '2026-06-07 00:55:39');
INSERT INTO `batch_room` VALUES (25, 35, 4067, '2026-06-07 00:55:39');
INSERT INTO `batch_room` VALUES (26, 35, 4068, '2026-06-07 00:55:39');
INSERT INTO `batch_room` VALUES (27, 35, 4069, '2026-06-07 00:55:55');

-- ----------------------------
-- Table structure for bed
-- ----------------------------
DROP TABLE IF EXISTS `bed`;
CREATE TABLE `bed`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bed_number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '床位编号(A/B/C/D)',
  `room_id` bigint NOT NULL COMMENT '所属房间ID',
  `bed_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'corridor' COMMENT '床型: window(靠窗)/corridor(靠走廊)',
  `is_occupied` tinyint NULL DEFAULT 0 COMMENT '是否已被占用(0空/1占)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_room_bed`(`room_id` ASC, `bed_number` ASC) USING BTREE,
  CONSTRAINT `bed_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21178 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '床位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bed
-- ----------------------------
INSERT INTO `bed` VALUES (1, 'D', 1, 'corridor', 0, '2026-05-19 16:59:12', '2026-05-19 16:59:12');
INSERT INTO `bed` VALUES (2, 'C', 1, 'corridor', 0, '2026-05-19 16:59:12', '2026-05-19 16:59:12');
INSERT INTO `bed` VALUES (3, 'B', 1, 'window', 0, '2026-05-19 16:59:12', '2026-05-19 16:59:12');
INSERT INTO `bed` VALUES (4, 'A', 1, 'window', 1, '2026-05-19 16:59:12', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (5, 'D', 2, 'corridor', 0, '2026-05-19 16:59:12', '2026-05-19 16:59:12');
INSERT INTO `bed` VALUES (6, 'C', 2, 'corridor', 0, '2026-05-19 16:59:12', '2026-05-19 16:59:12');
INSERT INTO `bed` VALUES (7, 'B', 2, 'window', 0, '2026-05-19 16:59:12', '2026-05-19 16:59:12');
INSERT INTO `bed` VALUES (8, 'A', 2, 'window', 0, '2026-05-19 16:59:12', '2026-05-19 16:59:12');
INSERT INTO `bed` VALUES (19746, 'A', 3821, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19747, 'B', 3821, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19748, 'C', 3821, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19749, 'D', 3821, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19750, 'A', 3822, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:56:38');
INSERT INTO `bed` VALUES (19751, 'B', 3822, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19752, 'C', 3822, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19753, 'D', 3822, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19754, 'A', 3823, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19755, 'B', 3823, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19756, 'C', 3823, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19757, 'D', 3823, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19758, 'A', 3824, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19759, 'B', 3824, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19760, 'C', 3824, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19761, 'D', 3824, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19762, 'A', 3825, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19763, 'B', 3825, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19764, 'C', 3825, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19765, 'D', 3825, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19766, 'A', 3826, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (19767, 'B', 3826, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19768, 'C', 3826, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19769, 'D', 3826, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19770, 'A', 3827, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19771, 'B', 3827, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19772, 'C', 3827, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19773, 'D', 3827, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19774, 'A', 3828, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19775, 'B', 3828, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19776, 'C', 3828, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19777, 'D', 3828, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19778, 'A', 3829, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19779, 'B', 3829, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19780, 'C', 3829, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19781, 'D', 3829, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19782, 'A', 3830, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19783, 'B', 3830, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19784, 'C', 3830, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19785, 'D', 3830, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19786, 'A', 3831, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19787, 'B', 3831, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19788, 'C', 3831, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19789, 'D', 3831, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19790, 'A', 3832, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19791, 'B', 3832, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19792, 'C', 3832, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19793, 'D', 3832, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19794, 'A', 3833, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19795, 'B', 3833, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19796, 'C', 3833, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19797, 'D', 3833, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19798, 'A', 3834, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19799, 'B', 3834, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19800, 'C', 3834, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19801, 'D', 3834, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19802, 'A', 3835, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19803, 'B', 3835, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19804, 'C', 3835, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19805, 'D', 3835, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19806, 'A', 3836, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19807, 'B', 3836, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19808, 'C', 3836, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19809, 'D', 3836, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19810, 'A', 3837, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19811, 'B', 3837, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19812, 'C', 3837, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19813, 'D', 3837, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19814, 'A', 3838, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19815, 'B', 3838, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19816, 'C', 3838, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19817, 'D', 3838, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19818, 'A', 3839, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19819, 'B', 3839, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19820, 'C', 3839, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19821, 'D', 3839, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19822, 'A', 3840, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19823, 'B', 3840, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19824, 'C', 3840, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19825, 'D', 3840, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19826, 'A', 3841, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19827, 'B', 3841, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19828, 'C', 3841, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19829, 'D', 3841, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19830, 'A', 3842, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19831, 'B', 3842, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19832, 'C', 3842, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19833, 'D', 3842, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19834, 'A', 3843, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19835, 'B', 3843, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19836, 'C', 3843, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19837, 'D', 3843, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19838, 'A', 3844, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19839, 'B', 3844, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19840, 'C', 3844, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19841, 'D', 3844, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19842, 'A', 3845, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19843, 'B', 3845, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19844, 'C', 3845, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19845, 'D', 3845, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19846, 'A', 3846, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19847, 'B', 3846, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19848, 'C', 3846, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19849, 'D', 3846, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19850, 'A', 3847, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19851, 'B', 3847, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19852, 'C', 3847, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19853, 'D', 3847, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19854, 'A', 3848, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19855, 'B', 3848, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19856, 'C', 3848, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19857, 'D', 3848, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19858, 'A', 3849, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19859, 'B', 3849, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19860, 'C', 3849, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19861, 'D', 3849, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19862, 'A', 3850, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19863, 'B', 3850, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19864, 'C', 3850, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19865, 'D', 3850, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19866, 'A', 3851, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19867, 'B', 3851, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19868, 'C', 3851, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19869, 'D', 3851, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19870, 'A', 3852, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19871, 'B', 3852, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19872, 'C', 3852, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19873, 'D', 3852, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19874, 'A', 3853, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19875, 'B', 3853, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19876, 'C', 3853, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19877, 'D', 3853, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19878, 'A', 3854, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19879, 'B', 3854, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19880, 'C', 3854, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19881, 'D', 3854, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19882, 'A', 3855, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19883, 'B', 3855, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19884, 'C', 3855, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19885, 'D', 3855, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19886, 'A', 3856, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19887, 'B', 3856, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19888, 'C', 3856, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19889, 'D', 3856, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19890, 'A', 3857, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19891, 'B', 3857, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19892, 'C', 3857, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19893, 'D', 3857, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19894, 'A', 3858, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19895, 'B', 3858, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19896, 'C', 3858, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19897, 'D', 3858, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19898, 'A', 3859, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19899, 'B', 3859, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19900, 'C', 3859, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19901, 'D', 3859, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19902, 'A', 3860, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19903, 'B', 3860, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19904, 'C', 3860, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19905, 'D', 3860, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19906, 'A', 3861, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19907, 'B', 3861, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19908, 'C', 3861, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19909, 'D', 3861, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19910, 'A', 3862, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19911, 'B', 3862, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19912, 'C', 3862, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19913, 'D', 3862, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19914, 'A', 3863, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19915, 'B', 3863, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19916, 'C', 3863, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19917, 'D', 3863, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19918, 'A', 3864, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19919, 'B', 3864, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19920, 'C', 3864, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19921, 'D', 3864, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19922, 'A', 3865, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19923, 'B', 3865, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19924, 'C', 3865, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19925, 'D', 3865, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19926, 'A', 3866, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19927, 'B', 3866, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19928, 'C', 3866, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19929, 'D', 3866, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19930, 'A', 3867, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19931, 'B', 3867, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19932, 'C', 3867, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19933, 'D', 3867, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19934, 'A', 3868, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19935, 'B', 3868, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19936, 'C', 3868, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19937, 'D', 3868, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19938, 'A', 3869, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19939, 'B', 3869, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19940, 'C', 3869, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19941, 'D', 3869, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19942, 'A', 3870, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19943, 'B', 3870, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19944, 'C', 3870, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19945, 'D', 3870, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19946, 'A', 3871, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19947, 'B', 3871, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19948, 'C', 3871, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19949, 'D', 3871, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19950, 'A', 3872, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19951, 'B', 3872, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19952, 'C', 3872, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19953, 'D', 3872, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19954, 'A', 3873, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19955, 'B', 3873, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19956, 'C', 3873, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19957, 'D', 3873, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19958, 'A', 3874, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19959, 'B', 3874, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19960, 'C', 3874, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19961, 'D', 3874, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19962, 'A', 3875, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19963, 'B', 3875, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19964, 'C', 3875, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19965, 'D', 3875, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19966, 'A', 3876, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19967, 'B', 3876, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19968, 'C', 3876, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19969, 'D', 3876, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19970, 'A', 3877, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19971, 'B', 3877, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19972, 'C', 3877, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19973, 'D', 3877, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19974, 'A', 3878, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19975, 'B', 3878, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19976, 'C', 3878, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19977, 'D', 3878, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19978, 'A', 3879, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19979, 'B', 3879, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19980, 'C', 3879, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19981, 'D', 3879, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19982, 'A', 3880, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19983, 'B', 3880, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19984, 'C', 3880, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19985, 'D', 3880, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19986, 'A', 3881, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19987, 'B', 3881, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19988, 'C', 3881, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19989, 'D', 3881, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19990, 'A', 3882, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19991, 'B', 3882, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19992, 'C', 3882, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19993, 'D', 3882, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19994, 'A', 3883, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19995, 'B', 3883, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19996, 'C', 3883, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19997, 'D', 3883, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19998, 'A', 3884, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (19999, 'B', 3884, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20000, 'C', 3884, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20001, 'D', 3884, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20002, 'A', 3885, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20003, 'B', 3885, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20004, 'C', 3885, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20005, 'D', 3885, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20006, 'A', 3886, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20007, 'B', 3886, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20008, 'C', 3886, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20009, 'D', 3886, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20010, 'A', 3887, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20011, 'B', 3887, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20012, 'C', 3887, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20013, 'D', 3887, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20014, 'A', 3888, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20015, 'B', 3888, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20016, 'C', 3888, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20017, 'D', 3888, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20018, 'A', 3889, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20019, 'B', 3889, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20020, 'C', 3889, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20021, 'D', 3889, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20022, 'A', 3890, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20023, 'B', 3890, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20024, 'C', 3890, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20025, 'D', 3890, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20026, 'A', 3891, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20027, 'B', 3891, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20028, 'C', 3891, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20029, 'D', 3891, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20030, 'A', 3892, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20031, 'B', 3892, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20032, 'C', 3892, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20033, 'D', 3892, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20034, 'A', 3893, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20035, 'B', 3893, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20036, 'C', 3893, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20037, 'D', 3893, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20038, 'A', 3894, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20039, 'B', 3894, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20040, 'C', 3894, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20041, 'D', 3894, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20042, 'A', 3895, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20043, 'B', 3895, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20044, 'C', 3895, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20045, 'D', 3895, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20046, 'A', 3896, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20047, 'B', 3896, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20048, 'C', 3896, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20049, 'D', 3896, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20050, 'A', 3897, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20051, 'B', 3897, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20052, 'C', 3897, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20053, 'D', 3897, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20054, 'A', 3898, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20055, 'B', 3898, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20056, 'C', 3898, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20057, 'D', 3898, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20058, 'A', 3899, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20059, 'B', 3899, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20060, 'C', 3899, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20061, 'D', 3899, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20062, 'A', 3900, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20063, 'B', 3900, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20064, 'C', 3900, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20065, 'D', 3900, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20066, 'A', 3901, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20067, 'B', 3901, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20068, 'C', 3901, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20069, 'D', 3901, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20070, 'A', 3902, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20071, 'B', 3902, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20072, 'C', 3902, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20073, 'D', 3902, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20074, 'A', 3903, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20075, 'B', 3903, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20076, 'C', 3903, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20077, 'D', 3903, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20078, 'A', 3904, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20079, 'B', 3904, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20080, 'C', 3904, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20081, 'D', 3904, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20082, 'A', 3905, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20083, 'B', 3905, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20084, 'C', 3905, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20085, 'D', 3905, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20086, 'A', 3906, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20087, 'B', 3906, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20088, 'C', 3906, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20089, 'D', 3906, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20090, 'A', 3907, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20091, 'B', 3907, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20092, 'C', 3907, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20093, 'D', 3907, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20094, 'A', 3908, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20095, 'B', 3908, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20096, 'C', 3908, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20097, 'D', 3908, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20098, 'A', 3909, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20099, 'B', 3909, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20100, 'C', 3909, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20101, 'D', 3909, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20102, 'A', 3910, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20103, 'B', 3910, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20104, 'C', 3910, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20105, 'D', 3910, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20106, 'A', 3911, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20107, 'B', 3911, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20108, 'C', 3911, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20109, 'D', 3911, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20110, 'A', 3912, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20111, 'B', 3912, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20112, 'C', 3912, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20113, 'D', 3912, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20114, 'A', 3913, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20115, 'B', 3913, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20116, 'C', 3913, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20117, 'D', 3913, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20118, 'A', 3914, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20119, 'B', 3914, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20120, 'C', 3914, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20121, 'D', 3914, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20122, 'A', 3915, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20123, 'B', 3915, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20124, 'C', 3915, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20125, 'D', 3915, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20126, 'A', 3916, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20127, 'B', 3916, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20128, 'C', 3916, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20129, 'D', 3916, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20130, 'A', 3917, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20131, 'B', 3917, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20132, 'C', 3917, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20133, 'D', 3917, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20134, 'A', 3918, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20135, 'B', 3918, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20136, 'C', 3918, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20137, 'D', 3918, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20138, 'A', 3919, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20139, 'B', 3919, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20140, 'C', 3919, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20141, 'D', 3919, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20142, 'A', 3920, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20143, 'B', 3920, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20144, 'C', 3920, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20145, 'D', 3920, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20146, 'A', 3921, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20147, 'B', 3921, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20148, 'C', 3921, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20149, 'D', 3921, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20150, 'A', 3922, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20151, 'B', 3922, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20152, 'C', 3922, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20153, 'D', 3922, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20154, 'A', 3923, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20155, 'B', 3923, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20156, 'C', 3923, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20157, 'D', 3923, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20158, 'A', 3924, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20159, 'B', 3924, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20160, 'C', 3924, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20161, 'D', 3924, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20162, 'A', 3925, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20163, 'B', 3925, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20164, 'C', 3925, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20165, 'D', 3925, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20166, 'A', 3926, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20167, 'B', 3926, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20168, 'C', 3926, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20169, 'D', 3926, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20170, 'A', 3927, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20171, 'B', 3927, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20172, 'C', 3927, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20173, 'D', 3927, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20174, 'A', 3928, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20175, 'B', 3928, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20176, 'C', 3928, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20177, 'D', 3928, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20178, 'A', 3929, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20179, 'B', 3929, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20180, 'C', 3929, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20181, 'D', 3929, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20182, 'A', 3930, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20183, 'B', 3930, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20184, 'C', 3930, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20185, 'D', 3930, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20186, 'A', 3931, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20187, 'B', 3931, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20188, 'C', 3931, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20189, 'D', 3931, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20190, 'A', 3932, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20191, 'B', 3932, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20192, 'C', 3932, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20193, 'D', 3932, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20194, 'A', 3933, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20195, 'B', 3933, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20196, 'C', 3933, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20197, 'D', 3933, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20198, 'A', 3934, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20199, 'B', 3934, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20200, 'C', 3934, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20201, 'D', 3934, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20202, 'A', 3935, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20203, 'B', 3935, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20204, 'C', 3935, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20205, 'D', 3935, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20206, 'A', 3936, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20207, 'B', 3936, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20208, 'C', 3936, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20209, 'D', 3936, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20210, 'A', 3937, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20211, 'B', 3937, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20212, 'C', 3937, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20213, 'D', 3937, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20214, 'A', 3938, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20215, 'B', 3938, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20216, 'C', 3938, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20217, 'D', 3938, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20218, 'A', 3939, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20219, 'B', 3939, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20220, 'C', 3939, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20221, 'D', 3939, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20222, 'A', 3940, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20223, 'B', 3940, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20224, 'C', 3940, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20225, 'D', 3940, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20226, 'A', 3941, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20227, 'B', 3941, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20228, 'C', 3941, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20229, 'D', 3941, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20230, 'A', 3942, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20231, 'B', 3942, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20232, 'C', 3942, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20233, 'D', 3942, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20234, 'A', 3943, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20235, 'B', 3943, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20236, 'C', 3943, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20237, 'D', 3943, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20238, 'A', 3944, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20239, 'B', 3944, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20240, 'C', 3944, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20241, 'D', 3944, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20242, 'A', 3945, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20243, 'B', 3945, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20244, 'C', 3945, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20245, 'D', 3945, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20246, 'A', 3946, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20247, 'B', 3946, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20248, 'C', 3946, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20249, 'D', 3946, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20250, 'A', 3947, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20251, 'B', 3947, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20252, 'C', 3947, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20253, 'D', 3947, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20254, 'A', 3948, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20255, 'B', 3948, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20256, 'C', 3948, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20257, 'D', 3948, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20258, 'A', 3949, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20259, 'B', 3949, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20260, 'C', 3949, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20261, 'D', 3949, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20262, 'A', 3950, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20263, 'B', 3950, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20264, 'C', 3950, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20265, 'D', 3950, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20266, 'A', 3951, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20267, 'B', 3951, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20268, 'C', 3951, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20269, 'D', 3951, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20270, 'A', 3952, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20271, 'B', 3952, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20272, 'C', 3952, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20273, 'D', 3952, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20274, 'A', 3953, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20275, 'B', 3953, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20276, 'C', 3953, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20277, 'D', 3953, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20278, 'A', 3954, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20279, 'B', 3954, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20280, 'C', 3954, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20281, 'D', 3954, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20282, 'A', 3955, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20283, 'B', 3955, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20284, 'C', 3955, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20285, 'D', 3955, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20286, 'A', 3956, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20287, 'B', 3956, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20288, 'C', 3956, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20289, 'D', 3956, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20290, 'A', 3957, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20291, 'B', 3957, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20292, 'C', 3957, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20293, 'D', 3957, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20294, 'A', 3958, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20295, 'B', 3958, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20296, 'C', 3958, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20297, 'D', 3958, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20298, 'A', 3959, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20299, 'B', 3959, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20300, 'C', 3959, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20301, 'D', 3959, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20302, 'A', 3960, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20303, 'B', 3960, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20304, 'C', 3960, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20305, 'D', 3960, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20306, 'A', 3961, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20307, 'B', 3961, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20308, 'C', 3961, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20309, 'D', 3961, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20310, 'A', 3962, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20311, 'B', 3962, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20312, 'C', 3962, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20313, 'D', 3962, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20314, 'A', 3963, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20315, 'B', 3963, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20316, 'C', 3963, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20317, 'D', 3963, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20318, 'A', 3964, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20319, 'B', 3964, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20320, 'C', 3964, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20321, 'D', 3964, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20322, 'A', 3965, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20323, 'B', 3965, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20324, 'C', 3965, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20325, 'D', 3965, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20326, 'A', 3966, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20327, 'B', 3966, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20328, 'C', 3966, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20329, 'D', 3966, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20330, 'A', 3967, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20331, 'B', 3967, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20332, 'C', 3967, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20333, 'D', 3967, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20334, 'A', 3968, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20335, 'B', 3968, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20336, 'C', 3968, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20337, 'D', 3968, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20338, 'A', 3969, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20339, 'B', 3969, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20340, 'C', 3969, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20341, 'D', 3969, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20342, 'A', 3970, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20343, 'B', 3970, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20344, 'C', 3970, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20345, 'D', 3970, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20346, 'A', 3971, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20347, 'B', 3971, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20348, 'C', 3971, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20349, 'D', 3971, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20350, 'A', 3972, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20351, 'B', 3972, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20352, 'C', 3972, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20353, 'D', 3972, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20354, 'A', 3973, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20355, 'B', 3973, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20356, 'C', 3973, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20357, 'D', 3973, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20358, 'A', 3974, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20359, 'B', 3974, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20360, 'C', 3974, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20361, 'D', 3974, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20362, 'A', 3975, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20363, 'B', 3975, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20364, 'C', 3975, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20365, 'D', 3975, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20366, 'A', 3976, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20367, 'B', 3976, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20368, 'C', 3976, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20369, 'D', 3976, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20370, 'A', 3977, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20371, 'B', 3977, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20372, 'C', 3977, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20373, 'D', 3977, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20374, 'A', 3978, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20375, 'B', 3978, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20376, 'C', 3978, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20377, 'D', 3978, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20378, 'A', 3979, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20379, 'B', 3979, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20380, 'C', 3979, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20381, 'D', 3979, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20382, 'A', 3980, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20383, 'B', 3980, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20384, 'C', 3980, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20385, 'D', 3980, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20386, 'A', 3981, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20387, 'B', 3981, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20388, 'C', 3981, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20389, 'D', 3981, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20390, 'A', 3982, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20391, 'B', 3982, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20392, 'C', 3982, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20393, 'D', 3982, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20394, 'A', 3983, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20395, 'B', 3983, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20396, 'C', 3983, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20397, 'D', 3983, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20398, 'A', 3984, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20399, 'B', 3984, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20400, 'C', 3984, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20401, 'D', 3984, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20402, 'A', 3985, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20403, 'B', 3985, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20404, 'C', 3985, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20405, 'D', 3985, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20406, 'A', 3986, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20407, 'B', 3986, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20408, 'C', 3986, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20409, 'D', 3986, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20410, 'A', 3987, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20411, 'B', 3987, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20412, 'C', 3987, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20413, 'D', 3987, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20414, 'A', 3988, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20415, 'B', 3988, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20416, 'C', 3988, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20417, 'D', 3988, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20418, 'A', 3989, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20419, 'B', 3989, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20420, 'C', 3989, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20421, 'D', 3989, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20422, 'A', 3990, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20423, 'B', 3990, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20424, 'C', 3990, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20425, 'D', 3990, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20426, 'A', 3991, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20427, 'B', 3991, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20428, 'C', 3991, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20429, 'D', 3991, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20430, 'A', 3992, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20431, 'B', 3992, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20432, 'C', 3992, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20433, 'D', 3992, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20434, 'A', 3993, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20435, 'B', 3993, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20436, 'C', 3993, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20437, 'D', 3993, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20438, 'A', 3994, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20439, 'B', 3994, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20440, 'C', 3994, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20441, 'D', 3994, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20442, 'A', 3995, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20443, 'B', 3995, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20444, 'C', 3995, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20445, 'D', 3995, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20446, 'A', 3996, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20447, 'B', 3996, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20448, 'C', 3996, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20449, 'D', 3996, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20450, 'A', 3997, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20451, 'B', 3997, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20452, 'C', 3997, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20453, 'D', 3997, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20454, 'A', 3998, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20455, 'B', 3998, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20456, 'C', 3998, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20457, 'D', 3998, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20458, 'A', 3999, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20459, 'B', 3999, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20460, 'C', 3999, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20461, 'D', 3999, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20462, 'A', 4000, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20463, 'B', 4000, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20464, 'C', 4000, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20465, 'D', 4000, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20466, 'A', 4001, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20467, 'B', 4001, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20468, 'C', 4001, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20469, 'D', 4001, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20470, 'A', 4002, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20471, 'B', 4002, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20472, 'C', 4002, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20473, 'D', 4002, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20474, 'A', 4003, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20475, 'B', 4003, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20476, 'C', 4003, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20477, 'D', 4003, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20478, 'A', 4004, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20479, 'B', 4004, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20480, 'C', 4004, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20481, 'D', 4004, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20482, 'A', 4005, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20483, 'B', 4005, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20484, 'C', 4005, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20485, 'D', 4005, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20486, 'A', 4006, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20487, 'B', 4006, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20488, 'C', 4006, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20489, 'D', 4006, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20490, 'A', 4007, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20491, 'B', 4007, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20492, 'C', 4007, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20493, 'D', 4007, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20494, 'A', 4008, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20495, 'B', 4008, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20496, 'C', 4008, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20497, 'D', 4008, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20498, 'A', 4009, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20499, 'B', 4009, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20500, 'C', 4009, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20501, 'D', 4009, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20502, 'A', 4010, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20503, 'B', 4010, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20504, 'C', 4010, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20505, 'D', 4010, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20506, 'A', 4011, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20507, 'B', 4011, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20508, 'C', 4011, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20509, 'D', 4011, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20510, 'A', 4012, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20511, 'B', 4012, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20512, 'C', 4012, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20513, 'D', 4012, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20514, 'A', 4013, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20515, 'B', 4013, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20516, 'C', 4013, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20517, 'D', 4013, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20518, 'A', 4014, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20519, 'B', 4014, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20520, 'C', 4014, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20521, 'D', 4014, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20522, 'A', 4015, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20523, 'B', 4015, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20524, 'C', 4015, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20525, 'D', 4015, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20526, 'A', 4016, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20527, 'B', 4016, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20528, 'C', 4016, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20529, 'D', 4016, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20530, 'A', 4017, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20531, 'B', 4017, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20532, 'C', 4017, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20533, 'D', 4017, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20534, 'A', 4018, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20535, 'B', 4018, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20536, 'C', 4018, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20537, 'D', 4018, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20538, 'A', 4019, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20539, 'B', 4019, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20540, 'C', 4019, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20541, 'D', 4019, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20542, 'A', 4020, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20543, 'B', 4020, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20544, 'C', 4020, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20545, 'D', 4020, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20546, 'A', 4021, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20547, 'B', 4021, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20548, 'C', 4021, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20549, 'D', 4021, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20550, 'A', 4022, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20551, 'B', 4022, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20552, 'C', 4022, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20553, 'D', 4022, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20554, 'A', 4023, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20555, 'B', 4023, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20556, 'C', 4023, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20557, 'D', 4023, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20558, 'A', 4024, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20559, 'B', 4024, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20560, 'C', 4024, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20561, 'D', 4024, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20562, 'A', 4025, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20563, 'B', 4025, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20564, 'C', 4025, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20565, 'D', 4025, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20566, 'A', 4026, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20567, 'B', 4026, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20568, 'C', 4026, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20569, 'D', 4026, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20570, 'A', 4027, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20571, 'B', 4027, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20572, 'C', 4027, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20573, 'D', 4027, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20574, 'A', 4028, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20575, 'B', 4028, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20576, 'C', 4028, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20577, 'D', 4028, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20578, 'A', 4029, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20579, 'B', 4029, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20580, 'C', 4029, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20581, 'D', 4029, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20582, 'A', 4030, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20583, 'B', 4030, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20584, 'C', 4030, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20585, 'D', 4030, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20586, 'A', 4031, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20587, 'B', 4031, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20588, 'C', 4031, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20589, 'D', 4031, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20590, 'A', 4032, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20591, 'B', 4032, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20592, 'C', 4032, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20593, 'D', 4032, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20594, 'A', 4033, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20595, 'B', 4033, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20596, 'C', 4033, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20597, 'D', 4033, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20598, 'A', 4034, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20599, 'B', 4034, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20600, 'C', 4034, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20601, 'D', 4034, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20602, 'A', 4035, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20603, 'B', 4035, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20604, 'C', 4035, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20605, 'D', 4035, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20606, 'A', 4036, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20607, 'B', 4036, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20608, 'C', 4036, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20609, 'D', 4036, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20610, 'A', 4037, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20611, 'B', 4037, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20612, 'C', 4037, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20613, 'D', 4037, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20614, 'A', 4038, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20615, 'B', 4038, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20616, 'C', 4038, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20617, 'D', 4038, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20618, 'A', 4039, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20619, 'B', 4039, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20620, 'C', 4039, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20621, 'D', 4039, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20622, 'A', 4040, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20623, 'B', 4040, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20624, 'C', 4040, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20625, 'D', 4040, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20626, 'A', 4041, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20627, 'B', 4041, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20628, 'C', 4041, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20629, 'D', 4041, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20630, 'A', 4042, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20631, 'B', 4042, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20632, 'C', 4042, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20633, 'D', 4042, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20634, 'A', 4043, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20635, 'B', 4043, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20636, 'C', 4043, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20637, 'D', 4043, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20638, 'A', 4044, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20639, 'B', 4044, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20640, 'C', 4044, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20641, 'D', 4044, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20642, 'A', 4045, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20643, 'B', 4045, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20644, 'C', 4045, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20645, 'D', 4045, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20646, 'A', 4046, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20647, 'B', 4046, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20648, 'C', 4046, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20649, 'D', 4046, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20650, 'A', 4047, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20651, 'B', 4047, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20652, 'C', 4047, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20653, 'D', 4047, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20654, 'A', 4048, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20655, 'B', 4048, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20656, 'C', 4048, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20657, 'D', 4048, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20658, 'A', 4049, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20659, 'B', 4049, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20660, 'C', 4049, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20661, 'D', 4049, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20662, 'A', 4050, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20663, 'B', 4050, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20664, 'C', 4050, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20665, 'D', 4050, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20666, 'A', 4051, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20667, 'B', 4051, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20668, 'C', 4051, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20669, 'D', 4051, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20670, 'A', 4052, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20671, 'B', 4052, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20672, 'C', 4052, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20673, 'D', 4052, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20674, 'A', 4053, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20675, 'B', 4053, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20676, 'C', 4053, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20677, 'D', 4053, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20678, 'A', 4054, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20679, 'B', 4054, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20680, 'C', 4054, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20681, 'D', 4054, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20682, 'A', 4055, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20683, 'B', 4055, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20684, 'C', 4055, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20685, 'D', 4055, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20686, 'A', 4056, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20687, 'B', 4056, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20688, 'C', 4056, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20689, 'D', 4056, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20690, 'A', 4057, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20691, 'B', 4057, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20692, 'C', 4057, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20693, 'D', 4057, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20694, 'A', 4058, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20695, 'B', 4058, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20696, 'C', 4058, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20697, 'D', 4058, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20698, 'A', 4059, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20699, 'B', 4059, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20700, 'C', 4059, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20701, 'D', 4059, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20702, 'A', 4060, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20703, 'B', 4060, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20704, 'C', 4060, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20705, 'D', 4060, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20706, 'A', 4061, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20707, 'B', 4061, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20708, 'C', 4061, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20709, 'D', 4061, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20710, 'A', 4062, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `bed` VALUES (20711, 'B', 4062, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20712, 'C', 4062, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20713, 'D', 4062, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20714, 'A', 4063, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20715, 'B', 4063, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20716, 'C', 4063, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20717, 'D', 4063, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20718, 'A', 4064, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20719, 'B', 4064, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20720, 'C', 4064, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20721, 'D', 4064, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20722, 'A', 4065, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20723, 'B', 4065, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20724, 'C', 4065, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20725, 'D', 4065, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20726, 'A', 4066, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20727, 'B', 4066, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20728, 'C', 4066, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20729, 'D', 4066, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20730, 'A', 4067, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20731, 'B', 4067, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20732, 'C', 4067, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20733, 'D', 4067, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20734, 'A', 4068, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20735, 'B', 4068, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20736, 'C', 4068, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 00:56:59');
INSERT INTO `bed` VALUES (20737, 'D', 4068, 'corridor', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20738, 'A', 4069, 'window', 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `bed` VALUES (20739, 'B', 4069, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20740, 'C', 4069, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20741, 'D', 4069, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20742, 'A', 4070, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20743, 'B', 4070, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20744, 'C', 4070, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20745, 'D', 4070, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20746, 'A', 4071, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20747, 'B', 4071, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20748, 'C', 4071, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20749, 'D', 4071, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20750, 'A', 4072, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20751, 'B', 4072, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20752, 'C', 4072, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20753, 'D', 4072, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20754, 'A', 4073, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20755, 'B', 4073, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20756, 'C', 4073, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20757, 'D', 4073, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20758, 'A', 4074, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20759, 'B', 4074, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20760, 'C', 4074, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20761, 'D', 4074, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20762, 'A', 4075, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20763, 'B', 4075, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20764, 'C', 4075, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20765, 'D', 4075, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20766, 'A', 4076, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20767, 'B', 4076, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20768, 'C', 4076, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20769, 'D', 4076, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20770, 'A', 4077, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20771, 'B', 4077, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20772, 'C', 4077, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20773, 'D', 4077, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20774, 'A', 4078, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20775, 'B', 4078, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20776, 'C', 4078, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20777, 'D', 4078, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20778, 'A', 4079, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20779, 'B', 4079, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20780, 'C', 4079, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20781, 'D', 4079, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20782, 'A', 4080, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20783, 'B', 4080, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20784, 'C', 4080, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20785, 'D', 4080, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20786, 'A', 4081, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20787, 'B', 4081, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20788, 'C', 4081, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20789, 'D', 4081, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20790, 'A', 4082, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20791, 'B', 4082, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20792, 'C', 4082, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20793, 'D', 4082, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20794, 'A', 4083, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20795, 'B', 4083, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20796, 'C', 4083, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20797, 'D', 4083, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20798, 'A', 4084, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20799, 'B', 4084, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20800, 'C', 4084, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20801, 'D', 4084, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20802, 'A', 4085, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20803, 'B', 4085, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20804, 'C', 4085, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20805, 'D', 4085, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20806, 'A', 4086, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20807, 'B', 4086, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20808, 'C', 4086, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20809, 'D', 4086, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20810, 'A', 4087, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20811, 'B', 4087, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20812, 'C', 4087, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20813, 'D', 4087, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20814, 'A', 4088, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20815, 'B', 4088, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20816, 'C', 4088, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20817, 'D', 4088, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20818, 'A', 4089, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20819, 'B', 4089, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20820, 'C', 4089, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20821, 'D', 4089, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20822, 'A', 4090, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20823, 'B', 4090, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20824, 'C', 4090, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20825, 'D', 4090, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20826, 'A', 4091, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20827, 'B', 4091, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20828, 'C', 4091, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20829, 'D', 4091, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20830, 'A', 4092, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20831, 'B', 4092, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20832, 'C', 4092, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20833, 'D', 4092, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20834, 'A', 4093, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20835, 'B', 4093, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20836, 'C', 4093, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20837, 'D', 4093, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20838, 'A', 4094, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20839, 'B', 4094, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20840, 'C', 4094, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20841, 'D', 4094, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20842, 'A', 4095, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20843, 'B', 4095, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20844, 'C', 4095, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20845, 'D', 4095, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20846, 'A', 4096, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20847, 'B', 4096, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20848, 'C', 4096, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20849, 'D', 4096, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20850, 'A', 4097, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20851, 'B', 4097, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20852, 'C', 4097, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20853, 'D', 4097, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20854, 'A', 4098, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20855, 'B', 4098, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20856, 'C', 4098, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20857, 'D', 4098, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20858, 'A', 4099, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20859, 'B', 4099, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20860, 'C', 4099, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20861, 'D', 4099, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20862, 'A', 4100, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20863, 'B', 4100, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20864, 'C', 4100, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20865, 'D', 4100, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20866, 'A', 4101, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20867, 'B', 4101, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20868, 'C', 4101, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20869, 'D', 4101, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20870, 'A', 4102, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20871, 'B', 4102, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20872, 'C', 4102, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20873, 'D', 4102, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20874, 'A', 4103, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20875, 'B', 4103, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20876, 'C', 4103, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20877, 'D', 4103, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20878, 'A', 4104, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20879, 'B', 4104, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20880, 'C', 4104, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20881, 'D', 4104, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20882, 'A', 4105, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20883, 'B', 4105, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20884, 'C', 4105, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20885, 'D', 4105, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20886, 'A', 4106, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20887, 'B', 4106, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20888, 'C', 4106, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20889, 'D', 4106, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20890, 'A', 4107, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20891, 'B', 4107, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20892, 'C', 4107, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20893, 'D', 4107, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20894, 'A', 4108, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20895, 'B', 4108, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20896, 'C', 4108, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20897, 'D', 4108, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20898, 'A', 4109, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20899, 'B', 4109, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20900, 'C', 4109, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20901, 'D', 4109, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20902, 'A', 4110, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20903, 'B', 4110, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20904, 'C', 4110, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20905, 'D', 4110, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20906, 'A', 4111, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20907, 'B', 4111, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20908, 'C', 4111, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20909, 'D', 4111, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20910, 'A', 4112, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20911, 'B', 4112, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20912, 'C', 4112, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20913, 'D', 4112, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20914, 'A', 4113, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20915, 'B', 4113, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20916, 'C', 4113, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20917, 'D', 4113, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20918, 'A', 4114, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20919, 'B', 4114, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20920, 'C', 4114, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20921, 'D', 4114, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20922, 'A', 4115, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20923, 'B', 4115, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20924, 'C', 4115, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20925, 'D', 4115, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20926, 'A', 4116, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20927, 'B', 4116, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20928, 'C', 4116, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20929, 'D', 4116, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20930, 'A', 4117, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20931, 'B', 4117, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20932, 'C', 4117, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20933, 'D', 4117, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20934, 'A', 4118, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20935, 'B', 4118, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20936, 'C', 4118, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20937, 'D', 4118, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20938, 'A', 4119, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20939, 'B', 4119, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20940, 'C', 4119, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20941, 'D', 4119, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20942, 'A', 4120, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20943, 'B', 4120, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20944, 'C', 4120, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20945, 'D', 4120, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20946, 'A', 4121, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20947, 'B', 4121, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20948, 'C', 4121, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20949, 'D', 4121, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20950, 'A', 4122, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20951, 'B', 4122, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20952, 'C', 4122, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20953, 'D', 4122, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20954, 'A', 4123, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20955, 'B', 4123, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20956, 'C', 4123, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20957, 'D', 4123, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20958, 'A', 4124, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20959, 'B', 4124, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20960, 'C', 4124, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20961, 'D', 4124, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20962, 'A', 4125, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20963, 'B', 4125, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20964, 'C', 4125, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20965, 'D', 4125, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20966, 'A', 4126, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20967, 'B', 4126, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20968, 'C', 4126, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20969, 'D', 4126, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20970, 'A', 4127, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20971, 'B', 4127, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20972, 'C', 4127, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20973, 'D', 4127, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20974, 'A', 4128, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20975, 'B', 4128, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20976, 'C', 4128, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20977, 'D', 4128, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20978, 'A', 4129, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20979, 'B', 4129, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20980, 'C', 4129, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20981, 'D', 4129, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20982, 'A', 4130, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20983, 'B', 4130, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20984, 'C', 4130, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20985, 'D', 4130, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20986, 'A', 4131, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20987, 'B', 4131, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20988, 'C', 4131, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20989, 'D', 4131, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20990, 'A', 4132, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20991, 'B', 4132, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20992, 'C', 4132, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20993, 'D', 4132, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20994, 'A', 4133, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20995, 'B', 4133, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20996, 'C', 4133, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20997, 'D', 4133, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20998, 'A', 4134, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (20999, 'B', 4134, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21000, 'C', 4134, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21001, 'D', 4134, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21002, 'A', 4135, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21003, 'B', 4135, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21004, 'C', 4135, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21005, 'D', 4135, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21006, 'A', 4136, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21007, 'B', 4136, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21008, 'C', 4136, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21009, 'D', 4136, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21010, 'A', 4137, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21011, 'B', 4137, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21012, 'C', 4137, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21013, 'D', 4137, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21014, 'A', 4138, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21015, 'B', 4138, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21016, 'C', 4138, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21017, 'D', 4138, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21018, 'A', 4139, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21019, 'B', 4139, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21020, 'C', 4139, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21021, 'D', 4139, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21022, 'A', 4140, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21023, 'B', 4140, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21024, 'C', 4140, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21025, 'D', 4140, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21026, 'A', 4141, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21027, 'B', 4141, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21028, 'C', 4141, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21029, 'D', 4141, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21030, 'A', 4142, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21031, 'B', 4142, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21032, 'C', 4142, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21033, 'D', 4142, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21034, 'A', 4143, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21035, 'B', 4143, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21036, 'C', 4143, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21037, 'D', 4143, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21038, 'A', 4144, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21039, 'B', 4144, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21040, 'C', 4144, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21041, 'D', 4144, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21042, 'A', 4145, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21043, 'B', 4145, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21044, 'C', 4145, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21045, 'D', 4145, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21046, 'A', 4146, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21047, 'B', 4146, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21048, 'C', 4146, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21049, 'D', 4146, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21050, 'A', 4147, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21051, 'B', 4147, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21052, 'C', 4147, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21053, 'D', 4147, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21054, 'A', 4148, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21055, 'B', 4148, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21056, 'C', 4148, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21057, 'D', 4148, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21058, 'A', 4149, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21059, 'B', 4149, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21060, 'C', 4149, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21061, 'D', 4149, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21062, 'A', 4150, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21063, 'B', 4150, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21064, 'C', 4150, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21065, 'D', 4150, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21066, 'A', 4151, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21067, 'B', 4151, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21068, 'C', 4151, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21069, 'D', 4151, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21070, 'A', 4152, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21071, 'B', 4152, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21072, 'C', 4152, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21073, 'D', 4152, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21074, 'A', 4153, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21075, 'B', 4153, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21076, 'C', 4153, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21077, 'D', 4153, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21078, 'A', 4154, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21079, 'B', 4154, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21080, 'C', 4154, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21081, 'D', 4154, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21082, 'A', 4155, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21083, 'B', 4155, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21084, 'C', 4155, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21085, 'D', 4155, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21086, 'A', 4156, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21087, 'B', 4156, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21088, 'C', 4156, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21089, 'D', 4156, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21090, 'A', 4157, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21091, 'B', 4157, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21092, 'C', 4157, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21093, 'D', 4157, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21094, 'A', 4158, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21095, 'B', 4158, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21096, 'C', 4158, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21097, 'D', 4158, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21098, 'A', 4159, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21099, 'B', 4159, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21100, 'C', 4159, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21101, 'D', 4159, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21102, 'A', 4160, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21103, 'B', 4160, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21104, 'C', 4160, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21105, 'D', 4160, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21106, 'A', 4161, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21107, 'B', 4161, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21108, 'C', 4161, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21109, 'D', 4161, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21110, 'A', 4162, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21111, 'B', 4162, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21112, 'C', 4162, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21113, 'D', 4162, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21114, 'A', 4163, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21115, 'B', 4163, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21116, 'C', 4163, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21117, 'D', 4163, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21118, 'A', 4164, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21119, 'B', 4164, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21120, 'C', 4164, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21121, 'D', 4164, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21122, 'A', 4165, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21123, 'B', 4165, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21124, 'C', 4165, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21125, 'D', 4165, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21126, 'A', 4166, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21127, 'B', 4166, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21128, 'C', 4166, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21129, 'D', 4166, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21130, 'A', 4167, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21131, 'B', 4167, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21132, 'C', 4167, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21133, 'D', 4167, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21134, 'A', 4168, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21135, 'B', 4168, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21136, 'C', 4168, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21137, 'D', 4168, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21138, 'A', 4169, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21139, 'B', 4169, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21140, 'C', 4169, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21141, 'D', 4169, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21142, 'A', 4170, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21143, 'B', 4170, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21144, 'C', 4170, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21145, 'D', 4170, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21146, 'A', 4171, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21147, 'B', 4171, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21148, 'C', 4171, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21149, 'D', 4171, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21150, 'A', 4172, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21151, 'B', 4172, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21152, 'C', 4172, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21153, 'D', 4172, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21154, 'A', 4173, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21155, 'B', 4173, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21156, 'C', 4173, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21157, 'D', 4173, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21158, 'A', 4174, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21159, 'B', 4174, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21160, 'C', 4174, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21161, 'D', 4174, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21162, 'A', 4175, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21163, 'B', 4175, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21164, 'C', 4175, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21165, 'D', 4175, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21166, 'A', 4176, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21167, 'B', 4176, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21168, 'C', 4176, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21169, 'D', 4176, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21170, 'A', 4177, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21171, 'B', 4177, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21172, 'C', 4177, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21173, 'D', 4177, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21174, 'A', 4178, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21175, 'B', 4178, 'window', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21176, 'C', 4178, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `bed` VALUES (21177, 'D', 4178, 'corridor', 0, '2026-06-05 14:34:32', '2026-06-05 14:34:32');

-- ----------------------------
-- Table structure for buildings
-- ----------------------------
DROP TABLE IF EXISTS `buildings`;
CREATE TABLE `buildings`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '楼栋名称',
  `floors` int NULL DEFAULT 6 COMMENT '楼层数',
  `rooms_per_floor` int NULL DEFAULT 20 COMMENT '每层房间数',
  `gender_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'MIXED' COMMENT '性别类型: MALE, FEMALE, MIXED',
  `gender_limit` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'MIXED' COMMENT '性别限制: MALE/FEMALE/MIXED(通用)',
  `manager` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '宿管姓名',
  `manager_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '宿管电话',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1启用, 0停用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_building_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 126 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '楼栋表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of buildings
-- ----------------------------
INSERT INTO `buildings` VALUES (1, '1号楼', 6, 20, 'MALE', 'MIXED', '乙', '13800000001', NULL, 1, '2026-05-11 17:49:45', '2026-05-27 21:26:19');
INSERT INTO `buildings` VALUES (2, '2号楼', 6, 20, 'MALE', 'MIXED', '甲', '13800000002', NULL, 1, '2026-05-11 17:49:45', '2026-05-27 21:26:07');
INSERT INTO `buildings` VALUES (121, '3号楼', 6, 20, 'FEMALE', 'FEMALE', '丙', '13800000003', NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');

-- ----------------------------
-- Table structure for check_exceptions
-- ----------------------------
DROP TABLE IF EXISTS `check_exceptions`;
CREATE TABLE `check_exceptions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `exception_date` date NOT NULL COMMENT '异常日期',
  `exception_type` tinyint NOT NULL COMMENT '异常类型: 1晚归, 2未归, 3缺卡',
  `check_record_id` bigint NULL DEFAULT NULL COMMENT '关联打卡记录ID',
  `handled` tinyint NULL DEFAULT 0 COMMENT '是否已处理: 0否, 1是',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `handle_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  INDEX `check_record_id`(`check_record_id` ASC) USING BTREE,
  CONSTRAINT `check_exceptions_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `check_exceptions_ibfk_2` FOREIGN KEY (`check_record_id`) REFERENCES `check_in_records` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '归寝异常记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of check_exceptions
-- ----------------------------

-- ----------------------------
-- Table structure for check_in_records
-- ----------------------------
DROP TABLE IF EXISTS `check_in_records`;
CREATE TABLE `check_in_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `room_id` bigint NULL DEFAULT NULL COMMENT '房间ID',
  `check_date` date NOT NULL COMMENT '打卡日期',
  `check_time` datetime NULL DEFAULT NULL COMMENT '打卡时间',
  `check_type` tinyint NULL DEFAULT 0 COMMENT '打卡方式: 0定位, 1人脸, 2手动',
  `latitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '经度',
  `location_accuracy` decimal(10, 2) NULL DEFAULT NULL COMMENT '定位精度(米)',
  `device_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备信息',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0正常, 1晚归, 2未归, 3请假',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_date`(`student_id` ASC, `check_date` ASC) USING BTREE,
  INDEX `room_id`(`room_id` ASC) USING BTREE,
  CONSTRAINT `check_in_records_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `check_in_records_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '归寝打卡记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of check_in_records
-- ----------------------------

-- ----------------------------
-- Table structure for check_rules
-- ----------------------------
DROP TABLE IF EXISTS `check_rules`;
CREATE TABLE `check_rules`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规则名称',
  `building_id` bigint NULL DEFAULT NULL COMMENT '适用楼栋ID，NULL表示全局规则',
  `check_start_time` time NOT NULL COMMENT '归寝开始时间(如22:00)',
  `check_end_time` time NOT NULL COMMENT '归寝结束时间(如23:00)',
  `late_threshold` time NULL DEFAULT NULL COMMENT '晚归判定时间(如23:30)',
  `apply_days` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1,2,3,4,5' COMMENT '适用日期(周几): 1-7,逗号分隔',
  `allow_late_count` int NULL DEFAULT 3 COMMENT '允许晚归次数/月',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认规则',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1启用, 0禁用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `allowed_latitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '允许打卡纬度',
  `allowed_longitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '允许打卡经度',
  `allowed_radius` int NULL DEFAULT 500 COMMENT '允许范围半径(米)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `building_id`(`building_id` ASC) USING BTREE,
  CONSTRAINT `check_rules_ibfk_1` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '归寝规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of check_rules
-- ----------------------------

-- ----------------------------
-- Table structure for college
-- ----------------------------
DROP TABLE IF EXISTS `college`;
CREATE TABLE `college`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学院名称',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学院表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of college
-- ----------------------------
INSERT INTO `college` VALUES (1, '计算机与人工智能学院', '2026-05-19 16:59:12');
INSERT INTO `college` VALUES (2, '信息科学与技术学院', '2026-05-19 16:59:12');
INSERT INTO `college` VALUES (3, '电气工程学院', '2026-05-19 16:59:12');
INSERT INTO `college` VALUES (4, '机械工程学院', '2026-05-19 16:59:12');
INSERT INTO `college` VALUES (5, '土木工程学院', '2026-05-19 16:59:12');
INSERT INTO `college` VALUES (6, '数学学院', '2026-06-07 01:51:37');
INSERT INTO `college` VALUES (7, '生命学院', '2026-06-07 01:51:49');

-- ----------------------------
-- Table structure for dorm_batch
-- ----------------------------
DROP TABLE IF EXISTS `dorm_batch`;
CREATE TABLE `dorm_batch`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '批次名称(如\"计算机学院2026级\")',
  `college_id` int NOT NULL COMMENT '绑定的学院ID',
  `start_time` datetime NULL DEFAULT NULL COMMENT '问卷开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '问卷结束时间',
  `confirm_deadline` datetime NULL DEFAULT NULL COMMENT '确认截止时间',
  `max_reallocation` int NULL DEFAULT 1 COMMENT '允许重分配最大次数',
  `allow_mix_major` tinyint NULL DEFAULT 0 COMMENT '是否允许跨专业混住(0否/1是)',
  `major_bonus` int NULL DEFAULT 10 COMMENT '同专业匹配加分值',
  `prefer_same_floor` tinyint NULL DEFAULT 1 COMMENT '是否优先同楼层分配(0否/1是)',
  `match_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '批次状态: pending/running/finished',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `college_id`(`college_id` ASC) USING BTREE,
  CONSTRAINT `dorm_batch_ibfk_1` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '选宿批次表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dorm_batch
-- ----------------------------
INSERT INTO `dorm_batch` VALUES (32, '信息学院2023级选宿-测试批次', 2, '2026-06-06 00:00:00', '2026-06-10 23:59:59', '2026-06-15 23:59:59', 1, 0, 10, 1, 'archived', '2026-06-06 21:55:04', '2026-06-07 01:11:33');
INSERT INTO `dorm_batch` VALUES (35, 'college1-full-lifecycle', 1, '2026-06-06 00:00:00', '2026-06-10 23:59:59', '2026-06-15 23:59:59', 1, 0, 10, 1, 'archived', '2026-06-07 00:55:17', '2026-06-07 00:57:41');

-- ----------------------------
-- Table structure for inspection_plans
-- ----------------------------
DROP TABLE IF EXISTS `inspection_plans`;
CREATE TABLE `inspection_plans`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '计划名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '计划描述',
  `inspection_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '检查类型: SAFETY-安全检查, HYGIENE-卫生检查, COMPREHENSIVE-综合检查',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, SCHEDULED-已安排, IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消',
  `scheduled_date` date NULL DEFAULT NULL COMMENT '计划检查日期',
  `building_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查楼栋ID列表，逗号分隔',
  `inspector_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查人员ID列表，逗号分隔',
  `total_rooms` int NULL DEFAULT 0 COMMENT '总房间数',
  `completed_rooms` int NULL DEFAULT 0 COMMENT '已完成房间数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '安全卫生检查计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inspection_plans
-- ----------------------------

-- ----------------------------
-- Table structure for inspection_records
-- ----------------------------
DROP TABLE IF EXISTS `inspection_records`;
CREATE TABLE `inspection_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_id` bigint NULL DEFAULT NULL COMMENT '关联计划ID',
  `building_id` bigint NOT NULL COMMENT '楼栋ID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `inspector_id` bigint NOT NULL COMMENT '检查人ID',
  `inspector_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查人姓名',
  `inspection_time` datetime NOT NULL COMMENT '检查时间',
  `overall_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '总评分',
  `result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PASS' COMMENT '检查结果: PASS-合格, FAIL-不合格',
  `items_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '检查项详情JSON',
  `photos` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '照片URL列表，逗号分隔',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `need_rectification` tinyint NULL DEFAULT 0 COMMENT '是否需要整改: 0否, 1是',
  `rectification_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NONE' COMMENT '整改状态: NONE-无需整改, PENDING-待整改, COMPLETED-已整改, VERIFIED-已核实',
  `rectification_deadline` date NULL DEFAULT NULL COMMENT '整改截止日期',
  `rectification_photos` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '整改后照片URL列表',
  `rectification_time` datetime NULL DEFAULT NULL COMMENT '整改完成时间',
  `verified_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '核实人',
  `verified_time` datetime NULL DEFAULT NULL COMMENT '核实时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `building_id`(`building_id` ASC) USING BTREE,
  INDEX `room_id`(`room_id` ASC) USING BTREE,
  CONSTRAINT `inspection_records_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `inspection_plans` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `inspection_records_ibfk_2` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `inspection_records_ibfk_3` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '安全卫生检查记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inspection_records
-- ----------------------------

-- ----------------------------
-- Table structure for inspection_items
-- ----------------------------
DROP TABLE IF EXISTS `inspection_items`;
CREATE TABLE `inspection_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '检查项名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查类别: SAFETY-安全, HYGIENE-卫生',
  `standard` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查标准描述',
  `max_score` decimal(5, 2) NULL DEFAULT 10.00 COMMENT '最高分值',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0禁用, 1启用',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '安全检查项模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inspection_items
-- ----------------------------
INSERT INTO `inspection_items` VALUES (1, '地面清洁', 'HYGIENE', '地面无垃圾、无污渍，清扫干净', 10.00, 1, 1, NOW(), NOW());
INSERT INTO `inspection_items` VALUES (2, '床铺整理', 'HYGIENE', '被褥叠放整齐，床单平整', 10.00, 1, 2, NOW(), NOW());
INSERT INTO `inspection_items` VALUES (3, '物品摆放', 'HYGIENE', '个人物品摆放整齐有序，不占用公共通道', 10.00, 1, 3, NOW(), NOW());
INSERT INTO `inspection_items` VALUES (4, '门窗玻璃', 'HYGIENE', '门窗玻璃干净明亮，无灰尘', 5.00, 1, 4, NOW(), NOW());
INSERT INTO `inspection_items` VALUES (5, '卫生间清洁', 'HYGIENE', '卫生间无异味、无污垢，洁具干净', 10.00, 1, 5, NOW(), NOW());
INSERT INTO `inspection_items` VALUES (6, '违规电器', 'SAFETY', '无电炉、热得快、电热毯等违规电器', 15.00, 1, 6, NOW(), NOW());
INSERT INTO `inspection_items` VALUES (7, '电线线路', 'SAFETY', '无私拉乱接电线，线路整齐规范', 10.00, 1, 7, NOW(), NOW());
INSERT INTO `inspection_items` VALUES (8, '消防设施', 'SAFETY', '灭火器、消防栓完好，消防通道畅通', 10.00, 1, 8, NOW(), NOW());
INSERT INTO `inspection_items` VALUES (9, '阳台安全', 'SAFETY', '阳台无堆放易燃物，栏杆牢固', 10.00, 1, 9, NOW(), NOW());
INSERT INTO `inspection_items` VALUES (10, '门窗锁具', 'SAFETY', '门窗锁具完好，能正常使用', 10.00, 1, 10, NOW(), NOW());

-- ----------------------------
-- Add creator_id to inspection_plans (if not exists)
-- ----------------------------
ALTER TABLE `inspection_plans` ADD COLUMN `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID' AFTER `completed_rooms`;

-- ----------------------------
-- Table structure for leave_requests
-- ----------------------------
DROP TABLE IF EXISTS `leave_requests`;
CREATE TABLE `leave_requests`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `leave_type` tinyint NULL DEFAULT 0 COMMENT '请假类型: 0事假, 1病假, 2其他',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请假原因',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `destination` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '去向',
  `attachment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件URL(请假条等)',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0待审批, 1已批准, 2已拒绝, 3已撤销, 4已销假',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '审批人ID',
  `approver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批人姓名',
  `approve_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `approve_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批备注',
  `actual_return_time` datetime NULL DEFAULT NULL COMMENT '实际返回时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `leave_requests_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '请假申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of leave_requests
-- ----------------------------

-- ----------------------------
-- Table structure for major
-- ----------------------------
DROP TABLE IF EXISTS `major`;
CREATE TABLE `major`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专业名称',
  `college_id` int NOT NULL COMMENT '所属学院ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `college_id`(`college_id` ASC) USING BTREE,
  CONSTRAINT `major_ibfk_1` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '专业表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of major
-- ----------------------------
INSERT INTO `major` VALUES (1, '计算机科学与技术', 1, '2026-05-19 16:59:12');
INSERT INTO `major` VALUES (2, '软件工程', 1, '2026-05-19 16:59:12');
INSERT INTO `major` VALUES (3, '人工智能', 1, '2026-05-19 16:59:12');
INSERT INTO `major` VALUES (4, '通信工程', 2, '2026-05-19 16:59:12');
INSERT INTO `major` VALUES (5, '电子信息工程', 2, '2026-05-19 16:59:12');
INSERT INTO `major` VALUES (6, '电气工程及其自动化', 3, '2026-05-19 16:59:12');
INSERT INTO `major` VALUES (7, '机械设计制造及其自动化', 4, '2026-05-19 16:59:12');
INSERT INTO `major` VALUES (8, '土木工程', 5, '2026-05-19 16:59:12');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `recipient_id` bigint NULL DEFAULT NULL COMMENT '接收学生ID(关联students.id)',
  `batch_id` int NULL DEFAULT NULL COMMENT '所属批次ID',
  `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知类型(推荐生成/确认提醒/分配结果)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '通知内容',
  `channel` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'inner' COMMENT '发送渠道: sms/email/inner',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '发送状态: pending/sent/failed',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `recipient_id`(`recipient_id` ASC) USING BTREE,
  INDEX `batch_id`(`batch_id` ASC) USING BTREE,
  CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`recipient_id`) REFERENCES `students` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `notification_ibfk_2` FOREIGN KEY (`batch_id`) REFERENCES `dorm_batch` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 139 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (41, 39, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (42, 40, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (43, 41, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (44, 42, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (45, 43, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (46, 49, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (47, 50, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (48, 51, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (49, 52, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (50, 53, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (51, 34, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (52, 35, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (53, 36, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (54, 37, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (55, 38, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (56, 44, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (57, 45, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (58, 46, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (59, 47, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (60, 48, 32, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:52:14');
INSERT INTO `notification` VALUES (61, 9, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (62, 10, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (63, 11, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (64, 12, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (65, 13, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (66, 19, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (67, 20, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (68, 21, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (69, 22, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (70, 23, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (71, 29, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (72, 30, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (73, 31, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (74, 32, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (75, 33, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (76, 4, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (77, 5, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (78, 6, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (79, 7, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (80, 8, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (81, 14, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (82, 15, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (83, 16, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (84, 17, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (85, 18, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (86, 24, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (87, 25, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (88, 26, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (89, 27, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (90, 28, 35, '推荐生成', '您的宿舍分配推荐已生成，请登录系统查看并确认', 'inner', 'sent', '2026-06-07 00:55:56');
INSERT INTO `notification` VALUES (91, 9, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (92, 10, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (93, 11, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (94, 12, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (95, 13, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (96, 19, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (97, 20, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (98, 21, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (99, 22, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (100, 23, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (101, 29, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (102, 30, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (103, 32, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (104, 33, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (105, 5, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (106, 6, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (107, 7, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (108, 8, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (109, 14, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (110, 15, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (111, 16, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (112, 17, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (113, 18, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (114, 24, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (115, 25, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (116, 26, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (117, 27, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (118, 28, 35, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 00:57:14');
INSERT INTO `notification` VALUES (119, 39, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (120, 40, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (121, 41, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (122, 42, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (123, 43, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (124, 49, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (125, 50, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (126, 51, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (127, 52, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (128, 53, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (129, 34, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (130, 35, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (131, 36, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (132, 37, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (133, 38, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (134, 44, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (135, 45, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (136, 46, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (137, 47, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');
INSERT INTO `notification` VALUES (138, 48, 32, 'auto_confirm', '你的选宿分配结果已自动确认，请查看「我的宿舍」', 'inner', 'sent', '2026-06-07 01:05:51');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NULL DEFAULT NULL COMMENT '被操作学生ID(关联students.id)',
  `operator_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作者类型: student/admin/system',
  `operator_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作者标识(学生学号或管理员用户名)',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '动作描述(确认宿舍/智能重匹配/手动调换/管理员修改)',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作详情JSON',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `operation_log_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1, 34, 'student', '20230031', '智能重匹配', '{\"oldAllocationId\":32}', '2026-06-07 00:52:39');
INSERT INTO `operation_log` VALUES (2, 4, 'student', '20230001', '确认宿舍', '{\"allocationId\":57, \"roomId\":3822}', '2026-06-07 00:56:38');
INSERT INTO `operation_log` VALUES (3, 31, 'student', '20230028', '确认宿舍', '{\"allocationId\":54, \"roomId\":4068}', '2026-06-07 00:56:59');

-- ----------------------------
-- Table structure for question_option
-- ----------------------------
DROP TABLE IF EXISTS `question_option`;
CREATE TABLE `question_option`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `q_id` int NOT NULL COMMENT '所属题目ID',
  `option_text` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项文本(如\"早睡\")',
  `option_value` int NULL DEFAULT 0 COMMENT '选项匹配值(相同得该分值)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `q_id`(`q_id` ASC) USING BTREE,
  CONSTRAINT `question_option_ibfk_1` FOREIGN KEY (`q_id`) REFERENCES `questionnaire` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目选项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_option
-- ----------------------------
INSERT INTO `question_option` VALUES (1, 1, '早睡早起', 7);
INSERT INTO `question_option` VALUES (2, 1, '有点熬夜', 5);
INSERT INTO `question_option` VALUES (3, 1, '晚睡，但是安静', 2);
INSERT INTO `question_option` VALUES (16, 2, '不打游戏', 5);
INSERT INTO `question_option` VALUES (17, 2, '偶尔休闲', 5);
INSERT INTO `question_option` VALUES (18, 2, '经常开黑', 5);
INSERT INTO `question_option` VALUES (19, 3, '比较随意', 5);
INSERT INTO `question_option` VALUES (20, 3, '每周打扫', 5);
INSERT INTO `question_option` VALUES (21, 3, '每日保持整洁', 5);
INSERT INTO `question_option` VALUES (22, 4, '完全不能忍', 5);
INSERT INTO `question_option` VALUES (23, 4, '一般可以接受', 5);
INSERT INTO `question_option` VALUES (24, 4, '无所谓', 5);
INSERT INTO `question_option` VALUES (25, 5, '很少在宿舍', 5);
INSERT INTO `question_option` VALUES (26, 5, '晚上回来睡', 5);
INSERT INTO `question_option` VALUES (27, 5, '基本都在', 5);
INSERT INTO `question_option` VALUES (28, 6, '怕热必开', 5);
INSERT INTO `question_option` VALUES (29, 6, '视情况', 5);
INSERT INTO `question_option` VALUES (30, 6, '能不开就不开', 5);

-- ----------------------------
-- Table structure for questionnaire
-- ----------------------------
DROP TABLE IF EXISTS `questionnaire`;
CREATE TABLE `questionnaire`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容',
  `question_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'match' COMMENT '类型: match(匹配类)/bed(床位类)',
  `is_required` tinyint NULL DEFAULT 1 COMMENT '是否必填(0否/1是)',
  `weight` int NULL DEFAULT 1 COMMENT '权重系数',
  `is_active` tinyint NULL DEFAULT 1 COMMENT '是否启用(0否/1是)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问卷题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of questionnaire
-- ----------------------------
INSERT INTO `questionnaire` VALUES (1, '你的作息习惯', 'match', 1, 1, 1, '2026-05-27 21:18:52', '2026-05-27 21:19:20');
INSERT INTO `questionnaire` VALUES (2, '你打游戏的习惯', 'match', 1, 1, 1, '2026-06-05 15:28:31', '2026-06-05 15:28:31');
INSERT INTO `questionnaire` VALUES (3, '你对宿舍卫生的要求', 'match', 1, 1, 1, '2026-06-05 15:28:31', '2026-06-05 15:28:31');
INSERT INTO `questionnaire` VALUES (4, '你对噪音的容忍度', 'match', 1, 1, 1, '2026-06-05 15:28:31', '2026-06-05 15:28:31');
INSERT INTO `questionnaire` VALUES (5, '你课外常在宿舍吗', 'match', 1, 1, 1, '2026-06-05 15:28:31', '2026-06-05 15:28:31');
INSERT INTO `questionnaire` VALUES (6, '你喜欢开空调吗', 'match', 1, 1, 1, '2026-06-05 15:28:31', '2026-06-05 15:28:31');

-- ----------------------------
-- Table structure for relocation_application
-- ----------------------------
DROP TABLE IF EXISTS `relocation_application`;
CREATE TABLE `relocation_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '申请人ID(关联students.id)',
  `batch_id` int NOT NULL COMMENT '所属批次ID',
  `current_room_id` bigint NULL DEFAULT NULL COMMENT '当前房间ID(快照)',
  `current_bed_id` bigint NULL DEFAULT NULL COMMENT '当前床位ID(快照)',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申请理由',
  `preferred_building_id` bigint NULL DEFAULT NULL COMMENT '偏好楼栋ID(可选)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '状态: pending/approved/rejected/executed',
  `reviewed_by` bigint NULL DEFAULT NULL COMMENT '审核人(关联users.id)',
  `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核意见',
  `executed_by` bigint NULL DEFAULT NULL COMMENT '执行人(关联users.id)',
  `new_room_id` bigint NULL DEFAULT NULL COMMENT '执行后新房间ID',
  `new_bed_id` bigint NULL DEFAULT NULL COMMENT '执行后新床位ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  INDEX `batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `current_room_id`(`current_room_id` ASC) USING BTREE,
  INDEX `current_bed_id`(`current_bed_id` ASC) USING BTREE,
  INDEX `reviewed_by`(`reviewed_by` ASC) USING BTREE,
  INDEX `executed_by`(`executed_by` ASC) USING BTREE,
  INDEX `new_room_id`(`new_room_id` ASC) USING BTREE,
  INDEX `new_bed_id`(`new_bed_id` ASC) USING BTREE,
  CONSTRAINT `relocation_application_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `relocation_application_ibfk_2` FOREIGN KEY (`batch_id`) REFERENCES `dorm_batch` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `relocation_application_ibfk_3` FOREIGN KEY (`current_room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `relocation_application_ibfk_4` FOREIGN KEY (`current_bed_id`) REFERENCES `bed` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `relocation_application_ibfk_5` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `relocation_application_ibfk_6` FOREIGN KEY (`executed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `relocation_application_ibfk_7` FOREIGN KEY (`new_room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `relocation_application_ibfk_8` FOREIGN KEY (`new_bed_id`) REFERENCES `bed` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '调换申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of relocation_application
-- ----------------------------

-- ----------------------------
-- Table structure for repairs
-- ----------------------------
DROP TABLE IF EXISTS `repairs`;
CREATE TABLE `repairs`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `student_id` bigint NULL DEFAULT NULL COMMENT '报修学生ID',
  `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报修类型: 电器/水管/门窗/家具/其他',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题描述',
  `images` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL，逗号分隔',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0待处理, 1处理中, 2已完成, 3已关闭',
  `handler` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理人',
  `handler_note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '处理备注',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `room_id`(`room_id` ASC) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `repairs_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `repairs_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '报修记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of repairs
-- ----------------------------

-- ----------------------------
-- Table structure for roommate_group
-- ----------------------------
DROP TABLE IF EXISTS `roommate_group`;
CREATE TABLE `roommate_group`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `batch_id` int NOT NULL COMMENT '所属批次ID',
  `room_id` bigint NULL DEFAULT NULL COMMENT '分配的宿舍ID',
  `member_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '成员学生ID列表(逗号分隔,对应students.id)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `room_id`(`room_id` ASC) USING BTREE,
  CONSTRAINT `roommate_group_ibfk_1` FOREIGN KEY (`batch_id`) REFERENCES `dorm_batch` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `roommate_group_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '室友组表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roommate_group
-- ----------------------------
INSERT INTO `roommate_group` VALUES (10, 32, 4059, '39,40,41,42', '2026-06-07 00:52:14');
INSERT INTO `roommate_group` VALUES (11, 32, 4060, '43', '2026-06-07 00:52:14');
INSERT INTO `roommate_group` VALUES (12, 32, 4061, '49,50,51,52', '2026-06-07 00:52:14');
INSERT INTO `roommate_group` VALUES (13, 32, 4062, '53', '2026-06-07 00:52:14');
INSERT INTO `roommate_group` VALUES (14, 32, 3940, '34,35,36,37', '2026-06-07 00:52:14');
INSERT INTO `roommate_group` VALUES (15, 32, 3941, '38', '2026-06-07 00:52:14');
INSERT INTO `roommate_group` VALUES (16, 32, 3942, '44,45,46,47', '2026-06-07 00:52:14');
INSERT INTO `roommate_group` VALUES (17, 32, 3943, '48', '2026-06-07 00:52:14');
INSERT INTO `roommate_group` VALUES (18, 32, 3940, '34', '2026-06-07 00:52:39');
INSERT INTO `roommate_group` VALUES (19, 35, 4064, '9,10,11,12', '2026-06-07 00:55:56');
INSERT INTO `roommate_group` VALUES (20, 35, 4065, '13', '2026-06-07 00:55:56');
INSERT INTO `roommate_group` VALUES (21, 35, 4066, '19,20,21,22', '2026-06-07 00:55:56');
INSERT INTO `roommate_group` VALUES (22, 35, 4067, '23', '2026-06-07 00:55:56');
INSERT INTO `roommate_group` VALUES (23, 35, 4068, '29,30,31,32', '2026-06-07 00:55:56');
INSERT INTO `roommate_group` VALUES (24, 35, 4069, '33', '2026-06-07 00:55:56');
INSERT INTO `roommate_group` VALUES (25, 35, 3822, '4,5,6,7', '2026-06-07 00:55:56');
INSERT INTO `roommate_group` VALUES (26, 35, 3823, '8', '2026-06-07 00:55:56');
INSERT INTO `roommate_group` VALUES (27, 35, 3824, '14,15,16,17', '2026-06-07 00:55:56');
INSERT INTO `roommate_group` VALUES (28, 35, 1, '18', '2026-06-07 00:55:56');
INSERT INTO `roommate_group` VALUES (29, 35, 3825, '24,25,26,27', '2026-06-07 00:55:56');
INSERT INTO `roommate_group` VALUES (30, 35, 3826, '28', '2026-06-07 00:55:56');

-- ----------------------------
-- Table structure for rooms
-- ----------------------------
DROP TABLE IF EXISTS `rooms`;
CREATE TABLE `rooms`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `building_id` bigint NOT NULL COMMENT '所属楼栋ID',
  `room_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房间号',
  `floor` int NOT NULL COMMENT '楼层',
  `capacity` int NULL DEFAULT 4 COMMENT '床位数',
  `current_count` int NULL DEFAULT 0 COMMENT '当前入住人数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1可用, 0停用',
  `room_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '4人间' COMMENT '房间规格(如4人间/2人间)',
  `window_beds_count` int NULL DEFAULT 2 COMMENT '靠窗床位数量',
  `corridor_beds_count` int NULL DEFAULT 2 COMMENT '靠走廊床位数量',
  `special_tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '特殊标签(无障碍/伤病员)',
  `is_active` tinyint NULL DEFAULT 1 COMMENT '是否启用(1是/0否)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_building_room`(`building_id` ASC, `room_number` ASC) USING BTREE,
  CONSTRAINT `rooms_ibfk_1` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4302 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rooms
-- ----------------------------
INSERT INTO `rooms` VALUES (1, 1, '0105', 1, 4, 1, 1, '4人间', 2, 2, NULL, 1, '2026-05-11 17:49:45', '2026-06-07 00:57:14');
INSERT INTO `rooms` VALUES (2, 2, '0105', 1, 4, 2, 1, '4人间', 2, 2, NULL, 1, '2026-05-11 17:49:45', '2026-06-01 21:40:50');
INSERT INTO `rooms` VALUES (3821, 1, '0101', 1, 4, 0, 0, '4人间', 2, 2, NULL, 0, '2026-06-05 14:34:32', '2026-06-06 21:33:22');
INSERT INTO `rooms` VALUES (3822, 1, '0102', 1, 4, 4, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `rooms` VALUES (3823, 1, '0103', 1, 4, 1, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `rooms` VALUES (3824, 1, '0104', 1, 4, 4, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `rooms` VALUES (3825, 1, '0106', 1, 4, 4, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `rooms` VALUES (3826, 1, '0107', 1, 4, 1, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `rooms` VALUES (3827, 1, '0108', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3828, 1, '0109', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3829, 1, '0110', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3830, 1, '0111', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3831, 1, '0112', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3832, 1, '0113', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3833, 1, '0114', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3834, 1, '0115', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3835, 1, '0116', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3836, 1, '0117', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3837, 1, '0118', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3838, 1, '0119', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3839, 1, '0120', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3840, 1, '0201', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3841, 1, '0202', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3842, 1, '0203', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3843, 1, '0204', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3844, 1, '0205', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3845, 1, '0206', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3846, 1, '0207', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3847, 1, '0208', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3848, 1, '0209', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3849, 1, '0210', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3850, 1, '0211', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3851, 1, '0212', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3852, 1, '0213', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3853, 1, '0214', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3854, 1, '0215', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3855, 1, '0216', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3856, 1, '0217', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3857, 1, '0218', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3858, 1, '0219', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3859, 1, '0220', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3860, 1, '0301', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3861, 1, '0302', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3862, 1, '0303', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3863, 1, '0304', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3864, 1, '0305', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3865, 1, '0306', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3866, 1, '0307', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3867, 1, '0308', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3868, 1, '0309', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3869, 1, '0310', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3870, 1, '0311', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3871, 1, '0312', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3872, 1, '0313', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3873, 1, '0314', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3874, 1, '0315', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3875, 1, '0316', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3876, 1, '0317', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3877, 1, '0318', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3878, 1, '0319', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3879, 1, '0320', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3880, 1, '0401', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3881, 1, '0402', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3882, 1, '0403', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3883, 1, '0404', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3884, 1, '0405', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3885, 1, '0406', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3886, 1, '0407', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3887, 1, '0408', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3888, 1, '0409', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3889, 1, '0410', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3890, 1, '0411', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3891, 1, '0412', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3892, 1, '0413', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3893, 1, '0414', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3894, 1, '0415', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3895, 1, '0416', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3896, 1, '0417', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3897, 1, '0418', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3898, 1, '0419', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3899, 1, '0420', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3900, 1, '0501', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3901, 1, '0502', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3902, 1, '0503', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3903, 1, '0504', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3904, 1, '0505', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3905, 1, '0506', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3906, 1, '0507', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3907, 1, '0508', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3908, 1, '0509', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3909, 1, '0510', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3910, 1, '0511', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3911, 1, '0512', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3912, 1, '0513', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3913, 1, '0514', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3914, 1, '0515', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3915, 1, '0516', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3916, 1, '0517', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3917, 1, '0518', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3918, 1, '0519', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3919, 1, '0520', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3920, 1, '0601', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3921, 1, '0602', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3922, 1, '0603', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3923, 1, '0604', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3924, 1, '0605', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3925, 1, '0606', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3926, 1, '0607', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3927, 1, '0608', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3928, 1, '0609', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3929, 1, '0610', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3930, 1, '0611', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3931, 1, '0612', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3932, 1, '0613', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3933, 1, '0614', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3934, 1, '0615', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3935, 1, '0616', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3936, 1, '0617', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3937, 1, '0618', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3938, 1, '0619', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3939, 1, '0620', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3940, 2, '0101', 1, 4, 4, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `rooms` VALUES (3941, 2, '0102', 1, 4, 1, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `rooms` VALUES (3942, 2, '0103', 1, 4, 4, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `rooms` VALUES (3943, 2, '0104', 1, 4, 1, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `rooms` VALUES (3944, 2, '0106', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3945, 2, '0107', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3946, 2, '0108', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3947, 2, '0109', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3948, 2, '0110', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3949, 2, '0111', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3950, 2, '0112', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3951, 2, '0113', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3952, 2, '0114', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3953, 2, '0115', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3954, 2, '0116', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3955, 2, '0117', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3956, 2, '0118', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3957, 2, '0119', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3958, 2, '0120', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3959, 2, '0201', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3960, 2, '0202', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3961, 2, '0203', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3962, 2, '0204', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3963, 2, '0205', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3964, 2, '0206', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3965, 2, '0207', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3966, 2, '0208', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3967, 2, '0209', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3968, 2, '0210', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3969, 2, '0211', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3970, 2, '0212', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3971, 2, '0213', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3972, 2, '0214', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3973, 2, '0215', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3974, 2, '0216', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3975, 2, '0217', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3976, 2, '0218', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3977, 2, '0219', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3978, 2, '0220', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3979, 2, '0301', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3980, 2, '0302', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3981, 2, '0303', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3982, 2, '0304', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3983, 2, '0305', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3984, 2, '0306', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3985, 2, '0307', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3986, 2, '0308', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3987, 2, '0309', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3988, 2, '0310', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3989, 2, '0311', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3990, 2, '0312', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3991, 2, '0313', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3992, 2, '0314', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3993, 2, '0315', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3994, 2, '0316', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3995, 2, '0317', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3996, 2, '0318', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3997, 2, '0319', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3998, 2, '0320', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (3999, 2, '0401', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4000, 2, '0402', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4001, 2, '0403', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4002, 2, '0404', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4003, 2, '0405', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4004, 2, '0406', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4005, 2, '0407', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4006, 2, '0408', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4007, 2, '0409', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4008, 2, '0410', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4009, 2, '0411', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4010, 2, '0412', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4011, 2, '0413', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4012, 2, '0414', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4013, 2, '0415', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4014, 2, '0416', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4015, 2, '0417', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4016, 2, '0418', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4017, 2, '0419', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4018, 2, '0420', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4019, 2, '0501', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4020, 2, '0502', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4021, 2, '0503', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4022, 2, '0504', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4023, 2, '0505', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4024, 2, '0506', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4025, 2, '0507', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4026, 2, '0508', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4027, 2, '0509', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4028, 2, '0510', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4029, 2, '0511', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4030, 2, '0512', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4031, 2, '0513', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4032, 2, '0514', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4033, 2, '0515', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4034, 2, '0516', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4035, 2, '0517', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4036, 2, '0518', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4037, 2, '0519', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4038, 2, '0520', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4039, 2, '0601', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4040, 2, '0602', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4041, 2, '0603', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4042, 2, '0604', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4043, 2, '0605', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4044, 2, '0606', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4045, 2, '0607', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4046, 2, '0608', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4047, 2, '0609', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4048, 2, '0610', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4049, 2, '0611', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4050, 2, '0612', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4051, 2, '0613', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4052, 2, '0614', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4053, 2, '0615', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4054, 2, '0616', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4055, 2, '0617', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4056, 2, '0618', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4057, 2, '0619', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4058, 2, '0620', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4059, 121, '0101', 1, 4, 4, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `rooms` VALUES (4060, 121, '0102', 1, 4, 1, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `rooms` VALUES (4061, 121, '0103', 1, 4, 4, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `rooms` VALUES (4062, 121, '0104', 1, 4, 1, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `rooms` VALUES (4063, 121, '0105', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4064, 121, '0106', 1, 4, 4, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `rooms` VALUES (4065, 121, '0107', 1, 4, 1, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `rooms` VALUES (4066, 121, '0108', 1, 4, 4, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `rooms` VALUES (4067, 121, '0109', 1, 4, 1, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `rooms` VALUES (4068, 121, '0110', 1, 4, 4, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `rooms` VALUES (4069, 121, '0111', 1, 4, 1, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `rooms` VALUES (4070, 121, '0112', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4071, 121, '0113', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4072, 121, '0114', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4073, 121, '0115', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4074, 121, '0116', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4075, 121, '0117', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4076, 121, '0118', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4077, 121, '0119', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4078, 121, '0120', 1, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4079, 121, '0201', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4080, 121, '0202', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4081, 121, '0203', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4082, 121, '0204', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4083, 121, '0205', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4084, 121, '0206', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4085, 121, '0207', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4086, 121, '0208', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4087, 121, '0209', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4088, 121, '0210', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4089, 121, '0211', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4090, 121, '0212', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4091, 121, '0213', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4092, 121, '0214', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4093, 121, '0215', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4094, 121, '0216', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4095, 121, '0217', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4096, 121, '0218', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4097, 121, '0219', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4098, 121, '0220', 2, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4099, 121, '0301', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4100, 121, '0302', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4101, 121, '0303', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4102, 121, '0304', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4103, 121, '0305', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4104, 121, '0306', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4105, 121, '0307', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4106, 121, '0308', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4107, 121, '0309', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4108, 121, '0310', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4109, 121, '0311', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4110, 121, '0312', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4111, 121, '0313', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4112, 121, '0314', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4113, 121, '0315', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4114, 121, '0316', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4115, 121, '0317', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4116, 121, '0318', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4117, 121, '0319', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4118, 121, '0320', 3, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4119, 121, '0401', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4120, 121, '0402', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4121, 121, '0403', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4122, 121, '0404', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4123, 121, '0405', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4124, 121, '0406', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4125, 121, '0407', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4126, 121, '0408', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4127, 121, '0409', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4128, 121, '0410', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4129, 121, '0411', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4130, 121, '0412', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4131, 121, '0413', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4132, 121, '0414', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4133, 121, '0415', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4134, 121, '0416', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4135, 121, '0417', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4136, 121, '0418', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4137, 121, '0419', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4138, 121, '0420', 4, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4139, 121, '0501', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4140, 121, '0502', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4141, 121, '0503', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4142, 121, '0504', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4143, 121, '0505', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4144, 121, '0506', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4145, 121, '0507', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4146, 121, '0508', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4147, 121, '0509', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4148, 121, '0510', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4149, 121, '0511', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4150, 121, '0512', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4151, 121, '0513', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4152, 121, '0514', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4153, 121, '0515', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4154, 121, '0516', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4155, 121, '0517', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4156, 121, '0518', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4157, 121, '0519', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4158, 121, '0520', 5, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4159, 121, '0601', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4160, 121, '0602', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4161, 121, '0603', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4162, 121, '0604', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4163, 121, '0605', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4164, 121, '0606', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4165, 121, '0607', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4166, 121, '0608', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4167, 121, '0609', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4168, 121, '0610', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4169, 121, '0611', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4170, 121, '0612', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4171, 121, '0613', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4172, 121, '0614', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4173, 121, '0615', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4174, 121, '0616', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4175, 121, '0617', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4176, 121, '0618', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4177, 121, '0619', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `rooms` VALUES (4178, 121, '0620', 6, 4, 0, 1, '4人间', 2, 2, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');

-- ----------------------------
-- Table structure for student_answer
-- ----------------------------
DROP TABLE IF EXISTS `student_answer`;
CREATE TABLE `student_answer`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID(关联students.id)',
  `q_id` int NOT NULL COMMENT '题目ID',
  `option_id` int NOT NULL COMMENT '选择的选项ID',
  `submit_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_question`(`student_id` ASC, `q_id` ASC) USING BTREE,
  INDEX `q_id`(`q_id` ASC) USING BTREE,
  INDEX `option_id`(`option_id` ASC) USING BTREE,
  CONSTRAINT `student_answer_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `student_answer_ibfk_2` FOREIGN KEY (`q_id`) REFERENCES `questionnaire` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `student_answer_ibfk_3` FOREIGN KEY (`option_id`) REFERENCES `question_option` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '新生答案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_answer
-- ----------------------------
INSERT INTO `student_answer` VALUES (2, 1, 1, 1, '2026-05-29 16:03:52');

-- ----------------------------
-- Table structure for students
-- ----------------------------
DROP TABLE IF EXISTS `students`;
CREATE TABLE `students`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '院系',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班级',
  `college_id` int NULL DEFAULT NULL COMMENT '所属学院ID',
  `major_id` int NULL DEFAULT NULL COMMENT '所属专业ID',
  `payment_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'unpaid' COMMENT '缴费状态: paid/unpaid',
  `dorm_batch_id` int NULL DEFAULT NULL COMMENT '参与选宿批次ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户ID(users.id)',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `room_id` bigint NULL DEFAULT NULL COMMENT '入住房间ID',
  `bed_number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '床位号',
  `check_in_date` datetime NULL DEFAULT NULL COMMENT '入住日期',
  `check_out_date` datetime NULL DEFAULT NULL COMMENT '退宿日期',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1在住, 0已退宿',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `student_no`(`student_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `room_id`(`room_id` ASC) USING BTREE,
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `students_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of students
-- ----------------------------
INSERT INTO `students` VALUES (1, '20232502', '张三', '男', NULL, '计算机与人工智能学院', '计科2301', 1, 1, 'unpaid', NULL, 59, NULL, 2, NULL, NULL, '2026-06-01 17:00:17', 0, '2026-05-19 19:15:28', '2026-06-01 21:40:50');
INSERT INTO `students` VALUES (2, '20232501', '李四', '男', NULL, '计算机与人工与人工智能学院', '计科2301', 1, 1, 'paid', NULL, 62, NULL, 2, NULL, NULL, '2026-06-01 17:00:14', 0, '2026-05-19 22:23:55', '2026-06-01 21:40:27');
INSERT INTO `students` VALUES (4, '20230001', '张伟', '男', '13920230001', '计算机与人工智能学院', '计算机科学与技术2023级1班', 1, 1, 'paid', 35, 70, NULL, 3822, 'A', '2026-06-07 00:56:39', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:56:38');
INSERT INTO `students` VALUES (5, '20230002', '王磊', '男', '13920230002', '计算机与人工智能学院', '计算机科学与技术2023级1班', 1, 1, 'paid', 35, NULL, NULL, 3822, 'B', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (6, '20230003', '李强', '男', '13920230003', '计算机与人工智能学院', '计算机科学与技术2023级1班', 1, 1, 'paid', 35, NULL, NULL, 3822, 'C', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (7, '20230004', '刘洋', '男', '13920230004', '计算机与人工智能学院', '计算机科学与技术2023级1班', 1, 1, 'paid', 35, NULL, NULL, 3822, 'D', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (8, '20230005', '陈明', '男', '13920230005', '计算机与人工智能学院', '计算机科学与技术2023级1班', 1, 1, 'paid', 35, NULL, NULL, 3823, 'A', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (9, '20230006', '王芳', '女', '13920230006', '计算机与人工智能学院', '计算机科学与技术2023级1班', 1, 1, 'paid', 35, NULL, NULL, 4064, 'A', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (10, '20230007', '李娜', '女', '13920230007', '计算机与人工智能学院', '计算机科学与技术2023级1班', 1, 1, 'paid', 35, NULL, NULL, 4064, 'B', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (11, '20230008', '张敏', '女', '13920230008', '计算机与人工智能学院', '计算机科学与技术2023级1班', 1, 1, 'paid', 35, NULL, NULL, 4064, 'C', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (12, '20230009', '刘婷', '女', '13920230009', '计算机与人工智能学院', '计算机科学与技术2023级1班', 1, 1, 'paid', 35, NULL, NULL, 4064, 'D', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (13, '20230010', '陈静', '女', '13920230010', '计算机与人工智能学院', '计算机科学与技术2023级1班', 1, 1, 'paid', 35, NULL, NULL, 4065, 'A', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (14, '20230011', '杨帆', '男', '13920230011', '计算机与人工智能学院', '软件工程2023级1班', 1, 2, 'paid', 35, NULL, NULL, 3824, 'A', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (15, '20230012', '赵鹏', '男', '13920230012', '计算机与人工智能学院', '软件工程2023级1班', 1, 2, 'paid', 35, NULL, NULL, 3824, 'B', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (16, '20230013', '黄俊', '男', '13920230013', '计算机与人工智能学院', '软件工程2023级1班', 1, 2, 'paid', 35, NULL, NULL, 3824, 'C', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (17, '20230014', '周杰', '男', '13920230014', '计算机与人工智能学院', '软件工程2023级1班', 1, 2, 'paid', 35, NULL, NULL, 3824, 'D', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (18, '20230015', '吴浩', '男', '13920230015', '计算机与人工智能学院', '软件工程2023级1班', 1, 2, 'paid', 35, NULL, NULL, 1, 'A', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (19, '20230016', '杨雪', '女', '13920230016', '计算机与人工智能学院', '软件工程2023级1班', 1, 2, 'paid', 35, NULL, NULL, 4066, 'A', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (20, '20230017', '赵丽', '女', '13920230017', '计算机与人工智能学院', '软件工程2023级1班', 1, 2, 'paid', 35, NULL, NULL, 4066, 'B', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (21, '20230018', '黄蓉', '女', '13920230018', '计算机与人工智能学院', '软件工程2023级1班', 1, 2, 'paid', 35, NULL, NULL, 4066, 'C', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (22, '20230019', '周琳', '女', '13920230019', '计算机与人工智能学院', '软件工程2023级1班', 1, 2, 'paid', 35, NULL, NULL, 4066, 'D', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (23, '20230020', '吴燕', '女', '13920230020', '计算机与人工智能学院', '软件工程2023级1班', 1, 2, 'paid', 35, NULL, NULL, 4067, 'A', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (24, '20230021', '徐凯', '男', '13920230021', '计算机与人工智能学院', '人工智能2023级1班', 1, 3, 'paid', 35, NULL, NULL, 3825, 'A', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (25, '20230022', '孙涛', '男', '13920230022', '计算机与人工智能学院', '人工智能2023级1班', 1, 3, 'paid', 35, NULL, NULL, 3825, 'B', '2026-06-07 00:57:15', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (26, '20230023', '马超', '男', '13920230023', '计算机与人工智能学院', '人工智能2023级1班', 1, 3, 'paid', 35, NULL, NULL, 3825, 'C', '2026-06-07 00:57:15', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (27, '20230024', '朱军', '男', '13920230024', '计算机与人工智能学院', '人工智能2023级1班', 1, 3, 'paid', 35, NULL, NULL, 3825, 'D', '2026-06-07 00:57:15', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (28, '20230025', '胡斌', '男', '13920230025', '计算机与人工智能学院', '人工智能2023级1班', 1, 3, 'paid', 35, NULL, NULL, 3826, 'A', '2026-06-07 00:57:15', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (29, '20230026', '徐艳', '女', '13920230026', '计算机与人工智能学院', '人工智能2023级1班', 1, 3, 'paid', 35, NULL, NULL, 4068, 'A', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (30, '20230027', '孙悦', '女', '13920230027', '计算机与人工智能学院', '人工智能2023级1班', 1, 3, 'paid', 35, NULL, NULL, 4068, 'B', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (31, '20230028', '马莉', '女', '13920230028', '计算机与人工智能学院', '人工智能2023级1班', 1, 3, 'paid', 35, 71, NULL, 4068, 'C', '2026-06-07 00:57:00', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:56:59');
INSERT INTO `students` VALUES (32, '20230029', '朱婷', '女', '13920230029', '计算机与人工智能学院', '人工智能2023级1班', 1, 3, 'paid', 35, 72, NULL, 4068, 'D', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (33, '20230030', '胡娟', '女', '13920230030', '计算机与人工智能学院', '人工智能2023级1班', 1, 3, 'paid', 35, NULL, NULL, 4069, 'A', '2026-06-07 00:57:14', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 00:57:14');
INSERT INTO `students` VALUES (34, '20230031', '郭磊', '男', '13920230031', '信息科学与技术学院', '通信工程2023级1班', 2, 4, 'paid', 32, 73, NULL, 3940, 'A', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (35, '20230032', '林峰', '男', '13920230032', '信息科学与技术学院', '通信工程2023级1班', 2, 4, 'paid', 32, NULL, NULL, 3940, 'B', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (36, '20230033', '何健', '男', '13920230033', '信息科学与技术学院', '通信工程2023级1班', 2, 4, 'paid', 32, NULL, NULL, 3940, 'C', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (37, '20230034', '高峰', '男', '13920230034', '信息科学与技术学院', '通信工程2023级1班', 2, 4, 'paid', 32, 74, NULL, 3940, 'D', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (38, '20230035', '罗辉', '男', '13920230035', '信息科学与技术学院', '通信工程2023级1班', 2, 4, 'paid', 32, NULL, NULL, 3941, 'A', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (39, '20230036', '郭雨', '女', '13920230036', '信息科学与技术学院', '通信工程2023级1班', 2, 4, 'paid', 32, NULL, NULL, 4059, 'A', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (40, '20230037', '林晓', '女', '13920230037', '信息科学与技术学院', '通信工程2023级1班', 2, 4, 'paid', 32, NULL, NULL, 4059, 'B', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (41, '20230038', '何雪', '女', '13920230038', '信息科学与技术学院', '通信工程2023级1班', 2, 4, 'paid', 32, NULL, NULL, 4059, 'C', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (42, '20230039', '高梦', '女', '13920230039', '信息科学与技术学院', '通信工程2023级1班', 2, 4, 'paid', 32, NULL, NULL, 4059, 'D', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (43, '20230040', '罗琳', '女', '13920230040', '信息科学与技术学院', '通信工程2023级1班', 2, 4, 'paid', 32, NULL, NULL, 4060, 'A', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (44, '20230041', '梁宇', '男', '13920230041', '信息科学与技术学院', '电子信息工程2023级1班', 2, 5, 'paid', 32, NULL, NULL, 3942, 'A', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (45, '20230042', '宋涛', '男', '13920230042', '信息科学与技术学院', '电子信息工程2023级1班', 2, 5, 'paid', 32, NULL, NULL, 3942, 'B', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (46, '20230043', '郑鑫', '男', '13920230043', '信息科学与技术学院', '电子信息工程2023级1班', 2, 5, 'paid', 32, NULL, NULL, 3942, 'C', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (47, '20230044', '谢永', '男', '13920230044', '信息科学与技术学院', '电子信息工程2023级1班', 2, 5, 'paid', 32, NULL, NULL, 3942, 'D', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (48, '20230045', '韩冰', '男', '13920230045', '信息科学与技术学院', '电子信息工程2023级1班', 2, 5, 'paid', 32, NULL, NULL, 3943, 'A', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (49, '20230046', '梁欣', '女', '13920230046', '信息科学与技术学院', '电子信息工程2023级1班', 2, 5, 'paid', 32, NULL, NULL, 4061, 'A', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (50, '20230047', '宋佳', '女', '13920230047', '信息科学与技术学院', '电子信息工程2023级1班', 2, 5, 'paid', 32, NULL, NULL, 4061, 'B', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (51, '20230048', '郑雅', '女', '13920230048', '信息科学与技术学院', '电子信息工程2023级1班', 2, 5, 'paid', 32, NULL, NULL, 4061, 'C', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (52, '20230049', '谢蕊', '女', '13920230049', '信息科学与技术学院', '电子信息工程2023级1班', 2, 5, 'paid', 32, NULL, NULL, 4061, 'D', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (53, '20230050', '韩雪', '女', '13920230050', '信息科学与技术学院', '电子信息工程2023级1班', 2, 5, 'paid', 32, NULL, NULL, 4062, 'A', '2026-06-07 01:05:51', NULL, 1, '2026-06-05 14:34:32', '2026-06-07 01:05:51');
INSERT INTO `students` VALUES (54, '20230051', '唐亮', '男', '13920230051', '电气工程学院', '电气工程及其自动化2023级1班', 3, 6, 'unpaid', NULL, 75, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 17:38:43');
INSERT INTO `students` VALUES (55, '20230052', '曹阳', '男', '13920230052', '电气工程学院', '电气工程及其自动化2023级1班', 3, 6, 'unpaid', NULL, 76, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 17:38:43');
INSERT INTO `students` VALUES (56, '20230053', '邓辉', '男', '13920230053', '电气工程学院', '电气工程及其自动化2023级1班', 3, 6, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 17:38:43');
INSERT INTO `students` VALUES (57, '20230054', '许强', '男', '13920230054', '电气工程学院', '电气工程及其自动化2023级1班', 3, 6, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 17:38:43');
INSERT INTO `students` VALUES (58, '20230055', '冯刚', '男', '13920230055', '电气工程学院', '电气工程及其自动化2023级1班', 3, 6, 'unpaid', NULL, 77, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 17:38:43');
INSERT INTO `students` VALUES (59, '20230056', '唐艺', '女', '13920230056', '电气工程学院', '电气工程及其自动化2023级1班', 3, 6, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 17:38:43');
INSERT INTO `students` VALUES (60, '20230057', '曹颖', '女', '13920230057', '电气工程学院', '电气工程及其自动化2023级1班', 3, 6, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 17:38:43');
INSERT INTO `students` VALUES (61, '20230058', '邓洁', '女', '13920230058', '电气工程学院', '电气工程及其自动化2023级1班', 3, 6, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 17:38:43');
INSERT INTO `students` VALUES (62, '20230059', '许诺', '女', '13920230059', '电气工程学院', '电气工程及其自动化2023级1班', 3, 6, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 17:38:43');
INSERT INTO `students` VALUES (63, '20230060', '冯媛', '女', '13920230060', '电气工程学院', '电气工程及其自动化2023级1班', 3, 6, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-07 17:38:43');
INSERT INTO `students` VALUES (64, '20230061', '彭飞', '男', '13920230061', '机械工程学院', '机械设计制造及其自动化2023级1班', 4, 7, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (65, '20230062', '蒋浩', '男', '13920230062', '机械工程学院', '机械设计制造及其自动化2023级1班', 4, 7, 'unpaid', NULL, 79, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-06 21:43:55');
INSERT INTO `students` VALUES (66, '20230063', '蔡勇', '男', '13920230063', '机械工程学院', '机械设计制造及其自动化2023级1班', 4, 7, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (67, '20230064', '贾磊', '男', '13920230064', '机械工程学院', '机械设计制造及其自动化2023级1班', 4, 7, 'unpaid', NULL, 78, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-06 21:43:36');
INSERT INTO `students` VALUES (68, '20230065', '魏超', '男', '13920230065', '机械工程学院', '机械设计制造及其自动化2023级1班', 4, 7, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (69, '20230066', '彭倩', '女', '13920230066', '机械工程学院', '机械设计制造及其自动化2023级1班', 4, 7, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (70, '20230067', '蒋莉', '女', '13920230067', '机械工程学院', '机械设计制造及其自动化2023级1班', 4, 7, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (71, '20230068', '蔡琳', '女', '13920230068', '机械工程学院', '机械设计制造及其自动化2023级1班', 4, 7, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (72, '20230069', '贾静', '女', '13920230069', '机械工程学院', '机械设计制造及其自动化2023级1班', 4, 7, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (73, '20230070', '魏婷', '女', '13920230070', '机械工程学院', '机械设计制造及其自动化2023级1班', 4, 7, 'unpaid', NULL, 80, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-06 21:44:17');
INSERT INTO `students` VALUES (74, '20230071', '薛涛', '男', '13920230071', '土木工程学院', '土木工程2023级1班', 5, 8, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (75, '20230072', '叶帆', '男', '13920230072', '土木工程学院', '土木工程2023级1班', 5, 8, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (76, '20230073', '阎鹏', '男', '13920230073', '土木工程学院', '土木工程2023级1班', 5, 8, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (77, '20230074', '余杰', '男', '13920230074', '土木工程学院', '土木工程2023级1班', 5, 8, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (78, '20230075', '潘明', '男', '13920230075', '土木工程学院', '土木工程2023级1班', 5, 8, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (79, '20230076', '薛雯', '女', '13920230076', '土木工程学院', '土木工程2023级1班', 5, 8, 'unpaid', NULL, 81, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-06 21:44:38');
INSERT INTO `students` VALUES (80, '20230077', '叶琳', '女', '13920230077', '土木工程学院', '土木工程2023级1班', 5, 8, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (81, '20230078', '阎雨', '女', '13920230078', '土木工程学院', '土木工程2023级1班', 5, 8, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (82, '20230079', '余敏', '女', '13920230079', '土木工程学院', '土木工程2023级1班', 5, 8, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');
INSERT INTO `students` VALUES (83, '20230080', '潘虹', '女', '13920230080', '土木工程学院', '土木工程2023级1班', 5, 8, 'unpaid', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-05 14:34:32', '2026-06-05 14:34:32');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'user' COMMENT '角色: admin, manager, user',
  `student_id` bigint NULL DEFAULT NULL COMMENT '关联学生ID(students.id)',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1启用, 0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '$2a$10$TGS01nW2sFuKlwZStnafDOeHpZ8yxGFcWaBuSUaXzc5EnH.tcxkqq', '系统管理员', 'ADMIN', NULL, NULL, NULL, NULL, 1, '2026-05-11 17:49:45', '2026-05-25 19:23:01');
INSERT INTO `users` VALUES (59, '20232502', '$2a$10$37LOdGCWajXUIEui4DFRpesjgBF4Tv7OBt2e.ALTmd5RwIdRFdbFC', '张三', 'STUDENT', NULL, NULL, NULL, NULL, 1, '2026-05-21 01:04:14', '2026-05-21 01:04:14');
INSERT INTO `users` VALUES (62, '20232501', '$2a$10$4bUyCrDayl/fXoqeZIf49e9dKr1olMg0sc1czZr0pgLAOeoztqN9C', '李四', 'STUDENT', NULL, NULL, NULL, NULL, 1, '2026-05-25 16:29:12', '2026-05-25 16:29:12');
INSERT INTO `users` VALUES (70, '20230001', '$2a$10$W9XIzG6Dds/lDzyXW1eG3e6EEITzVCcrnVY/510GgY35ExTTYComi', '张伟', 'STUDENT', NULL, '13920230001', NULL, NULL, 1, '2026-06-06 21:37:57', '2026-06-06 21:37:57');
INSERT INTO `users` VALUES (71, '20230028', '$2a$10$S3Cbkpuzmnrvvo7sv53EaOcZGDMdTBOODKh86QnsDttzyNG2OQ18S', '马莉', 'STUDENT', NULL, '13920230028', NULL, NULL, 1, '2026-06-06 21:39:05', '2026-06-06 21:39:05');
INSERT INTO `users` VALUES (72, '20230029', '$2a$10$ovMT8myQ81a3SIl.XakNPus5jUydXqjdMDJXs.SQFfe0lTZa9Wppy', '朱婷', 'STUDENT', NULL, '13920230029', NULL, NULL, 1, '2026-06-06 21:39:28', '2026-06-06 21:39:28');
INSERT INTO `users` VALUES (73, '20230031', '$2a$10$ktDasiwcJZMrbi4DGrcUve9nba25MuT6lITwT8VLEc1xK7wTaoUTe', '郭磊', 'STUDENT', NULL, '13920230031', NULL, NULL, 1, '2026-06-06 21:39:49', '2026-06-06 21:39:49');
INSERT INTO `users` VALUES (74, '20230034', '$2a$10$F13de7NOevB9ZJkJA28Ms.76reKfjHZNS999ItbtqgaLEBGOCkd3u', '高峰', 'STUDENT', NULL, '13920230034', NULL, NULL, 1, '2026-06-06 21:41:35', '2026-06-06 21:41:35');
INSERT INTO `users` VALUES (75, '20230051', '$2a$10$yKQp9FpKp6TltBUTh3YjC.Vruf/l2Bb5qDSTxtO30vRNdBITcK1c6', '唐亮', 'STUDENT', NULL, '13920230051', NULL, NULL, 1, '2026-06-06 21:42:01', '2026-06-06 21:42:01');
INSERT INTO `users` VALUES (76, '20230052', '$2a$10$Zkk5/1lO7ad6zP/hphAhZeu1zXEKaS9OX4EgFGI2R9rWF.8hTw.4e', '曹阳', 'STUDENT', NULL, '13920230052', NULL, NULL, 1, '2026-06-06 21:42:25', '2026-06-06 21:42:25');
INSERT INTO `users` VALUES (77, '20230055', '$2a$10$h5Do7EbwYfAjJfyo5GyGFu6EKOGnHOAkbeLdqifmUHj3SYprT9dsK', '冯刚', 'STUDENT', NULL, '13920230055', NULL, NULL, 1, '2026-06-06 21:42:49', '2026-06-06 21:42:49');
INSERT INTO `users` VALUES (78, '20230064', '$2a$10$O4gNZrhDNJjHGPEArtFYie1BMF6uXHf0.FbAK71ZzfPEyLOnVt4ui', '贾磊', 'STUDENT', NULL, '13920230064', NULL, NULL, 1, '2026-06-06 21:43:36', '2026-06-06 21:43:36');
INSERT INTO `users` VALUES (79, '20230062', '$2a$10$4EnyfUBsNYAWVzIWOLWVX.9C/u/qbag6yQEBkkATXNWoULNp3SMje', '蒋浩', 'STUDENT', NULL, '13920230062', NULL, NULL, 1, '2026-06-06 21:43:55', '2026-06-06 21:43:55');
INSERT INTO `users` VALUES (80, '20230070', '$2a$10$sRCgnzWtzs9G27GxOaPyS.EHozdd4U8z4V7AvQpmP/X5OZDvHgBD.', '魏婷', 'STUDENT', NULL, '13920230070', NULL, NULL, 1, '2026-06-06 21:44:17', '2026-06-06 21:44:17');
INSERT INTO `users` VALUES (81, '20230076', '$2a$10$XfNh5hyToquMDmWBtMiU7.tHyj84MtL7mrGl4H5cKlLhXCHGshARy', '薛雯', 'STUDENT', NULL, '13920230076', NULL, NULL, 1, '2026-06-06 21:44:38', '2026-06-06 21:44:38');

-- ----------------------------
-- Table structure for utility_fees
-- ----------------------------
DROP TABLE IF EXISTS `utility_fees`;
CREATE TABLE `utility_fees`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `year` int NOT NULL COMMENT '年份',
  `month` int NOT NULL COMMENT '月份',
  `electricity_start` decimal(10, 2) NULL DEFAULT NULL COMMENT '电表起始读数',
  `electricity_end` decimal(10, 2) NULL DEFAULT NULL COMMENT '电表结束读数',
  `electricity_usage` decimal(10, 2) NULL DEFAULT NULL COMMENT '用电量(度)',
  `electricity_fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '电费',
  `water_start` decimal(10, 2) NULL DEFAULT NULL COMMENT '水表起始读数',
  `water_end` decimal(10, 2) NULL DEFAULT NULL COMMENT '水表结束读数',
  `water_usage` decimal(10, 2) NULL DEFAULT NULL COMMENT '用水量(吨)',
  `water_fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '水费',
  `total_fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '总费用',
  `status` tinyint NULL DEFAULT 0 COMMENT '缴费状态: 0未缴, 1已缴',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '缴费时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_room_year_month`(`room_id` ASC, `year` ASC, `month` ASC) USING BTREE,
  CONSTRAINT `utility_fees_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '水电费表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of utility_fees
-- ----------------------------

-- ----------------------------
-- Table structure for visitors
-- ----------------------------
DROP TABLE IF EXISTS `visitors`;
CREATE TABLE `visitors`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL COMMENT '被访房间ID',
  `visitor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '访客姓名',
  `visitor_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '访客电话',
  `visitor_id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '访客身份证号',
  `relation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '与被访人关系',
  `purpose` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来访目的',
  `visit_time` datetime NOT NULL COMMENT '来访时间',
  `leave_time` datetime NULL DEFAULT NULL COMMENT '离开时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1在访, 0已离开',
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `room_id`(`room_id` ASC) USING BTREE,
  CONSTRAINT `visitors_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '访客记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of visitors
-- ----------------------------

-- ----------------------------
-- Procedure structure for add_column_if_missing
-- ----------------------------
DROP PROCEDURE IF EXISTS `add_column_if_missing`;
delimiter ;;
CREATE PROCEDURE `add_column_if_missing`(IN tbl_name  VARCHAR(128),
    IN col_name  VARCHAR(128),
    IN col_def   TEXT)
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
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
