# Design: 修复学生端契约与越权

## 1. 认证范围

`SecurityConfig` 匿名白名单改为：

```text
permitAll:
  - POST /api/auth/login
  - POST /api/auth/register
  - GET /api/announcements/published
  - OPTIONS /**
其余: authenticated()
```

`AuthController` `/me`、`/profile` 返回前 `user.setPassword(null)`；用户不存在返回 401/404，避免 NPE。

## 2. 角色判定与当前学生

不新增公共服务类。各 Controller 用 `Authentication`：

- `ROLE_STUDENT`：`StudentMapper.findByStudentNo(auth.getName())`；找不到则 400「未关联学生信息」
- 非学生：保持原管理查询语义

## 3. 隔离矩阵

| 接口 | 学生 | 管理端 |
|------|------|--------|
| `GET /repairs` | 强制 `findByStudentId(本人)`，忽略客户端 studentId/roomId | 支持 studentId / roomId / roomNumber / status |
| `GET /repairs/{id}` | 仅 `studentId` 匹配 | 任意 |
| `POST /repairs` | 强制写入本人 studentId、roomId；无房间则 400 | roomId 必填，studentId 可空 |
| `GET /utility-fees` | 强制本房间；无房间返回空列表 | 原逻辑 |
| `GET /utility-fees/{id}` | 仅 `roomId` 匹配 | 任意 |
| `POST .../pay` | 仍仅 ADMIN | 不变 |
| `GET /students/no/{no}` | `no` 必须等于 username | 任意 |
| `GET /visitors*` | 403 | ADMIN/MANAGER |
| `GET /leave-requests/{id}` | 仅本人 | ADMIN/MANAGER |

`RepairService.create`：`studentId` 为空时跳过学生存在校验（表字段可空）。

## 4. 报修类型与搜索

类型统一为字符串：`水电维修` / `门窗维修` / `家具维修` / `网络问题` / `其他`。前端 option 与展示用同一套文案；展示兼容历史数字 1–5。

`RepairMapper` 增加 `room_number LIKE` 查询；管理端搜索传 `roomNumber`。

## 5. 前端

- `StudentFees.vue`：列 `year-month`、电费、水费、合计、状态、缴费时间；分页在客户端切已隔离后的列表
- `StudentRepairs.vue`：去掉无后端字段的 `location`；状态 0/1/2/3；类型用字符串；时间用 `completeTime`
- `RepairManagement.vue`：类型字符串；`getRepairTypeName` 回退显示原字符串

## 6. 风险与回滚

- 学生端若仍用错误学号查档案会 403 → 现有 `getProfile` 已用 `user.username`
- 曾依赖「学生拉全量再前端筛」的调用会变少 → 本仓库学生端正是要修这一点
- 回滚：还原上述 Controller/Config/三个 Vue 文件

## 验证

```bash
cd backend && mvn compile -q
```

手工：学生 token 调 `GET /api/repairs` 仅本人；未登录 `GET /api/auth/me` 为 401；`/me` JSON 无 password。
