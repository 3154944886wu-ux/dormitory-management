## 1. 演示账号默认关闭

- [x] 1.1 给 `TeacherDataInitializer` 加上 `@ConditionalOnProperty(value = "app.seed-demo.enabled", havingValue = "true")`，与 `DemoCheckInSeedRunner` 对齐
- [x] 1.2 新增 demo 关闭时（`havingValue=false`, `matchIfMissing=true`）的 sanitizer：对 `010001`/`010002` 若密码仍为工号则 `users.status=0`，并打日志；不删除行、不改已改密账号
- [x] 1.3 更新 `README.md`：默认账号表去掉教师行；教师口令只出现在演示数据一节，并写明必须显式开启 `app.seed-demo.enabled`

## 2. 统一范围守卫

- [x] 2.1 在 `ManagerScopeService` 增加 `matches(scopes, buildingId, className)` 与 `assertStudentInScope(role, userId, studentId)`：ADMIN 通过；MANAGER 用学生 `className` + `Room.buildingId` 做行内 AND、行间 OR；空范围或无法解析楼栋视为拒绝
- [x] 2.2 `GlobalExceptionHandler` 将 `AccessDeniedException` 映射为 HTTP 403（`code=403`）
- [x] 2.3 `CheckInController` / `CheckExceptionController` 的 `isManager` 改为读取 `SecurityContext` 角色，禁止再读 JWT `role` claim

## 3. 归寝接口强制范围

- [x] 3.1 MANAGER 的 `GET /api/checkin/date/{date}`、`GET /api/checkin/search` 改为 scoped 查询；无范围返回空列表
- [x] 3.2 MANAGER 的单日 `GET /api/checkin/statistics` 改为范围统计，不得调用全局 `getStatistics(date)`
- [x] 3.3 `GET /api/checkin/{id}` 对 MANAGER 调用 `assertStudentInScope`，范围外 403
- [x] 3.4 已有 `/records` `/trend` `/export` 确认走同一 `isManager` 与 scoped 路径，无全局回退

## 4. 异常接口强制范围

- [x] 4.1 `GET /api/check-exceptions/{id}`、`POST /api/check-exceptions/{id}/handle` 对 MANAGER 先断言范围，范围外 403 且不更新处理状态
- [x] 4.2 `GET /date/{date}`、`GET /handled/{handled}`、`GET /search` 对 MANAGER 走 `searchScoped`；`GET /student/{studentId}` 范围外 403
- [x] 4.3 `GET /statistics`、`GET /count` 对 MANAGER 使用范围过滤；无范围返回 0 / 空汇总
- [x] 4.4 已有列表/trend/export 确认走同一守卫源

## 5. Token 以数据库为准

- [x] 5.1 抽出可单测的用户状态判定（对齐 `JwtSecretRules`）：缺失或 `status != 1` 则不可认证；authority 角色取自数据库
- [x] 5.2 `JwtAuthFilter` 解析 Token 后查 `UserMapper.findByUsername`，按 5.1 建立或跳过 `SecurityContext`；忽略 Token 内 `role`

## 6. 契约测试

- [x] 6.1 `ManagerScopeServiceTest`：只楼栋、只班级、双约束、多条 OR、空范围拒绝；教师 A 的学生不能被教师 B 的范围匹配
- [x] 6.2 覆盖 `assertStudentInScope`：范围外抛 `AccessDeniedException`（读详情与处理共用此断言）
- [x] 6.3 会话单测：禁用用户不可认证；Token 角色为 ADMIN、数据库为 MANAGER 时 authority 为 MANAGER

## 7. Parallelization Plan

- [x] 7.1 共享接口由主 agent 先完成 2.1–2.3 与 5.1（`ManagerScopeService`、`AccessDeniedException`、`AuthUserState`）。完成前禁止并行改 Controller
- [x] 7.2 2.1 完成后可并行：切片 A 改 `CheckInController`（任务 3.x，只读/只改该文件及相关 Service 统计方法）；切片 B 改 `CheckExceptionController`（任务 4.x）；切片 C 写 6.x 测试（mock Mapper，不改生产 Controller）。冲突文件：`ManagerScopeService` 仅主 agent 可再改
- [x] 7.3 1.x 演示账号与 README 可与 5.2 Filter 并行（文件不重叠：Initializer/sanitizer vs `JwtAuthFilter`）
- [x] 7.4 主 agent 负责合并、确认没有 Controller 仍读取 JWT `role` 做范围分支，并勾选 tasks

## 8. Architecture Verification

- [x] 8.1 用仓库本地 JDK 入口跑 `backend` 测试：`ManagerScopeServiceTest` 与会话单测通过；现有 `JwtSecretRulesTest`、`AnnouncementAccessTest` 不回退
- [x] 8.2 静态核对：CheckIn/CheckException 中所有 `hasAnyRole('ADMIN', 'MANAGER')` 方法，MANAGER 分支均有范围过滤或 `assertStudentInScope`，且 `isManager` 不解析 JWT claim
- [x] 8.3 确认无新依赖、无新表；请假/学生/仪表盘/报修/访客/巡检未在本 change 中「假装已隔离」
- [x] 8.4 `mvn compile` 通过；前端本轮无代码改动，不要求重建
