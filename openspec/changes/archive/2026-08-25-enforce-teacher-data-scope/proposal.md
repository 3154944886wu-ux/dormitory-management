# Proposal: 强制教师数据域边界（enforce-teacher-data-scope）

## Why

当前系统完成了角色授权（`@PreAuthorize("hasRole('MANAGER')")`），但没有把 `manager_scope` 做成强制后端安全边界。部分归寝列表/趋势接口已按范围过滤，页面演示看起来正常；详情、按日期查询、搜索、单日统计、异常处理等仍执行全局查询。叠加启动时无条件创建 `010001`/`010002`（初始密码=工号、角色 MANAGER，且写在 README），形成可利用越权链。JWT 有效期 7 天，过滤器只信 Token 内角色，账号禁用或降权后旧 Token 仍可按原权限使用。

此前加固覆盖了角色注解与学生 IDOR，没有系统覆盖教师数据域；现有 4 个后端测试也不覆盖这条链。需要立刻把「教师只能查看和处理其范围内归寝数据」从 README 承诺变成可测试的后端契约。

## What Changes

- **BREAKING（演示账号）**：`TeacherDataInitializer` 默认关闭，仅当 `app.seed-demo.enabled=true` 时创建示例教师；开关关闭且 `010001`/`010002` 仍为工号密码时禁用这两个账号。README 教师口令只出现在演示数据说明。
- **BREAKING（MANAGER 归寝/异常）**：教师请求归寝与异常的列表、详情、统计、导出、处理时，MUST 受 `manager_scope` 约束。范围外详情/处理返回 403；无有效范围时查询返回空，不得回退为全校数据。
- **BREAKING（会话）**：每次请求以数据库中的用户状态和角色为准。禁用账号立即 401；降权立即按新角色生效，不再信任 Token 内旧 `role`。
- 新增最小权限契约测试：教师 A 不能读取或处理教师 B 范围内的归寝/异常数据；禁用/降权 Token 立即失效。
- 不改请假、学生列表、仪表盘、报修、访客、巡检的数据域（已知同类缺口，另立 change）。

## Capabilities

### New Capabilities

- `teacher-data-scope`: 教师管理范围作为归寝打卡与异常处理的强制后端数据域边界（列表、详情、统计、导出、处理）

### Modified Capabilities

- `auth-session`: 演示教师账号仅在显式 demo 开关下创建；JWT 认证必须校验数据库用户状态，并以数据库角色授予权限

## Architecture Impact

复用现有 `ManagerScope` / `ManagerScopeService`、已有 `searchScoped*` 查询，以及 `DemoCheckInSeedRunner` 的 `app.seed-demo.enabled` 开关。在 `ManagerScopeService` 内扩展匹配与断言，不新建表、不引入新依赖、不抽 AOP/查询拦截器。Token 在现有 `JwtAuthFilter` 中查 `UserMapper`，不引入黑名单或 Refresh Token。测试沿用 JUnit 5 纯单测（无 Spring 上下文），不新建测试基础设施。

相关现有 specs：`auth-session`（认证入口与 JWT 密钥）；`student-self-service` 不改。数据模型：`manager_scope`、`users.status`、`users.role` 保持不变。

## Impact

- 后端：`TeacherDataInitializer`、`TeacherService`、`ManagerScopeService`、`JwtAuthFilter`、`CheckInController`/`CheckInService`、`CheckExceptionController`/`CheckExceptionService`、相关 Mapper 查询字段（详情需能解析楼栋/班级）
- 文档：`README.md`、必要时 `database/seed_demo_checkin.md`
- API：MANAGER 调用未过滤的归寝/异常接口语义变窄；Admin 行为不变
- 测试：新增范围匹配、教师 A/B 越权、禁用/降权 Token 单测
- 已知残留：请假审批、学生查询、仪表盘、报修、访客、巡检仍仅角色授权
