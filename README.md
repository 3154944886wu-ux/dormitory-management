# dormitory-manage-system

面向高校的宿舍综合管理平台，覆盖学生归寝打卡、请假审批、异常处理、宿务日常管理等功能。系统采用前后端分离架构，支持**管理员**、**教师（辅导员/宿管）**、**学生**三类角色。

## 功能概览

### 管理员

| 模块 | 说明 |
|------|------|
| 工作台 | 入住、报修、水电、检查等数据概览 |
| 宿舍资源 | 楼栋、房间、学生信息管理 |
| 归寝管理 | 打卡规则、打卡记录、异常记录、归寝统计分析 |
| 请假审批 | 学生请假申请审核 |
| 教师管理 | 工号账号、楼栋/班级管理范围绑定 |
| 日常事务 | 报修、水电费、访客登记、公告 |
| 安全卫生检查 | 检查计划、记录、统计分析 |
| 智能选宿 | 批次管理、问卷、分配结果与统计 |
| 审计日志 | 关键操作留痕 |

### 教师（宿管/导员等）

- 按绑定的**楼栋**和/或**班级**查看归寝记录
- 处理晚归、未归、缺卡等异常（处理说明选填）
- 归寝统计分析（含未归未处理/已处理拆分、图表展示）

### 学生

- 归寝打卡（支持定位）
- 请假申请与记录查询
- 我的宿舍、报修、水电费、公告
- 首页展示本月打卡概况

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3、Vue Router、Element Plus、ECharts、Vite |
| 后端 | Spring Boot 3.2、Spring Security、JWT、MyBatis |
| 数据库 | MySQL 8.x |
| 运行环境 | JDK 21、Node.js 18+ |

## 项目结构

```
dormitory-manage-system/
├── backend/                 # Spring Boot 后端
│   └── src/main/
│       ├── java/com/dormitory/
│       └── resources/
│           └── application.yml
├── frontend/                # Vue 3 前端
│   └── src/
│       ├── api/             # 接口封装
│       ├── views/           # 页面组件
│       └── router/          # 路由与权限
└── database/                # SQL 脚本与说明
    ├── schema.sql           # 基础表结构
    ├── test_data.sql        # 测试学生/楼栋/房间
    ├── migration_*.sql      # 增量迁移脚本
    └── seed_demo_checkin.md # 演示数据说明
```

## 快速开始

### 1. 环境准备

- 安装 **JDK 21**、**Maven 3.8+**、**Node.js 18+**、**MySQL 8**
- 创建数据库：

```sql
CREATE DATABASE dormitory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 初始化数据库

按需选择一种方式：

**方式 A：完整导入（推荐首次部署）**

```bash
mysql -u root -p dormitory < database/dormitory.sql
```

**方式 B：分步导入（适合开发调试）**

```bash
mysql -u root -p dormitory < database/schema.sql
mysql -u root -p dormitory < database/test_data.sql
mysql -u root -p dormitory < database/migration_teachers.sql
mysql -u root -p dormitory < database/migration_checkin_manager.sql
mysql -u root -p dormitory < database/migration_check_rules_fix.sql
```

> 若从旧版本升级，请按 `migration_*.sql` 文件名顺序依次执行尚未应用过的脚本。

### 3. 配置后端

编辑 `backend/src/main/resources/application.yml`，修改数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dormitory?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: your_password
```

首次无管理员账号时，可临时启用：

```yaml
app:
  init-admin:
    enabled: true
```

启动成功后会创建 `admin / admin123`，**随后请改回 `false`**。

### 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

默认端口：`http://localhost:8080`

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

默认地址：`http://localhost:5173`（通过 Vite 代理访问 `/api` → `8080`）

## 默认账号

| 角色 | 账号 | 密码 | 说明 |
|------|------|------|------|
| 管理员 | `admin` | `admin123` | 首次启动可配合 `app.init-admin.enabled=true` 创建 |
| 教师 | `010001` | `010001` | 工号即用户名，初始密码同 工号 |
| 学生 | 学号 | 注册时设置 | 如 `20230001`，也可在登录页注册 |

学生注册时需填写与 `students` 表中一致的学号信息。

## 演示数据（可选）

用于演示归寝打卡、请假、异常处理等完整流程，详见 [`database/seed_demo_checkin.md`](database/seed_demo_checkin.md)。

在 `application.yml` 中启用后重启后端：

```yaml
app:
  seed-demo:
    enabled: true
```

将生成：

- 18 名教师及楼栋/班级绑定
- 2026-06-20 ~ 06-22 的打卡、请假、异常样例

**执行一次后请关闭该开关**，避免重复清空演示日期数据。

## 归寝规则说明

默认规则逻辑（可在「打卡规则」中配置）：

| 时段 | 状态 |
|------|------|
| 归寝截止时间前打卡 | 已归 (0) |
| 截止时间后、未归截止时间前 | 晚归 (1) |
| 超过未归截止时间仍未打卡 | 未归 (2) |
| 已批准请假期间 | 请假 (3) |

系统自动任务会按规则周期检测并生成未归记录。

## 教师绑定规则

- 管理范围保存在 `manager_scope` 表
- 可仅绑楼栋、仅绑班级，或两者同时绑定
- `building_id` 为空表示不限楼栋；`class_name` 为空表示不限班级
- 教师登录后只能查看和处理其范围内的归寝数据

## 常用命令

```bash
# 后端编译
cd backend && mvn compile

# 前端构建
cd frontend && npm run build

# 前端预览构建结果
cd frontend && npm run preview
```

## 接口说明

- 基础路径：`/api`
- 认证方式：`Authorization: Bearer <token>`
- 登录：`POST /api/auth/login`
- 前端开发环境通过 `vite.config.js` 代理到 `http://localhost:8080`

## 注意事项

1. `application.yml` 中的数据库密码、JWT 密钥请勿提交到公开仓库
2. 修改后端代码后需**重启 Spring Boot** 才能生效
3. 统计分析依赖较新的归寝趋势接口；若卡片为 0，请确认后端已更新并重启，或将日期范围选至有数据的区间
4. 数据库结构详见 `database/schema.sql` 与 `数据库结构说明.txt`

## 许可证

本项目仅供学习与研究使用。
