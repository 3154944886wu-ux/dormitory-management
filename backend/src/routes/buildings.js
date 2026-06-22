const express = require('express');
const { body, validationResult } = require('express-validator');
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

// 获取楼栋列表
router.get('/', auth, async (req, res) => {
  try {
    const { status, genderType } = req.query;
    const filters = {};
    
    if (status !== undefined) filters.status = parseInt(status);
    if (genderType) filters.genderType = genderType;

    const buildings = await Building.findAll(filters);
    
    res.json({ 
      code: 200, 
      data: buildings.map(b => ({
        ...b,
        roomsPerFloor: b.rooms_per_floor,
        genderType: b.gender_type,
        managerPhone: b.manager_phone
      }))
    });
  } catch (error) {
    console.error('获取楼栋列表错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 获取单个楼栋
router.get('/:id', auth, async (req, res) => {
  try {
    const building = await Building.findById(req.params.id);
    if (!building) {
      return res.status(404).json({ 
        code: 404, 
        message: '楼栋不存在' 
      });
    }

    const stats = await Building.getStats(req.params.id);
    
    res.json({ 
      code: 200, 
      data: {
        ...building,
        roomsPerFloor: building.rooms_per_floor,
        genderType: building.gender_type,
        managerPhone: building.manager_phone,
        stats
      }
    });
  } catch (error) {
    console.error('获取楼栋信息错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 创建楼栋
router.post('/', auth, adminOnly, [
  body('name').trim().notEmpty().withMessage('楼栋名称不能为空'),
  body('floors').isInt({ min: 1, max: 50 }).withMessage('楼层数必须在1-50之间'),
  body('roomsPerFloor').isInt({ min: 1, max: 100 }).withMessage('每层房间数必须在1-100之间'),
  body('genderType').optional().isIn(['MALE', 'FEMALE', 'MIXED']).withMessage('楼栋类型不正确'),
], async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ 
      code: 400, 
      message: errors.array()[0].msg 
    });
  }

  try {
    const { name } = req.body;

    // 检查名称是否已存在
    if (await Building.existsByName(name)) {
      return res.status(400).json({ 
        code: 400, 
        message: '楼栋名称已存在' 
      });
    }

    const id = await Building.create(req.body);
    const building = await Building.findById(id);

    res.status(201).json({ 
      code: 201, 
      message: '创建成功', 
      data: {
        ...building,
        roomsPerFloor: building.rooms_per_floor,
        genderType: building.gender_type,
        managerPhone: building.manager_phone
      }
    });
  } catch (error) {
    console.error('创建楼栋错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 更新楼栋
router.put('/:id', auth, adminOnly, [
  body('name').trim().notEmpty().withMessage('楼栋名称不能为空'),
  body('floors').isInt({ min: 1, max: 50 }).withMessage('楼层数必须在1-50之间'),
  body('roomsPerFloor').isInt({ min: 1, max: 100 }).withMessage('每层房间数必须在1-100之间'),
  body('genderType').optional().isIn(['MALE', 'FEMALE', 'MIXED']).withMessage('楼栋类型不正确'),
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
    const { name } = req.body;

    // 检查楼栋是否存在
    const existing = await Building.findById(id);
    if (!existing) {
      return res.status(404).json({ 
        code: 404, 
        message: '楼栋不存在' 
      });
    }

    // 检查名称是否与其他楼栋重复
    if (await Building.existsByName(name, parseInt(id))) {
      return res.status(400).json({ 
        code: 400, 
        message: '楼栋名称已存在' 
      });
    }

    await Building.update(id, req.body);
    const building = await Building.findById(id);

    res.json({ 
      code: 200, 
      message: '更新成功', 
      data: {
        ...building,
        roomsPerFloor: building.rooms_per_floor,
        genderType: building.gender_type,
        managerPhone: building.manager_phone
      }
    });
  } catch (error) {
    console.error('更新楼栋错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 更新状态
router.put('/:id/status', auth, adminOnly, [
  body('status').isIn([0, 1]).withMessage('状态值不正确'),
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

    const existing = await Building.findById(id);
    if (!existing) {
      return res.status(404).json({ 
        code: 404, 
        message: '楼栋不存在' 
      });
    }

    await Building.updateStatus(id, status);
    res.json({ 
      code: 200, 
      message: '状态更新成功' 
    });
  } catch (error) {
    console.error('更新楼栋状态错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 删除楼栋
router.delete('/:id', auth, adminOnly, async (req, res) => {
  try {
    const { id } = req.params;

    const existing = await Building.findById(id);
    if (!existing) {
      return res.status(404).json({ 
        code: 404, 
        message: '楼栋不存在' 
      });
    }

    await Building.delete(id);
    res.json({ 
      code: 200, 
      message: '删除成功' 
    });
  } catch (error) {
    console.error('删除楼栋错误:', error);
    if (error.message.includes('存在房间')) {
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