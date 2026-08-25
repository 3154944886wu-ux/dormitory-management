## ADDED Requirements

### Requirement: 示例教师账号仅在演示开关下创建

系统 MUST 仅在 `app.seed-demo.enabled=true` 时自动确保示例教师 `010001`、`010002` 存在。该开关缺省或为 false 时，MUST NOT 创建这两个账号。若二者已存在且密码仍为对应工号，系统 MUST 将其用户状态设为禁用。

#### Scenario: 默认启动不创建示例教师

- **WHEN** 未配置 `app.seed-demo.enabled` 或值为 false 时应用启动
- **THEN** 系统不插入工号为 `010001` 或 `010002` 的新教师账号

#### Scenario: 关闭演示后禁用默认口令示例教师

- **WHEN** 库中已有 `010001` 且密码仍为 `010001`，以演示开关关闭状态启动
- **THEN** 该用户 `status` 为禁用，使用原密码登录失败

### Requirement: 认证必须使用数据库中的用户状态和角色

系统在接受 JWT 后 MUST 加载对应用户。用户不存在或 `status` 不为启用时 MUST 视为未认证。授予的角色 MUST 来自数据库 `role` 字段，MUST NOT 仅信任 Token 内的角色声明。

#### Scenario: 禁用账号的旧 Token 立即失效

- **WHEN** 用户在 Token 有效期内被禁用后携带该 Token 访问需认证接口
- **THEN** 响应状态为 401

#### Scenario: 降权后按新角色授权

- **WHEN** 用户 Token 中角色为 `ADMIN` 但数据库角色已改为 `MANAGER`
- **THEN** 请求按 `MANAGER` 授权，且归寝/异常数据受其 `manager_scope` 约束
