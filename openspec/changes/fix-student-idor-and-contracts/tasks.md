# Tasks: fix-student-idor-and-contracts

## 1. 认证收紧

- [x] `SecurityConfig` 匿名仅 `POST /api/auth/login`、`POST /api/auth/register`、`GET /api/announcements/published`、`OPTIONS`
- [x] `AuthController` `/me`、`/profile` 清除 password；用户缺失时 401/404 不 NPE

## 2. 后端数据隔离

- [x] `RepairController` 学生强制本人列表/详情；创建绑定本人；无房间 400
- [x] `RepairService.create` 允许 `studentId` 为空；`RepairMapper` 增加 `roomNumber` 模糊查询
- [x] `UtilityFeeController` 学生仅本房间列表/详情
- [x] `StudentController.getByStudentNo` 学生仅本人学号
- [x] `VisitorController` GET 补 `@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")`
- [x] `LeaveRequestController.getById` 学生仅本人单据

## 3. 前端契约

- [x] `StudentFees.vue` 对齐 UtilityFee 字段与客户端分页
- [x] `StudentRepairs.vue` 类型字符串、状态文案、去掉 location
- [x] `RepairManagement.vue` 类型字符串 + `roomNumber` 搜索

## 4. 验证

- [x] `mvn compile`（`backend/mvnw.cmd -q compile`，exit 0）

## 并发策略

| 工作流 | 执行方 | 说明 |
|--------|--------|------|
| 认证 + 隔离 Controller | 主 agent | 同安全边界，顺序改 |
| 前端三文件 | 可与后端并行 | 不共享文件 |
| `mvn compile` | 收尾 | 依赖全部 Java 改动 |

主 agent 负责最终集成与勾选。
