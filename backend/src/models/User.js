const { pool } = require('../config/db');
const bcrypt = require('bcryptjs');

class User {
  // 根据用户名查找用户
  static async findByUsername(username) {
    const [rows] = await pool.query(
      'SELECT * FROM users WHERE username = ?',
      [username]
    );
    return rows[0];
  }

  // 根据ID查找用户
  static async findById(id) {
    const [rows] = await pool.query(
      'SELECT id, username, nickname, phone, email, role, status, create_time, update_time FROM users WHERE id = ?',
      [id]
    );
    return rows[0];
  }

  // 创建用户
  static async create(userData) {
    const { username, password, nickname, phone, email, role = 'STUDENT' } = userData;
    const hashedPassword = await bcrypt.hash(password, 10);
    
    const [result] = await pool.query(
      'INSERT INTO users (username, password, nickname, phone, email, role) VALUES (?, ?, ?, ?, ?, ?)',
      [username, hashedPassword, nickname || username, phone, email, role]
    );
    return result.insertId;
  }

  // 更新用户信息
  static async update(id, userData) {
    const { nickname, phone, email } = userData;
    await pool.query(
      'UPDATE users SET nickname = ?, phone = ?, email = ? WHERE id = ?',
      [nickname, phone, email, id]
    );
    return true;
  }

  // 验证密码
  static async verifyPassword(plainPassword, hashedPassword) {
    return await bcrypt.compare(plainPassword, hashedPassword);
  }

  // 检查用户名是否存在
  static async exists(username) {
    const [rows] = await pool.query(
      'SELECT id FROM users WHERE username = ?',
      [username]
    );
    return rows.length > 0;
  }
}

module.exports = User;