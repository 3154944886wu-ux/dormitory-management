# Spec: dormitory.sql 便利快照

## ADDED Requirements

### Requirement: dump 与黄金路径 schema 对齐且不含环境数据

`database/dormitory.sql` MUST 是当前黄金 schema 的 schema-only 便利快照，不得作为第二套真相源。

#### Scenario: 结构完整

- **WHEN** 阅读 `dormitory.sql`
- **THEN** 含 `managers`、`manager_scope`、`rectify_remark`、`uk_student_date_type`，不含 `students.payment_status` 与 `users.student_id`

#### Scenario: 无环境 INSERT

- **WHEN** 在 dump 中搜索 `INSERT INTO`
- **THEN** 不存在业务行（检查项种子仍只属于 `schema.sql`）

#### Scenario: 文档指向黄金路径

- **WHEN** 阅读 `MIGRATIONS.md`
- **THEN** 首次部署仍推荐 `schema.sql` + `test_data.sql` + 迁移；`dormitory.sql` 标明仅便利快照
