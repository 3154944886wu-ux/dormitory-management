# 数据库迁移与初始化指南

本文档是**唯一推荐的**手工初始化路径。项目未使用 Flyway/Liquibase，请按顺序执行，并在团队内记录已应用的脚本。

## 首次部署（推荐）

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS dormitory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

mysql -u root -p dormitory < database/schema.sql
mysql -u root -p dormitory < database/test_data.sql
mysql -u root -p dormitory < database/migration_teachers.sql
mysql -u root -p dormitory < database/migration_checkin_manager.sql
mysql -u root -p dormitory < database/migration_check_rules_fix.sql
mysql -u root -p dormitory < database/migration_add_location.sql
mysql -u root -p dormitory < database/migration_smart_dorm.sql
mysql -u root -p dormitory < database/migration_rename_teachers_to_managers.sql
mysql -u root -p dormitory < database/visitors.sql
mysql -u root -p dormitory < database/migration_rectify_remark.sql
mysql -u root -p dormitory < database/migration_remaining_integrity.sql
```

`schema.sql` 已包含 `inspection_items` 表及种子数据；若从更旧版本升级，可单独执行 `migration_inspection_items.sql`。

## 脚本说明

| 文件 | 用途 |
|------|------|
| `schema.sql` | 基础表结构 + 检查项种子数据（黄金路径起点） |
| `test_data.sql` | 测试楼栋/房间/学生 |
| `migration_teachers.sql` | 教师/管理人员相关 |
| `migration_checkin_manager.sql` | 归寝与管理人员范围 |
| `migration_check_rules_fix.sql` | 打卡规则修复 |
| `migration_add_location.sql` | 定位打卡字段 |
| `migration_smart_dorm.sql` | 智能选宿模块 |
| `migration_rename_teachers_to_managers.sql` | teachers → managers 重命名 |
| `visitors.sql` | 访客模块补充 |
| `migration_rectify_remark.sql` | 检查整改说明独立列 |
| `migration_remaining_integrity.sql` | 楼栋 name 唯一、异常 (学生,日期,类型) 唯一 |
| `migration_inspection_items.sql` | 仅缺检查项表时执行 |

## 不推荐

- **仅导入 `dormitory.sql`**：这是历史 dump，可能缺少后续迁移表，且含有应用不读取的 `payment_status` 等列。黄金路径仍是 `schema.sql` + 下方 migration，不要单独导入 dump。
- **仅导入 `schema.sql` 不加 migration**：缺少智能选宿、归寝扩展等表。

## 演示数据

见 `seed_demo_checkin.md`，通过 `application-local.yml` 中 `app.seed-demo.enabled=true` 启用（执行一次后关闭）。

## 后端配置

数据库连接请写在 `backend/src/main/resources/application-local.yml`（参考 `application-local.yml.example`），勿将密码提交到 Git。
