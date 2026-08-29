## 1. 请假 scoped 查询

- [ ] 1.1 `LeaveRequestMapper` 增加 `searchScoped` / `countScoped`（可选 status，CSV 楼栋/班级，`FIND_IN_SET` 与归寝一致）
- [ ] 1.2 `LeaveRequestService` 封装 scoped 列表、计数、统计；`approve` 在 handler 为 MANAGER 时先 `assertStudentInScope`

## 2. 请假接口强制范围

- [ ] 2.1 `LeaveRequestController` 的 `isManager` 改读 `SecurityContext`；`approve`/`reject` 的 `catch (RuntimeException)` 必须重新抛出 `AccessDeniedException`
- [ ] 2.2 MANAGER 的 `GET /`、`GET /pending`、`GET /status/{status}` 走 scoped 查询；无范围返回空列表
- [ ] 2.3 MANAGER 的 `GET /{id}` 断言范围（学生本人校验保持不变）；`GET /statistics` 用范围内计数
- [ ] 2.4 MANAGER 的 `POST /{id}/approve` 与 `POST /{id}/reject` 先 load 再断言，范围外 403 且不改状态

## 3. 学生 scoped 查询

- [ ] 3.1 `StudentMapper` 增加 `findScopedPaged` / `countScoped`（name、roomId 与 CSV 范围 AND）
- [ ] 3.2 `StudentService` 封装 scoped 列表；`StudentController` 注入 `ManagerScopeService` 与 `UserMapper`（用 `Authentication.getName()` 取 userId）

## 4. 学生接口强制范围

- [ ] 4.1 MANAGER 的 `GET /api/students`（含姓名/房间搜索）走 scoped 分页；无范围空列表
- [ ] 4.2 MANAGER 的 `GET /{id}`、`GET /no/{studentNo}` 在记录存在后 `assertStudentInScope`；范围外 403，不存在仍 404。学生本人学号规则不变

## 5. 契约测试

- [ ] 5.1 请假：mock 假条 `studentId` 属于教师 B 时，教师 A 调用断言/approve 路径抛 `AccessDeniedException`
- [ ] 5.2 学生：教师 A 对教师 B 的学生 id/学号断言抛 `AccessDeniedException`；Admin 仍可绕过
- [ ] 5.3 现有 `ManagerScopeServiceTest`、`AuthUserStateTest`、JWT/公告测试不回退

## 6. Parallelization Plan

- [ ] 6.1 主 agent 先完成 1.1 与 3.1（Mapper SQL 模式对齐）。`ManagerScopeService` 本轮不改
- [ ] 6.2 其后可并行：切片 A 请假 Service+Controller（任务 1.2、2.x）；切片 B 学生 Service+Controller（任务 3.2、4.x）；切片 C 测试 5.x（只新增测试文件）。冲突文件：无共享生产文件
- [ ] 6.3 主 agent 合并后静态核对：请假/学生所有 `hasAnyRole('ADMIN','MANAGER')` 读接口均有范围或断言；`isManager` 不读 JWT `role` claim

## 7. Architecture Verification

- [ ] 7.1 用仓库本地 JDK 跑 `backend` 测试通过
- [ ] 7.2 确认无新依赖、无新表；仪表盘/报修/访客/巡检未在本 change 中声称已隔离
- [ ] 7.3 `mvn compile` 通过；前端本轮无代码改动
