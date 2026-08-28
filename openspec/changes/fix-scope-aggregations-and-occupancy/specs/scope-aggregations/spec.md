# Spec: scope-aggregations

## Requirements

### Requirement: 宿管工作台按范围汇总
MANAGER 调用 `GET /api/dashboard/overview`、`/accommodation`、`/repair`、`/utility`、`/dorm-stats` 时，MUST 只统计其管理范围内数据。无范围时 MUST 返回全 0。ADMIN 仍看全校。

房间空闲/部分入住/满员 MUST 按实际在住学生数判断，不得使用可能漂移的 `rooms.current_count`。

### Requirement: 请假统计按范围
MANAGER 调用 `GET /api/leave-requests/statistics` 时，`pendingCount`、`approvedCount` 与 `pending` 列表 MUST 与列表接口同一套范围规则。

### Requirement: 检查记录按楼栋范围
MANAGER 查询、创建、修改、审核、删除检查记录时，超出其楼栋范围 MUST 403 或空列表。仅班级、无楼栋的范围 MUST 不能看到全校检查记录。

### Requirement: 水电费按楼栋范围
MANAGER 列出或查看水电费时，超出其楼栋范围 MUST 403 或空列表。学生仍只能看本人房间。

### Requirement: 入住人数为实际在住
学生档案 `occupancy` MUST 等于该房间 `status=1` 的学生人数。房间列表应返回同样的实际在住人数（`occupancy` 字段），不得覆盖匹配使用的 `currentCount` 列。
