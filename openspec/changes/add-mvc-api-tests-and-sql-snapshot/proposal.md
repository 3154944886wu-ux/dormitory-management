# Proposal: 补 Spring MVC 接口级自动化，并重写 dormitory.sql 快照

## Why

上一轮把范围聚合、实际在住、401/403 等契约只落在纯逻辑单测和手工 curl 上，并明确跳过了两件事：

- 不补 Spring MVC 接口级自动化
- 不重写 `database/dormitory.sql` 历史 dump

这两项会让回归再次漏掉「HTTP 状态 + Security + SQL 一起生效」的路径，也会让有人误导入过期 dump（缺 `managers` / `manager_scope`，多出无用的 `payment_status`）。

## What Changes

- 增加 `@SpringBootTest` + `MockMvc` 契约测试，覆盖未登录 401、角色 403、学生不可枚举房间、宿管工作台/请假/水电按范围、入住人数用实际 COUNT、改密忽略 `/users/me` 的 password、空批次触发匹配返回 HTTP 400
- 将 `database/dormitory.sql` 从 2026-06 Navicat 带数据 dump，改写成与当前 `schema.sql` 一致的 **schema-only 便利快照**（无环境数据、无废弃列）
- 更新 `MIGRATIONS.md`：dump 不再是第二套真相源，黄金路径仍是 `schema.sql` + 测试数据

## Out of Scope

- 不引入 Testcontainers / H2（本仓库 SQL 依赖 MySQL 方言）
- 不覆盖全部控制器，只锁上一轮已验收的关键契约
- 不删除未挂路由的死页面
- 不改选宿匹配算法

## 验收标准

- `mvn test` 含新增接口契约测试且通过
- 学生 token 访问 `GET /api/rooms` 为 HTTP 403
- 未登录访问 `/uploads/**` 为 HTTP 401
- 只管辖 1 栋楼的宿管，工作台 `buildingCount` 小于管理员
- 学生档案 `occupancy` 等于同房间 `status=1` 学生数，即使 `rooms.current_count` 为 0
- `dormitory.sql` 无 `INSERT` 业务数据，无 `payment_status`，含 `managers` 与 `manager_scope`
