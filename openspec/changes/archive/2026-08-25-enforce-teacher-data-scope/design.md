# Design: 强制教师数据域边界

## Context

角色注解已覆盖管理接口，但 `MANAGER` 在部分归寝/异常接口上被当成全校角色。`manager_scope` 只在少数列表/趋势/导出路径生效。`TeacherDataInitializer` 无条件创建公开示例教师；`JwtAuthFilter` 只解析 Token，不核对 `users.status` / `users.role`。约束：不新增表与依赖；本轮只修归寝/异常 + 演示账号 + 会话即时失效。

## Goals / Non-Goals

**Goals:**

- 演示教师账号默认不出现；已有工号密码账号在 demo 关闭时被禁用
- 教师对归寝/异常的读、写、统计、导出都受 `manager_scope` 约束
- 禁用立即 401；降权立即按数据库角色生效
- 最小单测锁住教师 A/B 越权与会话失效

**Non-Goals:**

- 请假、学生、仪表盘、报修、访客、巡检的数据域
- Token 黑名单、Refresh Token、缩短 7 天 TTL
- 自动删除教师行或重置已改过的密码
- AOP / 查询拦截器 / 新测试框架

## Architecture Assessment

### Existing Design Reuse

- 配置开关：`app.seed-demo.enabled`（`DemoCheckInSeedRunner`）与 `app.init-admin.enabled`（`DataInitializer`）
- 范围数据：`ManagerScope`、`ManagerScopeService.hasScope` / `buildingIdsCsv` / `classNamesCsv`
- 范围查询：`CheckInMapper.searchScopedPaged`、`CheckExceptionMapper.searchScoped`、已有 trend/export 路径
- 学生楼栋：`StudentMapper.findById` + `RoomMapper.findById`（`Student` 无 `buildingId`，房间有）
- 会话：`JwtAuthFilter` + `UserMapper.findByUsername`；测试模式对齐 `JwtSecretRulesTest`（JUnit 5、无 Spring 上下文）
- 403 语义：与学生 IDOR（`RepairController` / `StudentController`）一致
- specs：扩展 `auth-session`；新增 `teacher-data-scope`。不改 `student-self-service`

### Boundaries and Ownership

| 职责 | 归属 |
|------|------|
| 范围匹配与断言 | `ManagerScopeService`（唯一入口） |
| 列表/统计 SQL 过滤 | 现有 scoped Mapper；Controller 禁止 MANAGER 走全局查询 |
| 详情/处理 | Service 调 `assertStudentInScope` 后再读/写 |
| 演示账号创建 | `TeacherDataInitializer`，仅 demo 开 |
| 演示账号禁用 | 独立 `CommandLineRunner`，demo 关（含缺省） |
| 会话角色与启用状态 | `JwtAuthFilter` 查库；Controller 的 `isManager` 读 `SecurityContext`，不读 Token claim |
| 失败语义 | 范围外 403；无范围查询空列表；禁用/缺失用户不建立认证 → 401 |

不引入新存储。每次请求一次 `users` 查询；范围断言按需读 `manager_scope` + 学生/房间。

### Options and Rationale

1. **范围落地**：统一 `ManagerScopeService` 匹配/断言（选中），而不是 Controller 逐个补洞或 AOP。补洞仍会漏详情/处理；AOP 过重。
2. **Token**：请求时查库并用数据库角色（选中），而不是黑名单表或缩短 TTL。直接覆盖禁用/降权，无新表。
3. **演示账号**：与现有 seed-demo 开关对齐，默认关闭；仅当密码仍为工号时禁用 `010001`/`010002`（选中）。不删除行，避免误伤已改密账号。

### Quality Attributes

- **安全**：数据域在服务边界强制；会话不以 Token 内角色为准
- **性能**：多一次用户查询，本系统可接受；范围匹配走已有 CSV/FIND_IN_SET 列表查询
- **可测试性**：匹配规则与会话状态纯函数/带 mock 的 Service 单测
- **可维护性**：禁止 Controller 再复制一套 `isManager + hasScope` 判定源

### Complexity and Exceptions

不新增抽象层、依赖、表或协议。仅在现有 Service/Filter 上扩展方法。列表查询暂不重写为「多条 scope 行间 OR」的动态 SQL（见 Risks）。

## Decisions

### D1. 匹配规则（与 README 一致）

一条 `manager_scope`：`building_id` 空=不限楼栋，`class_name` 空=不限班级；两者都有则必须同时满足。多条记录：**行内 AND、行间 OR**。无有效范围 → 查询空、详情/处理 403。

`assertStudentInScope(role, userId, studentId)`：ADMIN 直接通过；MANAGER 取学生 `className` 与房间 `buildingId` 做 `matches`。学生不存在或无房间导致无法匹配楼栋时，视为不在范围内（403）。

### D2. 接口落地矩阵（仅归寝/异常）

MANAGER 必须走范围路径的接口：

- 归寝：`GET /date/{date}`、`GET /search`、`GET /statistics`（含单日）、`GET /{id}`；已有 `/records` `/trend` `/export` 改为同一守卫（`isManager` 改读 SecurityContext）
- 异常：`GET /{id}`、`GET /date/{date}`、`GET /student/{studentId}`、`GET /handled/{handled}`、`GET /statistics`、`GET /count`、`POST /{id}/handle`；已有列表/search/trend/export 同样改守卫源

Admin 保持全局查询。响应 JSON 形状不变，仅数据变窄。

范围外详情/处理：`ResponseEntity.status(403)`。可在 `GlobalExceptionHandler` 增加 `AccessDeniedException` → 403，供 Service 抛出。

### D3. 列表 SQL 暂沿用 CSV AND

现有 `searchScoped*` 把所有楼栋 CSV 与所有班级 CSV 做 AND。对「只绑楼栋 / 只绑班级 / 单条双约束」正确；对「一条只绑楼栋 + 一条只绑班级」会过窄。本轮不改 SQL，详情/处理用 D1 的行间 OR。混合绑定教师的列表可能偏严，不会偏松（不扩大越权）。

### D4. 演示账号

- `TeacherDataInitializer`：`@ConditionalOnProperty(value = "app.seed-demo.enabled", havingValue = "true")`
- 新增 sanitizer：`havingValue = "false", matchIfMissing = true`；对 `010001`/`010002` 若存在且 `PasswordEncoder.matches(工号, password)` 则 `status=0`
- README：默认账号表去掉教师行；口令只写在演示数据一节，并注明需显式开开关
- 不自动重新启用已禁用账号；演示环境可在教师管理中启用，或开 seed-demo 后由已有 `ensureTeacher` 跳过已存在行（保持禁用，避免悄悄复活公开口令）

### D5. 会话即时生效

`JwtAuthFilter`：解析 Token 得到 username → `UserMapper.findByUsername`。用户缺失或 `status != 1` 则不写入 `SecurityContext`。`GrantedAuthority` 使用数据库 `role`，忽略 Token 内 `role`。Controller 用 `Authentication` 判断 MANAGER。

### D6. 测试

不引入 `@SpringBootTest`。新增：

- `ManagerScopeServiceTest`：只楼栋 / 只班级 / 双约束 / 多条 OR / 空范围拒绝 / 教师 A 不能匹配教师 B 的学生
- `AuthUserStateTest`（或等价小类，对齐 `JwtSecretRules`）：禁用、缺失、角色以降权后的数据库角色为准

契约「不能处理」通过 `assertStudentInScope` 抛错覆盖，不发真实 HTTP。

## Risks / Trade-offs

- [混合 scope 列表偏严] → 接受；详情/处理语义正确；若现场有此类教师再改 SQL
- [无房间学生 403] → 教师本就无法用楼栋约束定位；避免把无楼栋记录当成全局可见
- [每请求查 users] → 可接受；不做缓存以免禁用延迟
- [sanitizer 只处理默认密码] → 已改密的 `010001` 会留下；文档要求运营自查
- [请假等模块仍可越权] → 明确下一 change；本轮测试不假装已覆盖
- [isManager 若仍读 JWT] → 降权后可能按 Admin 路径拿全局数据；任务中强制改为 SecurityContext

## Migration Plan

1. 部署后默认不再创建示例教师
2. 已有库：启动一次（demo 关）会禁用仍为工号密码的 `010001`/`010002`
3. 本地演示：`app.seed-demo.enabled=true` 后重启
4. 已登录教师：禁用/降权无需等 Token 过期
5. 回滚：还原 Initializer/Filter/Controller/Service 与 README；被禁用的示例账号需手工 `status=1`

无需 DDL。

## Open Questions

无。范围已确认为最短路径；混合 scope 列表 SQL 明确推迟。
