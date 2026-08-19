# Proposal: 项目基础加固（project-hardening-foundation）

## 目标

修复审计发现的结构性风险：后端授权缺口、双后端遗留、数据库初始化不一致、敏感配置入库，并清理前端调试残留。

## 背景（当前问题）

1. **安全错觉**：前端按角色隐藏菜单，但大量 API 仅需登录即可调用管理操作。
2. **幽灵后端**：`backend/` 内 Spring Boot 与 Node/Express 并存，易误启动错误服务。
3. **数据库脚本分裂**：`dormitory.sql` 与 `schema.sql` 缺表互补，README 迁移清单不完整。
4. **密钥入库**：`application.yml` 含明文密码、固定 JWT secret、`init-admin` 常开。
5. **调试残留**：水电费页面含 `[v5]` 与测试按钮；缴费 API 未对齐。

## Scope

- 为缺失 `@PreAuthorize` 的 Controller 补全角色校验
- 收紧 `SecurityConfig` 匿名访问范围
- 删除 Node 遗留代码与 `backend/package.json`
- 在 `schema.sql` 补全 `inspection_items`，新增 `MIGRATIONS.md` 黄金路径
- 敏感配置改为环境变量 / `application-local.yml`
- 清理 `UtilityFeeManagement.vue` 调试 UI，对齐 `pay` API
- 学生端公告改调 `/announcements/published`

## Out of Scope

- 引入 Flyway/Liquibase（仅文档化手工迁移顺序）
- 学生端报修/水电费按房间/学号的服务层数据隔离（后续 change）
- CI/CD、Docker、自动化测试体系
- Git 历史中的密钥清理（需人工 `git filter-repo`）

## 期望行为

| 场景 | 期望 |
|------|------|
| 学生 token 调公告 POST | 403 |
| 未登录访问已发布公告 | 200（仅 `GET /published`） |
| `npm start` in backend | 无 Node 入口，仅 Maven 启动 |
| 新环境按 MIGRATIONS.md 导入 | 表结构完整，检查项可用 |
| 克隆仓库启动 | 需本地 `application-local.yml`，无明文密钥 |

## 验收标准

- [ ] 无 `@PreAuthorize` 的管理类 Controller 已补全或说明例外
- [ ] `SecurityConfig` 仅 `auth/**` 与 `GET /announcements/published` 匿名
- [ ] Node 遗留文件已删除，`backend/package.json` 不存在
- [ ] `schema.sql` 包含 `inspection_items` 及种子数据
- [ ] `database/MIGRATIONS.md` 描述唯一推荐初始化路径
- [ ] `application.yml` 无明文密码，`init-admin` 默认 false
- [ ] 水电费页面无调试元素，缴费调用 `POST /utility-fees/{id}/pay`
- [ ] `mvn compile` 通过

## 风险

- 收紧权限后学生端若仍调旧公告接口会 403 → 已改 `student.js` 使用 `published`
- 本地开发需额外创建 `application-local.yml` → 提供 example 文件

## 关联

- change-id: `project-hardening-foundation`
