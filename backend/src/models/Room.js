const { pool } = require('../config/db');

class Room {
  // 获取所有房间
  static async findAll(filters = {}) {
    let sql = `SELECT r.*, b.name as building_name 
               FROM rooms r 
               LEFT JOIN buildings b ON r.building_id = b.id 
               WHERE 1=1`;
    const params = [];

    if (filters.buildingId) {
      sql += ' AND r.building_id = ?';
      params.push(filters.buildingId);
    }

    if (filters.status) {
      sql += ' AND r.status = ?';
      params.push(filters.status);
    }

    if (filters.floor) {
      sql += ' AND r.floor = ?';
      params.push(filters.floor);
    }

    sql += ' ORDER BY b.id ASC, r.floor ASC, r.room_number ASC';

    const [rows] = await pool.query(sql, params);
    return rows.map(row => ({
      ...row,
      buildingId: row.building_id,
      buildingName: row.building_name,
      roomNumber: row.room_number,
      bedCount: row.bed_count,
      occupiedBeds: row.occupied_beds
    }));
  }

  // 根据ID查找房间
  static async findById(id) {
    const [rows] = await pool.query(
      `SELECT r.*, b.name as building_name 
       FROM rooms r 
       LEFT JOIN buildings b ON r.building_id = b.id 
       WHERE r.id = ?`,
      [id]
    );
    
    if (!rows[0]) return null;
    
    return {
      ...rows[0],
      buildingId: rows[0].building_id,
      buildingName: rows[0].building_name,
      roomNumber: rows[0].room_number,
      bedCount: rows[0].bed_count,
      occupiedBeds: rows[0].occupied_beds
    };
  }

  // 创建房间
  static async create(data) {
    const { buildingId, roomNumber, floor, bedCount, status = 'EMPTY', remark } = data;
    
    const [result] = await pool.query(
      `INSERT INTO rooms 
       (building_id, room_number, floor, bed_count, status, remark) 
       VALUES (?, ?, ?, ?, ?, ?)`,
      [buildingId, roomNumber, floor, bedCount, status, remark]
    );
    return result.insertId;
  }

  // 更新房间
  static async update(id, data) {
    const { roomNumber, bedCount, status, remark } = data;
    
    await pool.query(
      `UPDATE rooms 
       SET room_number = ?, bed_count = ?, status = ?, remark = ?
       WHERE id = ?`,
      [roomNumber, bedCount, status, remark, id]
    );
    return true;
  }

  // 更新状态
  static async updateStatus(id, status) {
    await pool.query(
      'UPDATE rooms SET status = ? WHERE id = ?',
      [status, id]
    );
    return true;
  }

  // 删除房间
  static async delete(id) {
    // 检查是否有学生入住
    const [students] = await pool.query(
      'SELECT COUNT(*) as count FROM students WHERE room_id = ?',
      [id]
    );
    
    if (students[0].count > 0) {
      throw new Error('该房间内有学生入住，无法删除');
    }

    await pool.query('DELETE FROM rooms WHERE id = ?', [id]);
    return true;
  }

  // 检查房间号是否存在
  static async existsByNumber(buildingId, roomNumber, excludeId = null) {
    let sql = 'SELECT id FROM rooms WHERE building_id = ? AND room_number = ?';
    const params = [buildingId, roomNumber];

    if (excludeId) {
      sql += ' AND id != ?';
      params.push(excludeId);
    }

    const [rows] = await pool.query(sql, params);
    return rows.length > 0;
  }

  // 批量创建房间（为楼栋初始化房间）
  static async batchCreate(buildingId, floors, roomsPerFloor, bedCount = 4) {
    const connection = await pool.getConnection();
    try {
      await connection.beginTransaction();

      // 获取楼栋信息
      const [buildings] = await connection.query(
        'SELECT * FROM buildings WHERE id = ?',
        [buildingId]
      );
      
      if (!buildings[0]) {
        throw new Error('楼栋不存在');
      }

      const building = buildings[0];
      const values = [];

      for (let floor = 1; floor <= floors; floor++) {
        for (let room = 1; room <= roomsPerFloor; room++) {
          // 房间号格式：楼层 + 房间号（如：101, 102, 201, 202）
          const roomNumber = `${floor}${String(room).padStart(2, '0')}`;
          values.push([buildingId, roomNumber, floor, bedCount, 'EMPTY']);
        }
      }

      await connection.query(
        `INSERT IGNORE INTO rooms 
         (building_id, room_number, floor, bed_count, status) 
         VALUES ?`,
        [values]
      );

      await connection.commit();
      return values.length;
    } catch (error) {
      await connection.rollback();
      throw error;
    } finally {
      connection.release();
    }
  }

  // 获取房间统计
  static async getStats(buildingId = null) {
    let sql = `SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN status = 'EMPTY' THEN 1 ELSE 0 END) as empty,
                SUM(CASE WHEN status = 'PARTIAL' THEN 1 ELSE 0 END) as partial,
                SUM(CASE WHEN status = 'FULL' THEN 1 ELSE 0 END) as full,
                SUM(bed_count) as total_beds,
                SUM(occupied_beds) as occupied_beds
              FROM rooms`;
    
    const params = [];
    if (buildingId) {
      sql += ' WHERE building_id = ?';
      params.push(buildingId);
    }

    const [rows] = await pool.query(sql, params);
    return rows[0];
  }
}

module.exports = Room;