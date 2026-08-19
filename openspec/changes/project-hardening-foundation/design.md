# Design: 项目基础加固

## 1. 后端授权模型

### 原则

- **默认拒绝**：除明确 `permitAll` 外，均需 JWT 认证。
- **方法级角色**：使用 `@PreAuthorize`，与现有 `CheckExceptionController` 等保持一致。
- **角色枚举**：`ADMIN`、`MANAGER`、`STUDENT`（JWT 中 `ROLE_` 前缀由 `JwtAuthFilter` 处理）。

### SecurityConfig 调整

```text
permitAll:
  - /api/auth/**
  - GET /api/announcements/published
  - OPTIONS /**
其余: authenticated()
```

移除 `GET /api/announcements` 的 `permitAll`（管理列表需登录）。

### Controller 授权矩阵

| Controller | 读 | 写 |
|------------|-----|-----|
| AnnouncementController | GET: ADMIN/MANAGER/STUDENT；published: 匿名 | ADMIN |
| DashboardController | ADMIN, MANAGER | — |
| InspectionPlan/Record | ADMIN, MANAGER | ADMIN, MANAGER |
| InspectionItemController | ADMIN, MANAGER | ADMIN |
| UploadController | — | ADMIN, MANAGER, STUDENT + 文件白名单 |
| BedController | ADMIN, MANAGER, STUDENT | — |
| StudentController list/getById | ADMIN, MANAGER | ADMIN（已有） |
| StudentController getByStudentNo | ADMIN, MANAGER, STUDENT | — |
| RepairController GET/POST | ADMIN, MANAGER, STUDENT | handle: ADMIN, MANAGER |
| UtilityFeeController GET | ADMIN, MANAGER, STUDENT | ADMIN（已有） |

### UploadController 加固

- 单文件上限 5MB
- 扩展名白名单：jpg, jpeg, png, gif, webp, pdf
- 拒绝空文件与无扩展名上传

## 2. 移除 Node 遗留后端

删除目录/文件：

- `backend/package.json`、`backend/package-lock.json`
- `backend/src/index.js`
- `backend/src/config/db.js`
- `backend/src/routes/**`
- `backend/src/models/**`
- `backend/src/middleware/**`

保留 `backend/node_modules/` 在 `.gitignore`（历史残留无害）。

在 `backend/README.md` 注明：**仅 Spring Boot，禁止 npm 启动**。

## 3. 数据库黄金路径

**推荐（开发/首次部署）**：

```bash
mysql -u root -p dormitory < database/schema.sql
mysql -u root -p dormitory < database/test_data.sql
mysql -u root -p dormitory < database/migration_teachers.sql
mysql -u root -p dormitory < database/migration_checkin_manager.sql
mysql -u root -p dormitory < database/migration_check_rules_fix.sql
mysql -u root -p dormitory < database/migration_add_location.sql
mysql -u root -p dormitory < database/migration_smart_dorm.sql
mysql -u root -p dormitory < database/migration_rename_teachers_to_managers.sql
mysql -u root -p dormitory < database/visitors.sql
```

`schema.sql` 新增 `inspection_items` 表及 10 条种子数据（与 `dormitory.sql` 对齐）。

另提供 `database/migration_inspection_items.sql` 供已有库增量升级。

`dormitory.sql` 保留为全量快照，README 标注「方式 A 可能缺 managers 表，不推荐」。

## 4. 配置外置

### application.yml（提交）

```yaml
spring.datasource.username: ${DB_USERNAME:root}
spring.datasource.password: ${DB_PASSWORD:}
jwt.secret: ${JWT_SECRET:change-me-in-local-config}
app.init-admin.enabled: ${INIT_ADMIN:false}
```

### application-local.yml（不提交）

开发者复制 `application-local.yml.example`，Spring Boot 自动加载同目录 `application-local.yml`。

### .gitignore

增加 `backend/src/main/resources/application-local.yml`

## 5. 前端对齐

- `student.js`：`getAnnouncements` → `GET /announcements/published`
- `utilityFee.js`：新增 `payUtilityFee(id)`
- `UtilityFeeManagement.vue`：移除调试 UI，`handlePay` 调 pay 接口

## 回滚

- 授权：还原 Controller 注解与 `SecurityConfig`
- Node：从 git 恢复删除文件
- 配置：恢复旧 `application.yml`（不推荐）

## 验证

```bash
cd backend && mvn compile
cd frontend && npm run build
```

手动：学生 token 调 `POST /api/announcements` 应 403；`GET /api/announcements/published` 无 token 应 200。
