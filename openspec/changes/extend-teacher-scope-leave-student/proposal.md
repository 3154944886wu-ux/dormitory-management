# Proposal: 将教师范围扩展到请假与学生查询（extend-teacher-scope-leave-student）

## Why

`enforce-teacher-data-scope` 已把归寝/异常做成强制数据域，但请假审批和学生档案仍只检查 `MANAGER` 角色。教师可列出全校请假、审批范围外学生的假条，以及按 id/学号读取任意学生档案。这与「教师只能处理其管理范围内数据」不一致，也是同一条越权链的下一跳。

现在就做：守卫 `assertStudentInScope` 已存在且有单测，本轮只需接到请假与学生读写边界，不必再设计授权模型。

## What Changes

- **BREAKING（MANAGER 请假）**：列表、按状态、待审批、详情、统计、批准、拒绝 MUST 受 `manager_scope` 约束。范围外详情/审批返回 403；无有效范围时查询为空，统计为 0。
- **BREAKING（MANAGER 学生）**：列表、按 id、按学号 MUST 受 `manager_scope` 约束。范围外详情/学号查询 403；无范围时列表为空。学生本人查自己学号的现有规则不变。
- Admin 与学生自助接口语义不变。学生写操作（创建/调宿/退宿/删除）仍仅 ADMIN。
- 补充契约测试：教师 A 不能读取或审批教师 B 范围内学生的请假/档案。
- 不改仪表盘、报修、访客、巡检。

## Capabilities

### New Capabilities

- （无）

### Modified Capabilities

- `teacher-data-scope`: 将强制数据域从归寝/异常扩展到请假审批与学生查询

## Architecture Impact

复用 `ManagerScopeService.assertStudentInScope` / `hasScope` / `buildingIdsCsv` / `classNamesCsv`，以及 CheckIn/CheckException 的 `SecurityContext` `isManager` 与 `AccessDeniedException` → 403。列表过滤对齐现有 scoped SQL（`FIND_IN_SET` CSV AND）。`LeaveRequestMapper` 已 JOIN 学生/房间/楼栋；`StudentMapper` 已 JOIN 房间/楼栋。不新建表、不引入依赖、不改 JWT。测试沿用 JUnit 5 + Mockito，无 Spring 上下文。

相关 specs：`teacher-data-scope`（扩展）；`student-self-service` 的学生本人请假/学号规则不改；`auth-session` 不改。

## Impact

- 后端：`LeaveRequestController`/`LeaveRequestService`/`LeaveRequestMapper`，`StudentController`/`StudentService`/`StudentMapper`
- API：MANAGER 请假与学生查询变窄；Admin 与学生 token 行为不变
- 测试：范围外请假审批/学生详情 403；范围内列表不含越权记录
- 前端：本轮不改；教师端列表可能变少，属预期
