const express = require('express');
const { body, validationResult } = require('express-validator');
const Room = require('../models/Room');
const Building = require('../models/Building');
const { auth } = require('../middleware/auth');

const router = express.Router();

// 管理员权限检查中间件
const adminOnly = (req, res, next) => {
  if (req.user.role !== 'ADMIN') {
    return res.status(403).json({ 
      code: 403, 
      message: '权限不足，需要管理员权限' 
    });
  }
  next();
};

// 获取房间列表
router.get('/', auth, async (req, res) => {
  try {
    const { buildingId, status, floor } = req.query;
    const filters = {};
    
    if (buildingId) filters.buildingId = parseInt(buildingId);
    if (status) filters.status = status;
    if (floor) filters.floor = parseInt(floor);

    const rooms = await Room.findAll(filters);
    res.json({ code: 200, data: rooms });
  } catch (error) {
    console.error('获取房间列表错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 获取房间统计
router.get('/stats', auth, async (req, res) => {
  try {
    const { buildingId } = req.query;
    const stats = await Room.getStats(buildingId || null);
    res.json({ 
      code: 200, 
      data: {
        total: stats.total || 0,
        empty: stats.empty || 0,
        partial: stats.partial || 0,
        full: stats.full || 0,
        totalBeds: stats.total_beds || 0,
        occupiedBeds: stats.occupied_beds || 0,
        availableBeds: (stats.total_beds || 0) - (stats.occupied_beds || 0)
      }
    });
  } catch (error) {
    console.error('获取房间统计错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 获取单个房间
router.get('/:id', auth, async (req, res) => {
  try {
    const room = await Room.findById(req.params.id);
    if (!room) {
      return res.status(404).json({ 
        code: 404, 
        message: '房间不存在' 
      });
    }
    res.json({ code: 200, data: room });
  } catch (error) {
    console.error('获取房间信息错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 创建房间
router.post('/', auth, adminOnly, [
  body('buildingId').isInt({ min: 1 }).withMessage('楼栋ID不正确'),
  body('roomNumber').trim().notEmpty().withMessage('房间号不能为空'),
  body('floor').isInt({ min: 1 }).withMessage('楼层不正确'),
  body('bedCount').isInt({ min: 1, max: 12 }).withMessage('床位数量必须在1-12之间'),
], async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ 
      code: 400, 
      message: errors.array()[0].msg 
    });
  }

  try {
    const { buildingId, roomNumber } = req.body;

    // 检查楼栋是否存在
    const building = await Building.findById(buildingId);
    if (!building) {
      return res.status(400).json({ 
        code: 400, 
        message: '楼栋不存在' 
      });
    }

    // 检查房间号是否已存在
    if (await Room.existsByNumber(buildingId, roomNumber)) {
      return res.status(400).json({ 
        code: 400, 
        message: '该楼栋中房间号已存在' 
      });
    }

    const id = await Room.create(req.body);
    const room = await Room.findById(id);

    res.status(201).json({ 
      code: 201, 
      message: '创建成功', 
      data: room 
    });
  } catch (error) {
    console.error('创建房间错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 批量创建房间（为楼栋初始化）
router.post('/batch', auth, adminOnly, [
  body('buildingId').isInt({ min: 1 }).withMessage('楼栋ID不正确'),
  body('floors').isInt({ min: 1, max: 50 }).withMessage('楼层数必须在1-50之间'),
  body('roomsPerFloor').isInt({ min: 1, max: 100 }).withMessage('每层房间数必须在1-100之间'),
  body('bedCount').optional().isInt({ min: 1, max: 12 }).withMessage('床位数量必须在1-12之间'),
], async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ 
      code: 400, 
      message: errors.array()[0].msg 
    });
  }

  try {
    const { buildingId, floors, roomsPerFloor, bedCount = 4 } = req.body;

    // 检查楼栋是否存在
    const building = await Building.findById(buildingId);
    if (!building) {
      return res.status(400).json({ 
        code: 400, 
        message: '楼栋不存在' 
      });
    }

    const count = await Room.batchCreate(buildingId, floors, roomsPerFloor, bedCount);
    
    res.status(201).json({ 
      code: 201, 
      message: `成功创建 ${count} 个房间`,
      data: { count }
    });
  } catch (error) {
    console.error('批量创建房间错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 更新房间
router.put('/:id', auth, adminOnly, [
  body('roomNumber').trim().notEmpty().withMessage('房间号不能为空'),
  body('bedCount').isInt({ min: 1, max: 12 }).withMessage('床位数量必须在1-12之间'),
  body('status').optional().isIn(['EMPTY', 'PARTIAL', 'FULL']).withMessage('状态值不正确'),
], async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ 
      code: 400, 
      message: errors.array()[0].msg 
    });
  }

  try {
    const { id } = req.params;
    const { roomNumber, buildingId } = req.body;

    // 检查房间是否存在
    const existing = await Room.findById(id);
    if (!existing) {
      return res.status(404).json({ 
        code: 404, 
        message: '房间不存在' 
      });
    }

    // 检查房间号是否与其他房间重复
    if (await Room.existsByNumber(existing.buildingId, roomNumber, parseInt(id))) {
      return res.status(400).json({ 
        code: 400, 
        message: '该楼栋中房间号已存在' 
      });
    }

    await Room.update(id, req.body);
    const room = await Room.findById(id);

    res.json({ 
      code: 200, 
      message: '更新成功', 
      data: room 
    });
  } catch (error) {
    console.error('更新房间错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 更新状态
router.put('/:id/status', auth, adminOnly, [
  body('status').isIn(['EMPTY', 'PARTIAL', 'FULL']).withMessage('状态值不正确'),
], async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ 
      code: 400, 
      message: errors.array()[0].msg 
    });
  }

  try {
    const { id } = req.params;
    const { status } = req.body;

    const existing = await Room.findById(id);
    if (!existing) {
      return res.status(404).json({ 
        code: 404, 
        message: '房间不存在' 
      });
    }

    await Room.updateStatus(id, status);
    res.json({ 
      code: 200, 
      message: '状态更新成功' 
    });
  } catch (error) {
    console.error('更新房间状态错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 删除房间
router.delete('/:id', auth, adminOnly, async (req, res) => {
  try {
    const { id } = req.params;

    const existing = await Room.findById(id);
    if (!existing) {
      return res.status(404).json({ 
        code: 404, 
        message: '房间不存在' 
      });
    }

    await Room.delete(id);
    res.json({ 
      code: 200, 
      message: '删除成功' 
    });
  } catch (error) {
    console.error('删除房间错误:', error);
    if (error.message.includes('学生入住')) {
      return res.status(400).json({ 
        code: 400, 
        message: error.message 
      });
    }
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

module.exports = router;