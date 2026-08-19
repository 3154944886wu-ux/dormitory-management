# Spec: student-self-service

学生端自助查询与提交时的数据隔离及页面契约。

## Requirements

### Requirement: 学生只能访问本人报修

学生角色请求报修列表时，系统 MUST 只返回 `studentId` 等于当前学生的记录，并 MUST 忽略客户端传入的 `studentId`/`roomId` 过滤。查询或操作他人报修 MUST 返回 403。学生提交报修 MUST 使用当前学生的 `studentId` 与 `roomId`。

#### Scenario: 学生拉取报修列表

- **WHEN** 学生 token 请求 `GET /api/repairs`
- **THEN** 返回的每条记录 `studentId` 均为该学生

#### Scenario: 学生查看他人报修详情

- **WHEN** 学生请求不属于自己的 `GET /api/repairs/{id}`
- **THEN** 响应 403

### Requirement: 学生只能访问本房间水电费

学生角色请求费用列表时，系统 MUST 只返回其 `roomId` 对应记录；无房间时返回空列表。他人房间费用详情 MUST 403。缴费接口仍仅管理员可调用。

#### Scenario: 学生拉取水电费

- **WHEN** 已分配房间的学生请求 `GET /api/utility-fees`
- **THEN** 所有记录的 `roomId` 等于该学生房间

### Requirement: 学生只能查询本人档案

学生请求 `GET /api/students/no/{studentNo}` 时，`studentNo` MUST 等于当前用户名（学号），否则 403。

#### Scenario: 学生查询其他学号

- **WHEN** 学生 A 请求 `GET /api/students/no/{学生B学号}`
- **THEN** 响应 403

### Requirement: 访客列表不对普通学生开放

`GET /api/visitors` 及其详情/统计 MUST 仅允许 ADMIN 与 MANAGER。

#### Scenario: 学生拉访客列表

- **WHEN** 学生 token 请求 `GET /api/visitors`
- **THEN** 响应 403

### Requirement: 学生只能查看本人请假详情

学生请求 `GET /api/leave-requests/{id}` 时，单据 `studentId` MUST 为本人，否则 403。

#### Scenario: 学生查看他人请假单

- **WHEN** 学生请求他人的请假 id
- **THEN** 响应 403

### Requirement: 学生端页面与后端字段一致

水电费页 MUST 展示月份、电费、水费、合计与缴费状态。报修页 MUST 使用字符串类型与状态 0待处理/1处理中/2已完成/3已关闭。管理端按房间号搜索 MUST 调用后端 `roomNumber` 参数。

#### Scenario: 学生打开水电费页

- **WHEN** 后端返回含 `electricityFee`/`waterFee`/`totalFee`/`year`/`month` 的记录
- **THEN** 表格对应列展示这些金额与 `YYYY-MM` 月份，而非不存在的 `type`/`amount`

### Requirement: 学生不可查看未发布公告

学生请求公告列表或详情时，系统 MUST 只暴露 `status = 1` 的已发布公告。草稿与已下线 MUST 对学生表现为不存在。

#### Scenario: 学生按 id 读取草稿

- **WHEN** 学生请求未发布公告的 `GET /api/announcements/{id}`
- **THEN** 响应 404
