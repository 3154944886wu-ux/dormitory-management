# Spec: Spring MVC 接口契约

## ADDED Requirements

### Requirement: 未认证与权限不足的 HTTP 状态可自动回归

系统 MUST 用 Spring MockMvc 锁定：缺失 token 返回 HTTP 401；已登录但角色不足返回 HTTP 403。

#### Scenario: 未登录访问需要认证的接口

- **WHEN** 不带 Authorization 访问 `GET /api/dashboard/overview` 或 `GET /uploads/any`
- **THEN** HTTP 状态为 401，响应 `code` 为 401

#### Scenario: 学生访问管理端房间列表

- **WHEN** 学生 JWT 访问 `GET /api/rooms` 或 `GET /api/beds/available/{roomId}`
- **THEN** HTTP 状态为 403

### Requirement: 学生只能查本人档案与本房间检查记录

#### Scenario: 查询他人学号

- **WHEN** 学生 A 访问 `GET /api/students/no/{学生B学号}`
- **THEN** HTTP 403

#### Scenario: 本房间检查记录

- **WHEN** 学生访问本人 `roomId` 的 `GET /api/inspection/records/room/{roomId}`
- **THEN** HTTP 200；访问其他房间为 HTTP 403

### Requirement: 宿管聚合接口按管理范围过滤

#### Scenario: 只管辖一栋楼的宿管

- **WHEN** 管理员与只绑定楼栋 1 的宿管分别访问工作台、待审批请假、水电费列表
- **THEN** 宿管看到的楼栋数、待审批数、水电条数均小于管理员全校数，且不含范围外数据

### Requirement: 入住人数使用实际在住 COUNT

#### Scenario: current_count 漂移

- **WHEN** 房间 `current_count` 为 0，但有 2 名 `status=1` 在住学生
- **THEN** `GET /api/students/no/{学号}` 与房间列表返回的 `occupancy` 为 2

### Requirement: 资料接口不改密码，匹配失败返回真实 HTTP 400

#### Scenario: PUT /users/me 带 password

- **WHEN** 已登录用户 `PUT /api/users/me` 并在 JSON 中携带 `password`
- **THEN** 数据库密码哈希不变，原密码仍可登录

#### Scenario: 空批次触发匹配

- **WHEN** 管理员对 running 且无学生的批次 `PUT /api/batches/{id}/trigger-matching`
- **THEN** HTTP 400，文案只有一层「匹配失败」，不以 HTTP 200 包错误
