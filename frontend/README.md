# dormitory-manage-system · 前端

基于 Vue 3 + Element Plus 的宿舍管理系统前端项目。

## 技术栈

- Vue 3 (Composition API)
- Vue Router 4
- Element Plus
- Axios
- ECharts
- Vite

## 功能模块

| 模块 | 页面 | 说明 |
|------|------|------|
| 认证 | Login.vue, Register.vue | 登录注册 |
| 首页 | Dashboard.vue | 数据统计仪表盘 |
| 楼栋 | BuildingManagement.vue | 楼栋增删改查 |
| 房间 | RoomManagement.vue | 房间管理、批量生成 |
| 学生 | StudentManagement.vue | 学生入住退宿 |
| 访客 | VisitorManagement.vue | 访客登记管理 |
| 报修 | RepairManagement.vue | 报修处理跟踪 |
| 水电费 | UtilityFeeManagement.vue | 水电费录入缴费 |
| 公告 | AnnouncementManagement.vue | 公告发布管理 |

## 项目结构

```
src/
├── api/                  # API 接口封装
│   ├── announcement.js    # 公告接口
│   ├── auth.js          # 认证接口
│   ├── building.js      # 楼栋接口
│   ├── dashboard.js     # 仪表盘接口
│   ├── index.js         # Axios 实例
│   ├── repair.js        # 报修接口
│   ├── room.js          # 房间接口
│   ├── student.js       # 学生接口
│   └── utilityFee.js    # 水电费接口
├── router/              # 路由配置
│   └── index.js
├── utils/               # 工具函数
│   └── api.js           # API 请求工具
├── views/               # 页面组件
│   ├── AnnouncementManagement.vue
│   ├── BuildingManagement.vue
│   ├── Dashboard.vue
│   ├── Home.vue
│   ├── Login.vue
│   ├── Profile.vue
│   ├── Register.vue
│   ├── RepairManagement.vue
│   ├── RoomManagement.vue
│   ├── StudentManagement.vue
│   ├── UtilityFeeManagement.vue
│   └── VisitorManagement.vue
├── App.vue
└── main.js
```

## 开发

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

## 配置

修改 `src/api/index.js` 配置后端 API 地址：

```javascript
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})
```

## 默认账号

- 用户名: admin
- 密码: admin123

## License

MIT