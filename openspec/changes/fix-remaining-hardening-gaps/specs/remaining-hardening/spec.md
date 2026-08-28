# Spec: remaining-hardening

## Requirements

### Requirement: 学生宿舍详情字段完整
学生档案查询 MUST 返回 `floor`、`roomType`、`occupancy`、`capacity`、`buildingId`。

### Requirement: 宿管范围语义一致
多条范围 MUST 按「单条 building AND class，范围之间 OR」判断。SQL 查询 MUST 使用同一语义，禁止把所有楼栋与所有班级做成笛卡尔积。

### Requirement: 宿管写操作受范围约束
MANAGER 审批/驳回请假、处理归寝异常、按日期/搜索打卡时，超出范围 MUST 403 或空数据。

### Requirement: 学生不可枚举全校房间
`GET /api/rooms`、`GET /api/rooms/building/{id}`、`GET /api/beds/available/{id}` MUST 仅 ADMIN/MANAGER。学生查本人房间走学生档案接口。

### Requirement: 改密必须验证原密码
`PUT /api/users/me` MUST 忽略 `password`。改密 MUST 走 `/api/auth/password` 且校验原密码、新密码至少 6 位。

### Requirement: 注册不枚举学号
学号不存在、姓名不匹配、已注册 MUST 返回同一业务文案。

### Requirement: 业务失败使用对应 HTTP 状态
JSON `code` 为 400/404 时，HTTP 状态 MUST 同步为 400/404，不得固定 200。
