## ADDED Requirements

### Requirement: 教师请假数据必须受管理范围约束

教师（`MANAGER`）请求请假列表、待审批、按状态查询、统计与详情时，系统 MUST 只返回其有效 `manager_scope` 内学生的单据。批准与拒绝 MUST 仅允许范围内学生的请假；范围外 MUST 返回 403 且不得更新审批状态。无有效范围时列表 MUST 为空、统计 MUST 为 0，且 MUST NOT 回退为全校数据。管理员不受此约束。学生查看本人请假详情的既有规则不变。

#### Scenario: 教师不能审批范围外请假

- **WHEN** 教师 A 对属于教师 B 范围内学生的请假调用 `POST /api/leave-requests/{id}/approve` 或 `POST /api/leave-requests/{id}/reject`
- **THEN** 响应状态为 403 且该请假状态不变

#### Scenario: 教师列表不含范围外请假

- **WHEN** 教师 A 请求 `GET /api/leave-requests` 或 `GET /api/leave-requests/pending`
- **THEN** 返回的每条记录所属学生均匹配教师 A 的管理范围

#### Scenario: 教师读取范围外请假详情

- **WHEN** 教师 A 请求属于教师 B 范围内学生的 `GET /api/leave-requests/{id}`
- **THEN** 响应状态为 403

### Requirement: 教师学生档案必须受管理范围约束

教师请求学生列表、按 id 详情、按学号查询时，系统 MUST 只允许其管理范围内的学生。范围外详情或学号查询 MUST 返回 403。无有效范围时列表 MUST 为空。按姓名或房间搜索 MUST 不得返回范围外学生。管理员不受此约束。学生只能查本人学号的既有规则不变。

#### Scenario: 教师读取范围外学生详情

- **WHEN** 教师 A 请求教师 B 范围内学生的 `GET /api/students/{id}`
- **THEN** 响应状态为 403

#### Scenario: 教师按学号查询范围外学生

- **WHEN** 教师 A 请求 `GET /api/students/no/{studentNo}` 且该学生不在其范围内
- **THEN** 响应状态为 403

#### Scenario: 教师学生列表不能越权

- **WHEN** 教师 A 请求 `GET /api/students` 且不带过滤或仅带姓名
- **THEN** 返回的每条学生记录均匹配教师 A 的管理范围
