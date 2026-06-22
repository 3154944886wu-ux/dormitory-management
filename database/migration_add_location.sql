-- 位置验证功能迁移脚本
-- 为 check_rules 表添加位置验证字段
-- 西南交通大学犀浦校区

USE dormitory;

-- 添加位置验证字段
ALTER TABLE check_rules 
ADD COLUMN allowed_latitude DECIMAL(10,7) COMMENT '允许打卡纬度' AFTER remark,
ADD COLUMN allowed_longitude DECIMAL(10,7) COMMENT '允许打卡经度' AFTER allowed_latitude,
ADD COLUMN allowed_radius INT DEFAULT 500 COMMENT '允许范围半径(米)' AFTER allowed_longitude;

-- 更新默认规则，设置西南交通大学犀浦校区位置
-- 犀浦校区坐标：纬度 30.7617，经度 103.9656
-- 允许范围：1000米（覆盖主要校园区域）
UPDATE check_rules 
SET allowed_latitude = 30.7617000, 
    allowed_longitude = 103.9656000, 
    allowed_radius = 1000 
WHERE is_default = 1;