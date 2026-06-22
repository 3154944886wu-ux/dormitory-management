const express = require('express');
const cors = require('cors');
const { initDatabase } = require('./config/db');
const authRoutes = require('./routes/auth');
const buildingRoutes = require('./routes/buildings');
const roomRoutes = require('./routes/rooms');
const studentRoutes = require('./routes/students');

const app = express();
const PORT = process.env.PORT || 3000;

// 中间件
app.use(cors());
app.use(express.json());

// 路由
app.use('/api/auth', authRoutes);
app.use('/api/buildings', buildingRoutes);
app.use('/api/rooms', roomRoutes);
app.use('/api/students', studentRoutes);

// 根路由
app.get('/', (req, res) => {
  res.json({ 
    message: '学生宿舍管理系统 API', 
    version: '1.0.0',
    endpoints: {
      auth: {
        register: 'POST /api/auth/register',
        login: 'POST /api/auth/login',
        me: 'GET /api/auth/me',
        profile: 'PUT /api/auth/profile'
      },
      buildings: {
        list: 'GET /api/buildings',
        detail: 'GET /api/buildings/:id',
        create: 'POST /api/buildings',
        update: 'PUT /api/buildings/:id',
        delete: 'DELETE /api/buildings/:id',
        status: 'PUT /api/buildings/:id/status'
      }
    }
  });
});

// 启动服务器
async function start() {
  try {
    await initDatabase();
    app.listen(PORT, () => {
      console.log(`🚀 服务器运行在 http://localhost:${PORT}`);
    });
  } catch (error) {
    console.error('启动失败:', error);
    process.exit(1);
  }
}

start();