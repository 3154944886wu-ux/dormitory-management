const express = require('express');
const { body, validationResult } = require('express-validator');
const Student = require('../models/Student');
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

// 获取学生列表
router.get('/', auth, async (req, res) => {
  try {
    const { buildingId, roomId, status, gender, keyword } = req.query;
    const filters = {};
    
    if (buildingId) filters.buildingId = parseInt(buildingId);
    if (roomId) filters.roomId = parseInt(roomId);
    if (status) filters.status = status;
    if (gender) filters.gender = gender;
    if (keyword) filters.keyword = keyword;

    const students = await Student.findAll(filters);
    res.json({ code: 200, data: students });
  } catch (error) {
    console.error('获取学生列表错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 获取学生统计
router.get('/stats', auth, async (req, res) => {
  try {
    const { buildingId } = req.query;
    const stats = await Student.getStats(buildingId || null);
    res.json({ 
      code: 200, 
      data: {
        total: stats.total || 0,
        active: stats.active || 0,
        checkedOut: stats.checked_out || 0,
        male: stats.male || 0,
        female: stats.female || 0
      }
    });
  } catch (error) {
    console.error('获取学生统计错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 获取房间内的学生
router.get('/room/:roomId', auth, async (req, res) => {
  try {
    const students = await Student.findByRoom(req.params.roomId);
    res.json({ code: 200, data: students });
  } catch (error) {
    console.error('获取房间学生错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 获取单个学生
router.get('/:id', auth, async (req, res) => {
  try {
    const student = await Student.findById(req.params.id);
    if (!student) {
      return res.status(404).json({ 
        code: 404, 
        message: '学生不存在' 
      });
    }
    res.json({ code: 200, data: student });
  } catch (error) {
    console.error('获取学生信息错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 创建学生（入住）
router.post('/', auth, adminOnly, [
  body('studentNo').trim().notEmpty().withMessage('学号不能为空'),
  body('name').trim().notEmpty().withMessage('姓名不能为空'),
  body('gender').isIn(['MALE', 'FEMALE']).withMessage('性别不正确'),
  body('buildingId').isInt({ min: 1 }).withMessage('请选择楼栋'),
  body('roomId').isInt({ min: 1 }).withMessage('请选择房间'),
  body('bedNumber').isInt({ min: 1 }).withMessage('床位号不正确'),
], async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ 
      code: 400, 
      message: errors.array()[0].msg 
    });
  }

  try {
    const { studentNo, roomId, bedNumber } = req.body;

    // 检查学号是否已存在
    if (await Student.existsByStudentNo(studentNo)) {
      return res.status(400).json({ 
        code: 400, 
        message: '学号已存在' 
      });
    }

    // 检查床位是否被占用
    if (await Student.isBedOccupied(roomId, bedNumber)) {
      return res.status(400).json({ 
        code: 400, 
        message: '该床位已被占用' 
      });
    }

    // 检查房间是否已满
    const room = await Room.findById(roomId);
    if (!room) {
      return res.status(400).json({ 
        code: 400, 
        message: '房间不存在' 
      });
    }

    if (room.status === 'FULL') {
      return res.status(400).json({ 
        code: 400, 
        message: '房间已满' 
      });
    }

    const id = await Student.create(req.body);
    const student = await Student.findById(id);

    res.status(201).json({ 
      code: 201, 
      message: '入住成功', 
      data: student 
    });
  } catch (error) {
    console.error('创建学生错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 更新学生信息
router.put('/:id', auth, adminOnly, [
  body('name').trim().notEmpty().withMessage('姓名不能为空'),
  body('gender').isIn(['MALE', 'FEMALE']).withMessage('性别不正确'),
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

    // 检查学生是否存在
    const existing = await Student.findById(id);
    if (!existing) {
      return res.status(404).json({ 
        code: 404, 
        message: '学生不存在' 
      });
    }

    await Student.update(id, req.body);
    const student = await Student.findById(id);

    res.json({ 
      code: 200, 
      message: '更新成功', 
      data: student 
    });
  } catch (error) {
    console.error('更新学生错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 更换房间
router.put('/:id/room', auth, adminOnly, [
  body('buildingId').isInt({ min: 1 }).withMessage('请选择楼栋'),
  body('roomId').isInt({ min: 1 }).withMessage('请选择房间'),
  body('bedNumber').isInt({ min: 1 }).withMessage('床位号不正确'),
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
    const { buildingId, roomId, bedNumber } = req.body;

    // 检查学生是否存在
    const existing = await Student.findById(id);
    if (!existing) {
      return res.status(404).json({ 
        code: 404, 
        message: '学生不存在' 
      });
    }

    // 检查床位是否被占用（排除自己）
    if (await Student.isBedOccupied(roomId, bedNumber, parseInt(id))) {
      return res.status(400).json({ 
        code: 400, 
        message: '该床位已被占用' 
      });
    }

    // 检查房间是否已满（如果要换到新房间）
    if (existing.roomId !== roomId) {
      const room = await Room.findById(roomId);
      if (!room) {
        return res.status(400).json({ 
          code: 400, 
          message: '房间不存在' 
        });
      }

      if (room.status === 'FULL') {
        return res.status(400).json({ 
          code: 400, 
          message: '目标房间已满' 
        });
      }
    }

    await Student.changeRoom(id, buildingId, roomId, bedNumber);
    const student = await Student.findById(id);

    res.json({ 
      code: 200, 
      message: '换房成功', 
      data: student 
    });
  } catch (error) {
    console.error('更换房间错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 退宿
router.put('/:id/checkout', auth, adminOnly, async (req, res) => {
  try {
    const { id } = req.params;

    const existing = await Student.findById(id);
    if (!existing) {
      return res.status(404).json({ 
        code: 404, 
        message: '学生不存在' 
      });
    }

    if (existing.status !== 'ACTIVE') {
      return res.status(400).json({ 
        code: 400, 
        message: '学生已退宿' 
      });
    }

    await Student.checkOut(id);
    res.json({ 
      code: 200, 
      message: '退宿成功' 
    });
  } catch (error) {
    console.error('退宿错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 删除学生
router.delete('/:id', auth, adminOnly, async (req, res) => {
  try {
    const { id } = req.params;

    await Student.delete(id);
    res.json({ 
      code: 200, 
      message: '删除成功' 
    });
  } catch (error) {
    console.error('删除学生错误:', error);
    if (error.message.includes('入住中') || error.message.includes('不存在')) {
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