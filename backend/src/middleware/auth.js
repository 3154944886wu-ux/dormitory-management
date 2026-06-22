const jwt = require('jsonwebtoken');

const JWT_SECRET = process.env.JWT_SECRET || 'dormitory-secret-key-2024';

// 验证Token中间件
const auth = (req, res, next) => {
  const token = req.headers.authorization?.replace('Bearer ', '');
  
  if (!token) {
    return res.status(401).json({ code: 401, message: '未登录，请先登录' });
  }

  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    req.user = decoded;
    next();
  } catch (error) {
    return res.status(401).json({ code: 401, message: 'Token已过期，请重新登录' });
  }
};

// 生成Token
const generateToken = (user) => {
  return jwt.sign(
    { id: user.id, username: user.username, role: user.role },
    JWT_SECRET,
    { expiresIn: '7d' }
  );
};

module.exports = { auth, generateToken, JWT_SECRET };