# Spec: auth-session

登录会话与当前用户资料保护。

## Requirements

### Requirement: 匿名仅登录与注册

系统 MUST 仅允许未认证访问 `POST /api/auth/login` 与 `POST /api/auth/register`。`GET /api/auth/me`、`PUT /api/auth/profile`、`PUT /api/auth/password` MUST 要求已认证。

#### Scenario: 未登录读取当前用户

- **WHEN** 未携带 JWT 请求 `GET /api/auth/me`
- **THEN** 响应状态为 401

#### Scenario: 登录后读取当前用户不含密码

- **WHEN** 已登录用户请求 `GET /api/auth/me`
- **THEN** 响应 200 且 `data.password` 为 null 或不存在

### Requirement: 拒绝默认 JWT 密钥

系统 MUST 在启动时校验 `jwt.secret`：禁止空值、禁止占位符 `change-me-in-local-config`，且长度 MUST 不少于 32。

#### Scenario: 使用占位符启动

- **WHEN** `jwt.secret` 为默认占位符
- **THEN** 应用启动失败并提示配置本地密钥
