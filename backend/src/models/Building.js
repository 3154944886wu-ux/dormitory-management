const { pool } = require('../config/db');

class Building {
  // 获取所有楼栋
  static async findAll(filters = {}) {
    let sql = 'SELECT * FROM buildings WHERE 1=1';
    const params = [];

    if (filters.status !== undefined) {
      sql += ' AND status = ?';
      params.push(filters.status);
    }

    if (filters.genderType) {
      sql += ' AND gender_type = ?';
      params.push(filters.genderType);
    }

    sql += ' ORDER BY id ASC';

    const [rows] = await pool.query(sql, params);
    return rows;
  }

  // 根据ID查找楼栋
  static async findById(id) {
    const [rows] = await pool.query(
      'SELECT * FROM buildings WHERE id = ?',
      [id]
    );
    return rows[0];
  }

  // 创建楼栋
  static async create(data) {
    const { name, floors, roomsPerFloor, genderType, manager, managerPhone, remark } = data;
    
    const [result] = await pool.query(
      `INSERT INTO buildings 
       (name, floors, rooms_per_floor, gender_type, manager, manager_phone, remark) 
       VALUES (?, ?, ?, ?, ?, ?, ?)`,
      [name, floors, roomsPerFloor, genderType || 'MALE', manager, managerPhone, remark]
    );
    return result.insertId;
  }

  // 更新楼栋
  static async update(id, data) {
    const { name, floors, roomsPerFloor, genderType, manager, managerPhone, remark } = data;
    
    await pool.query(
      `UPDATE buildings 
       SET name = ?, floors = ?, rooms_per_floor = ?, gender_type = ?, 
           manager = ?, manager_phone = ?, remark = ?
       WHERE id = ?`,
      [name, floors, roomsPerFloor, genderType, manager, managerPhone, remark, id]
    );
    return true;
  }

  // 更新状态
  static async updateStatus(id, status) {
    await pool.query(
      'UPDATE buildings SET status = ? WHERE id = ?',
      [status, id]
    );
    return true;
  }

  // 删除楼栋
  static async delete(id) {
    // 检查是否有房间关联
    const [rooms] = await pool.query(
      'SELECT COUNT(*) as count FROM rooms WHERE building_id = ?',
      [id]
    );
    
    if (rooms[0].count > 0) {
      throw new Error('该楼栋下存在房间，无法删除');
    }

    await pool.query('DELETE FROM buildings WHERE id = ?', [id]);
    return true;
  }

  // 检查楼栋名称是否存在
  static async existsByName(name, excludeId = null) {
    let sql = 'SELECT id FROM buildings WHERE name = ?';
    const params = [name];

    if (excludeId) {
      sql += ' AND id != ?';
      params.push(excludeId);
    }

    const [rows] = await pool.query(sql, params);
    return rows.length > 0;
  }

  // 获取楼栋统计信息
  static async getStats(id) {
    const [rooms] = await pool.query(
      'SELECT COUNT(*) as total, SUM(CASE WHEN status = "OCCUPIED" THEN 1 ELSE 0 END) as occupied FROM rooms WHERE building_id = ?',
      [id]
    );
    
    const [students] = await pool.query(
      'SELECT COUNT(*) as count FROM students WHERE building_id = ?',
      [id]
    );

    return {
      totalRooms: rooms[0].total || 0,
      occupiedRooms: rooms[0].occupied || 0,
      studentCount: students[0].count || 0
    };
  }
}

module.exports = Building;