# dormitory-manage-system · 后端

基于 Spring Boot 3 的宿舍管理系统后端 API 服务。

> **注意**：本目录仅包含 Spring Boot 后端，请使用 `mvn spring-boot:run` 启动。历史 Node/Express 代码已移除。

## 技术栈

- Java 17+
- Spring Boot 3.2+
- Spring Security + JWT
- MyBatis
- MySQL 8.0+
- Maven

## 项目结构

```
src/main/java/com/dormitory/
├── config/                    # 配置类
│   ├── SecurityConfig.java    # Spring Security 配置
│   └── JwtConfig.java         # JWT 配置
├── controller/                # 控制器层
│   ├── AnnouncementController.java
│   ├── AuthController.java
│   ├── BuildingController.java
│   ├── DashboardController.java
│   ├── RepairController.java
│   ├── RoomController.java
│   ├── StudentController.java
│   ├── UserController.java
│   ├── UtilityFeeController.java
│   └── VisitorController.java
├── mapper/                    # MyBatis Mapper 接口
│   ├── AnnouncementMapper.java
│   ├── BuildingMapper.java
│   ├── RepairMapper.java
│   ├── RoomMapper.java
│   ├── StudentMapper.java
│   ├── UserMapper.java
│   ├── UtilityFeeMapper.java
│   └── VisitorMapper.java
├── model/                     # 实体类
│   ├── Announcement.java
│   ├── Building.java
│   ├── Repair.java
│   ├── Room.java
│   ├── Student.java
│   ├── User.java
│   ├── UtilityFee.java
│   └── Visitor.java
├── service/                   # 服务层
│   ├── AnnouncementService.java
│   ├── BuildingService.java
│   ├── RepairService.java
│   ├── RoomService.java
│   ├── StudentService.java
│   ├── UserService.java
│   ├── UtilityFeeService.java
│   └── VisitorService.java
└── DormitoryApplication.java  # 启动类

src/main/resources/
├── application.yml            # 应用配置
└── schema.sql                 # 数据库初始化脚本
```

## API 模块

| 模块 | 前缀 | 说明 |
|------|------|------|
| 认证 | /api/auth | 登录、注册、Token 刷新 |
| 用户 | /api/users | 用户信息管理 |
| 楼栋 | /api/buildings | 楼栋 CRUD |
| 房间 | /api/rooms | 房间 CRUD、批量生成 |
| 学生 | /api/students | 学生管理、入住退宿 |
| 访客 | /api/visitors | 访客登记管理 |
| 报修 | /api/repairs | 报修管理 |
| 水电费 | /api/utility-fees | 水电费管理 |
| 公告 | /api/announcements | 公告管理 |
| 仪表盘 | /api/dashboard | 数据统计 |

## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE dormitory CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 配置数据库连接

复制 `src/main/resources/application-local.yml.example` 为 `application-local.yml`，填写数据库密码与 JWT secret。勿修改已提交的 `application.yml` 中的占位配置。

### 3. 运行项目

```bash
# 方式一：Maven
mvn spring-boot:run

# 方式二：打包运行
mvn clean package
java -jar target/dormitory-manage-system-1.0.0.jar
```

服务默认运行在 `http://localhost:8080`

## 默认数据

首次启动会自动创建表并插入默认数据：

- 管理员账号: admin / admin123
- 示例楼栋: 1号楼、2号楼、3号楼
- 示例公告: 欢迎使用宿舍管理系统

## 认证方式

使用 JWT Token 认证：

1. 登录获取 Token
```bash
POST /api/auth/login
{
  "username": "admin",
  "password": "admin123"
}
```

2. 请求携带 Token
```
Authorization: Bearer <token>
```

## License

MIT