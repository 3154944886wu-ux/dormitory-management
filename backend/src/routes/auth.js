const express = require('express');
const { body, validationResult } = require('express-validator');
const User = require('../models/User');
const { auth, generateToken } = require('../middleware/auth');

const router = express.Router();

// 用户注册
router.post('/register', [
  body('username').trim().isLength({ min: 3, max: 20 }).withMessage('用户名长度为3-20个字符'),
  body('password').isLength({ min: 6 }).withMessage('密码长度至少6位'),
  body('nickname').optional().trim(),
  body('phone').optional().trim(),
  body('email').optional().isEmail().withMessage('邮箱格式不正确'),
], async (req, res) => {
  // 验证请求
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ 
      code: 400, 
      message: errors.array()[0].msg 
    });
  }

  const { username, password, nickname, phone, email } = req.body;

  try {
    // 检查用户名是否已存在
    if (await User.exists(username)) {
      return res.status(400).json({ 
        code: 400, 
        message: '用户名已存在' 
      });
    }

    // 创建用户
    const userId = await User.create({ username, password, nickname, phone, email });
    
    res.status(201).json({
      code: 201,
      message: '注册成功',
      data: { userId }
    });
  } catch (error) {
    console.error('注册错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误，注册失败' 
    });
  }
});

// 用户登录
router.post('/login', [
  body('username').notEmpty().withMessage('用户名不能为空'),
  body('password').notEmpty().withMessage('密码不能为空'),
], async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ 
      code: 400, 
      message: errors.array()[0].msg 
    });
  }

  const { username, password } = req.body;

  try {
    // 查找用户
    const user = await User.findByUsername(username);
    if (!user) {
      return res.status(401).json({ 
        code: 401, 
        message: '用户名或密码错误' 
      });
    }

    // 检查账号状态
    if (user.status === 0) {
      return res.status(401).json({ 
        code: 401, 
        message: '账号已被禁用，请联系管理员' 
      });
    }

    // 验证密码
    const isValid = await User.verifyPassword(password, user.password);
    if (!isValid) {
      return res.status(401).json({ 
        code: 401, 
        message: '用户名或密码错误' 
      });
    }

    // 生成Token
    const token = generateToken(user);

    res.json({
      code: 200,
      message: '登录成功',
      data: {
        token,
        user: {
          id: user.id,
          username: user.username,
          nickname: user.nickname,
          role: user.role
        }
      }
    });
  } catch (error) {
    console.error('登录错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误，登录失败' 
    });
  }
});

// 获取当前用户信息
router.get('/me', auth, async (req, res) => {
  try {
    const user = await User.findById(req.user.id);
    if (!user) {
      return res.status(404).json({ 
        code: 404, 
        message: '用户不存在' 
      });
    }
    res.json({ code: 200, data: user });
  } catch (error) {
    console.error('获取用户信息错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

// 更新个人信息
router.put('/profile', auth, [
  body('nickname').optional().trim(),
  body('phone').optional().trim(),
  body('email').optional().isEmail().withMessage('邮箱格式不正确'),
], async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ 
      code: 400, 
      message: errors.array()[0].msg 
    });
  }

  try {
    await User.update(req.user.id, req.body);
    const user = await User.findById(req.user.id);
    res.json({ 
      code: 200, 
      message: '信息更新成功', 
      data: user 
    });
  } catch (error) {
    console.error('更新用户信息错误:', error);
    res.status(500).json({ 
      code: 500, 
      message: '服务器错误' 
    });
  }
});

module.exports = router;