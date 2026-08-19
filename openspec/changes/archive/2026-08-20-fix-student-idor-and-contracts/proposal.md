# Proposal: 修复学生端契约与越权（fix-student-idor-and-contracts）

## Why

学生端水电费、报修页面与后端字段/状态约定不一致，管理端按房间号搜索报修无效。同时学生 token 可列出全校报修、水电费、访客、任意学号档案和任意请假单；`/api/auth/**` 整段匿名导致 `/me` 回传密码哈希、未登录改密会 500。这些是用户可感知的功能缺陷与 IDOR，应在当前加固基础上立刻修掉。

## What Changes

- **BREAKING（学生 token）**：`GET /api/repairs`、`GET /api/utility-fees` 仅返回本人/本房间数据；`GET /{id}` 非所属资源返回 403
- **BREAKING（学生 token）**：`GET /api/students/no/{studentNo}` 仅允许查询本人学号
- **BREAKING**：`GET /api/visitors*` 仅 ADMIN/MANAGER
- **BREAKING（学生 token）**：`GET /api/leave-requests/{id}` 仅本人单据
- **BREAKING**：匿名仅允许 `POST /api/auth/login` 与 `POST /api/auth/register`；`/me`、`/profile`、`/password` 需登录且不返回 `password`
- 学生端水电费表对齐 `UtilityFee` 字段；报修类型改为字符串、状态文案与后端一致
- 管理端报修支持按 `roomNumber` 模糊查询；管理员代报修允许 `studentId` 为空

## Capabilities

### New Capabilities

- `auth-session`: 认证入口收紧与当前用户资料不泄露密码
- `student-self-service`: 学生端报修/水电费/档案/请假的数据隔离与前端契约

### Modified Capabilities

- （无。`openspec/specs/` 尚无已发布 spec）

## Architecture Impact

复用现有 JWT `Authentication`、`StudentMapper.findByStudentNo`（学号=用户名）与各 Controller 的 `@PreAuthorize`。不新增表、不引入新依赖。隔离逻辑放在 Controller 边界（与请假 `cancel` 已有的本人校验一致），不抽跨模块基础设施。前端只改学生端两页与管理端报修搜索/类型展示。

## Impact

- 后端：`SecurityConfig`、`AuthController`、`RepairController`/`RepairService`/`RepairMapper`、`UtilityFeeController`、`StudentController`、`VisitorController`、`LeaveRequestController`
- 前端：`StudentFees.vue`、`StudentRepairs.vue`、`RepairManagement.vue`
- API：学生列表接口语义变窄；前端登录后 `/auth/me` 行为不变（本来就带 token）
- 测试：仓库无现成测试框架；以 `mvn compile` + 关键路径手工说明为准
