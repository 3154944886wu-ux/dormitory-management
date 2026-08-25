# Spec: teacher-data-scope

教师管理范围作为归寝打卡与异常处理的强制后端数据域边界。

## Requirements

### Requirement: 教师归寝数据必须受管理范围约束

教师（`MANAGER`）请求归寝打卡的列表、搜索、按日期查询、统计、趋势、导出与单条详情时，系统 MUST 只返回其有效 `manager_scope` 内的数据。无有效范围时列表/统计/导出 MUST 为空，且 MUST NOT 回退为全校数据。范围外的单条详情 MUST 返回 403。管理员（`ADMIN`）不受此约束。

范围匹配 MUST 遵循：一条范围记录中空 `building_id` 表示不限楼栋、空 `class_name` 表示不限班级；两者都有则必须同时满足。多条有效范围之间为 OR。

#### Scenario: 教师按日期查询不能看到范围外记录

- **WHEN** 教师 A 的范围仅为楼栋 1，请求 `GET /api/checkin/date/{date}`
- **THEN** 响应中不含楼栋 1 以外学生的打卡记录

#### Scenario: 教师读取范围外打卡详情

- **WHEN** 教师 A 请求属于教师 B 范围内学生的 `GET /api/checkin/{id}`
- **THEN** 响应状态为 403

#### Scenario: 无范围教师查询归寝列表

- **WHEN** 没有任何有效 `manager_scope` 的教师请求归寝列表或统计
- **THEN** 返回空数据且总计数为 0

### Requirement: 教师异常数据必须受管理范围约束

教师请求异常记录的列表、搜索、按日期/学生/处理状态查询、统计、计数、趋势、导出、详情与处理时，系统 MUST 只允许其管理范围内的数据。范围外详情或处理 MUST 返回 403。无有效范围时查询 MUST 为空。管理员不受此约束。

#### Scenario: 教师不能处理范围外异常

- **WHEN** 教师 A 对属于教师 B 范围内学生的异常调用 `POST /api/check-exceptions/{id}/handle`
- **THEN** 响应状态为 403 且该异常仍为未处理

#### Scenario: 教师按学生查询范围外学号

- **WHEN** 教师 A 请求 `GET /api/check-exceptions/student/{studentId}` 且该学生不在其范围内
- **THEN** 响应状态为 403

#### Scenario: 教师搜索异常不能越权

- **WHEN** 教师 A 请求 `GET /api/check-exceptions/search` 不带楼栋参数
- **THEN** 返回的每条记录均匹配教师 A 的管理范围
