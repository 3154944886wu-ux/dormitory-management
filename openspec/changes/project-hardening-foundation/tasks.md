# Tasks: project-hardening-foundation

## 1. OpenSpec 与文档

- [x] 创建 `openspec/changes/project-hardening-foundation/`（proposal, design, tasks）
- [x] 新增 `database/MIGRATIONS.md`
- [x] 更新 `README.md` 数据库章节指向黄金路径
- [x] 更新 `backend/README.md` 移除 Node 误导

## 2. 后端授权

- [x] 收紧 `SecurityConfig` 匿名路径
- [x] `AnnouncementController` 补 `@PreAuthorize`
- [x] `DashboardController` 补 `@PreAuthorize`
- [x] `InspectionPlanController` / `InspectionRecordController` 补 `@PreAuthorize`
- [x] `InspectionItemController` 补 `@PreAuthorize`
- [x] `UploadController` 补 `@PreAuthorize` + 文件校验
- [x] `BedController` 补 `@PreAuthorize`
- [x] `StudentController` GET 补 `@PreAuthorize`
- [x] `RepairController` GET/POST 补 `@PreAuthorize`；handle 允许 MANAGER
- [x] `UtilityFeeController` GET 补 `@PreAuthorize`

## 3. 删除 Node 遗留

- [x] 删除 `backend/package.json` 及 Node 源码目录
- [x] 删除 `backend/package-lock.json`（若存在）

## 4. 数据库

- [x] `schema.sql` 增加 `inspection_items` + 种子数据
- [x] 新增 `database/migration_inspection_items.sql`

## 5. 配置安全

- [x] `application.yml` 环境变量占位
- [x] `application-local.yml.example`
- [x] `.gitignore` 忽略 `application-local.yml`
- [x] `init-admin` 默认 false

## 6. 前端收尾

- [x] `student.js` 公告改 `/published`
- [x] `utilityFee.js` 增加 `payUtilityFee`
- [x] `UtilityFeeManagement.vue` 清理调试 UI + 对齐 pay API

## 7. 验证

- [x] `mvn compile`
- [ ] `npm run build`（可选，本地 Node 环境）

## 并发策略

| 工作流 | 执行方 | 说明 |
|--------|--------|------|
| Controller 授权 | 主 agent | 同模块多文件，需统一矩阵 |
| Node 删除 + DB 脚本 | 主 agent | 无冲突 |
| 前端三文件 | 可与授权并行 | 独立目录 |
| `mvn compile` | 收尾 | 依赖全部 Java 改动 |
