# Spec: auth-session

## ADDED Requirements

### Requirement: 匿名仅登录与注册

系统 MUST 仅允许未认证访问 `POST /api/auth/login` 与 `POST /api/auth/register`。`GET /api/auth/me`、`PUT /api/auth/profile`、`PUT /api/auth/password` MUST 要求已认证。

#### Scenario: 未登录读取当前用户

- **WHEN** 未携带 JWT 请求 `GET /api/auth/me`
- **THEN** 响应状态为 401

#### Scenario: 登录后读取当前用户不含密码

- **WHEN** 已登录用户请求 `GET /api/auth/me`
- **THEN** 响应 200 且 `data.password` 为 null 或不存在
