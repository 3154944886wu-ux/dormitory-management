# Design: 教师范围扩展到请假与学生查询

## Context

归寝/异常已强制 `manager_scope`。请假与学生查询仍把 `MANAGER` 当全校角色：`LeaveRequestController` 的列表/待审批/状态/详情/统计/批准/拒绝无范围；`StudentController` 的列表/`/{id}`/`/no/{studentNo}` 对教师同样全局。学生本人学号与本人请假详情已隔离。约束：复用现有守卫，不新表、不新依赖；本轮不做仪表盘/报修/访客/巡检。

## Goals / Non-Goals

**Goals:**

- 教师只能列出、查看、审批其范围内学生的请假
- 教师只能列出、查看其范围内学生档案（含按学号）
- 无范围：查询为空、统计为 0；详情/审批 403
- 契约测试锁住教师 A 不能读/批教师 B 的学生与假条

**Non-Goals:**

- 仪表盘、报修、访客、巡检
- 学生写操作（已仅 ADMIN）
- 改 `assertStudentInScope` 匹配规则或列表 CSV OR SQL
- 前端改动、Token 黑名单

## Architecture Assessment

### Existing Design Reuse

- `ManagerScopeService.assertStudentInScope` / `hasScope` / CSV 辅助方法
- CheckIn/CheckException：`SecurityContext` 判断 `ROLE_MANAGER`；`AccessDeniedException` → 403；approve 路径像 `CheckExceptionService.handle` 一样在 Service 再断言
- Mapper JOIN：请假已联学生/房间/楼栋；学生列表已联房间/楼栋
- scoped SQL 模式：`FIND_IN_SET(building_id)` AND `FIND_IN_SET(class_name)`（与归寝一致，混合 scope 列表偏严）
- 测试：`ManagerScopeServiceTest` 风格，JUnit 5 + Mockito

### Boundaries and Ownership

| 职责 | 归属 |
|------|------|
| 匹配/断言 | 现有 `ManagerScopeService`，本轮不改算法 |
| 请假列表 SQL | `LeaveRequestMapper` 新增 scoped 查询 |
| 学生列表 SQL | `StudentMapper` 新增 scoped 分页/计数 |
| 详情/审批 | Controller 先断言；`LeaveRequestService.approve` 对 MANAGER 再断言 |
| 当前用户 id | 请假继续 JWT `id`；学生列表用 `Authentication.getName()` + `UserMapper.findByUsername`（该 Controller 无 JwtUtils） |
| 学生 token | 保持 `student-self-service`：本人学号、本人请假详情 |

### Options and Rationale

1. **接入现有守卫 + scoped SQL（选中）** vs 内存过滤全表。后者分页 total 不准且易漏。
2. **不抽新 Guard 组件**：两个 Controller 复制 CheckIn 的 `isManager()` 小方法。抽公共基类过重。
3. **Service 层审批再断言**：防止以后新入口绕过 Controller。

### Quality Attributes

- 安全：处理类越权（审批）与档案 IDOR 同时堵住
- 性能：SQL 过滤，不扫全表再筛
- 可测试性：断言已有测试；本轮补「假条 studentId / 学生 id 越权」用例

### Complexity and Exceptions

不新增抽象、依赖、表。仅扩展 Mapper 查询与 Controller/Service 分支。

## Decisions

### D1. 请假接口矩阵

MANAGER：

- `GET /`、`GET /pending`、`GET /status/{status}` → scoped 列表；无范围空列表
- `GET /statistics` → 范围内计数；无范围全 0
- `GET /{id}` → 非学生时若为 MANAGER 则 `assertStudentInScope`（学生分支保持本人校验）
- `POST /{id}/approve`、`POST /{id}/reject` → 先 load，再断言，再 `approve`；`AccessDeniedException` 不得被 catch 成 400

Admin 保持全局。响应 JSON 形状不变。

### D2. 学生接口矩阵

MANAGER：

- `GET /api/students`（含 name/roomId 搜索）必须 AND 范围，禁止靠搜索漏出范围外学生
- `GET /{id}`、`GET /no/{studentNo}`：学生 token 规则不变；MANAGER 在查到记录后断言，范围外 403（记录不存在仍 404）

写接口仍仅 ADMIN。

### D3. scoped SQL

请假：`searchScoped(status, buildingIdsCsv, classNamesCsv, offset, limit)` + `countScoped`。  
学生：`findScopedPaged(name, roomId, csv..., offset, size)` + `countScoped`。  
CSV 语义与归寝相同，不在本轮改为行间 OR。

### D4. 测试

不引入 MockMvc。扩展或新增：

- 教师 A 对教师 B 学生的 `assertStudentInScope` 已覆盖；本轮用同一断言证明请假 `studentId` 与学生 id/学号路径会抛 `AccessDeniedException`
- 可选：scoped mapper SQL 不在无 DB 单测中执行；用 Service 在 mock mapper 上验证 MANAGER 走 scoped 方法、无范围不调用全局 `findAll`

## Risks / Trade-offs

- [混合 scope 列表偏严] → 与归寝一致，不扩大越权
- [教师端请假/学生列表变短] → 预期；Admin 不受影响
- [StudentController 用用户名反查 userId] → 与 JWT filter 同源用户名；禁用账号已无法建立认证
- [仪表盘等仍全局] → 明确下一 change

## Migration Plan

无 DDL。部署后教师立即只能看范围内请假与学生。回滚对应 Controller/Service/Mapper。无需数据迁移。

## Open Questions

无。
