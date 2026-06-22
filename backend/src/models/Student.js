const { pool } = require('../config/db');

class Student {
  // 获取所有学生
  static async findAll(filters = {}) {
    let sql = `SELECT s.*, 
               b.name as building_name, 
               r.room_number,
               u.username, u.nickname
               FROM students s
               LEFT JOIN buildings b ON s.building_id = b.id
               LEFT JOIN rooms r ON s.room_id = r.id
               LEFT JOIN users u ON s.user_id = u.id
               WHERE 1=1`;
    const params = [];

    if (filters.buildingId) {
      sql += ' AND s.building_id = ?';
      params.push(filters.buildingId);
    }

    if (filters.roomId) {
      sql += ' AND s.room_id = ?';
      params.push(filters.roomId);
    }

    if (filters.status) {
      sql += ' AND s.status = ?';
      params.push(filters.status);
    }

    if (filters.gender) {
      sql += ' AND s.gender = ?';
      params.push(filters.gender);
    }

    if (filters.keyword) {
      sql += ' AND (s.name LIKE ? OR s.student_no LIKE ? OR s.phone LIKE ?)';
      const kw = `%${filters.keyword}%`;
      params.push(kw, kw, kw);
    }

    sql += ' ORDER BY b.id ASC, r.room_number ASC, s.id ASC';

    const [rows] = await pool.query(sql, params);
    return rows.map(row => ({
      ...row,
      buildingId: row.building_id,
      buildingName: row.building_name,
      roomId: row.room_id,
      roomNumber: row.room_number,
      studentNo: row.student_no,
      userId: row.user_id,
      checkInDate: row.check_in_date,
      checkOutDate: row.check_out_date
    }));
  }

  // 根据ID查找学生
  static async findById(id) {
    const [rows] = await pool.query(
      `SELECT s.*, 
       b.name as building_name, 
       r.room_number,
       u.username, u.nickname
       FROM students s
       LEFT JOIN buildings b ON s.building_id = b.id
       LEFT JOIN rooms r ON s.room_id = r.id
       LEFT JOIN users u ON s.user_id = u.id
       WHERE s.id = ?`,
      [id]
    );
    
    if (!rows[0]) return null;
    
    return {
      ...rows[0],
      buildingId: rows[0].building_id,
      buildingName: rows[0].building_name,
      roomId: rows[0].room_id,
      roomNumber: rows[0].room_number,
      studentNo: rows[0].student_no,
      userId: rows[0].user_id,
      checkInDate: rows[0].check_in_date,
      checkOutDate: rows[0].check_out_date
    };
  }

  // 根据学号查找学生
  static async findByStudentNo(studentNo) {
    const [rows] = await pool.query(
      'SELECT * FROM students WHERE student_no = ?',
      [studentNo]
    );
    return rows[0];
  }

  // 创建学生
  static async create(data) {
    const connection = await pool.getConnection();
    try {
      await connection.beginTransaction();

      const { 
        userId, studentNo, name, gender, phone, 
        idCard, className, major, college,
        buildingId, roomId, bedNumber, remark 
      } = data;

      // 插入学生记录
      const [result] = await connection.query(
        `INSERT INTO students 
         (user_id, student_no, name, gender, phone, id_card, class_name, major, college, 
          building_id, room_id, bed_number, status, check_in_date) 
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE', CURDATE())`,
        [userId, studentNo, name, gender, phone, idCard, className, major, college, 
         buildingId, roomId, bedNumber, remark]
      );

      // 更新房间入住人数
      if (roomId) {
        await connection.query(
          `UPDATE rooms SET occupied_beds = occupied_beds + 1,
           status = CASE 
             WHEN occupied_beds + 1 >= bed_count THEN 'FULL'
             WHEN occupied_beds + 1 > 0 THEN 'PARTIAL'
             ELSE 'EMPTY'
           END
           WHERE id = ?`,
          [roomId]
        );
      }

      await connection.commit();
      return result.insertId;
    } catch (error) {
      await connection.rollback();
      throw error;
    } finally {
      connection.release();
    }
  }

  // 更新学生信息
  static async update(id, data) {
    const { 
      name, gender, phone, idCard, className, major, college,
      buildingId, roomId, bedNumber, remark 
    } = data;
    
    await pool.query(
      `UPDATE students 
       SET name = ?, gender = ?, phone = ?, id_card = ?, class_name = ?, major = ?, college = ?,
           building_id = ?, room_id = ?, bed_number = ?, remark = ?
       WHERE id = ?`,
      [name, gender, phone, idCard, className, major, college, 
       buildingId, roomId, bedNumber, remark, id]
    );
    return true;
  }

  // 更换房间
  static async changeRoom(id, newBuildingId, newRoomId, newBedNumber) {
    const connection = await pool.getConnection();
    try {
      await connection.beginTransaction();

      // 获取学生当前信息
      const [students] = await connection.query(
        'SELECT * FROM students WHERE id = ?',
        [id]
      );
      
      if (!students[0]) {
        throw new Error('学生不存在');
      }

      const student = students[0];
      const oldRoomId = student.room_id;

      // 如果房间有变化，更新房间入住人数
      if (oldRoomId !== newRoomId) {
        // 旧房间减一人
        if (oldRoomId) {
          await connection.query(
            `UPDATE rooms SET occupied_beds = GREATEST(0, occupied_beds - 1),
             status = CASE 
               WHEN occupied_beds - 1 <= 0 THEN 'EMPTY'
               WHEN occupied_beds - 1 >= bed_count THEN 'FULL'
               ELSE 'PARTIAL'
             END
             WHERE id = ?`,
            [oldRoomId]
          );
        }

        // 新房间加一人
        if (newRoomId) {
          await connection.query(
            `UPDATE rooms SET occupied_beds = occupied_beds + 1,
             status = CASE 
               WHEN occupied_beds + 1 >= bed_count THEN 'FULL'
               ELSE 'PARTIAL'
             END
             WHERE id = ?`,
            [newRoomId]
          );
        }
      }

      // 更新学生房间信息
      await connection.query(
        `UPDATE students SET building_id = ?, room_id = ?, bed_number = ? WHERE id = ?`,
        [newBuildingId, newRoomId, newBedNumber, id]
      );

      await connection.commit();
      return true;
    } catch (error) {
      await connection.rollback();
      throw error;
    } finally {
      connection.release();
    }
  }

  // 退宿
  static async checkOut(id) {
    const connection = await pool.getConnection();
    try {
      await connection.beginTransaction();

      // 获取学生信息
      const [students] = await connection.query(
        'SELECT * FROM students WHERE id = ?',
        [id]
      );
      
      if (!students[0]) {
        throw new Error('学生不存在');
      }

      const student = students[0];

      // 更新学生状态
      await connection.query(
        `UPDATE students SET status = 'CHECKED_OUT', room_id = NULL, building_id = NULL, 
         bed_number = NULL, check_out_date = CURDATE() 
         WHERE id = ?`,
        [id]
      );

      // 更新房间入住人数
      if (student.room_id) {
        await connection.query(
          `UPDATE rooms SET occupied_beds = GREATEST(0, occupied_beds - 1),
           status = CASE 
             WHEN occupied_beds - 1 <= 0 THEN 'EMPTY'
             WHEN occupied_beds - 1 >= bed_count THEN 'FULL'
             ELSE 'PARTIAL'
           END
           WHERE id = ?`,
          [student.room_id]
        );
      }

      await connection.commit();
      return true;
    } catch (error) {
      await connection.rollback();
      throw error;
    } finally {
      connection.release();
    }
  }

  // 删除学生
  static async delete(id) {
    const student = await this.findById(id);
    if (!student) {
      throw new Error('学生不存在');
    }

    if (student.status === 'ACTIVE' && student.roomId) {
      throw new Error('学生正在入住中，请先办理退宿');
    }

    await pool.query('DELETE FROM students WHERE id = ?', [id]);
    return true;
  }

  // 获取房间内的学生
  static async findByRoom(roomId) {
    const [rows] = await pool.query(
      `SELECT s.*, u.username, u.nickname
       FROM students s
       LEFT JOIN users u ON s.user_id = u.id
       WHERE s.room_id = ? AND s.status = 'ACTIVE'
       ORDER BY s.bed_number ASC`,
      [roomId]
    );
    return rows;
  }

  // 检查学号是否存在
  static async existsByStudentNo(studentNo, excludeId = null) {
    let sql = 'SELECT id FROM students WHERE student_no = ?';
    const params = [studentNo];

    if (excludeId) {
      sql += ' AND id != ?';
      params.push(excludeId);
    }

    const [rows] = await pool.query(sql, params);
    return rows.length > 0;
  }

  // 检查床位是否被占用
  static async isBedOccupied(roomId, bedNumber, excludeId = null) {
    let sql = 'SELECT id FROM students WHERE room_id = ? AND bed_number = ? AND status = "ACTIVE"';
    const params = [roomId, bedNumber];

    if (excludeId) {
      sql += ' AND id != ?';
      params.push(excludeId);
    }

    const [rows] = await pool.query(sql, params);
    return rows.length > 0;
  }

  // 获取统计
  static async getStats(buildingId = null) {
    let sql = `SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) as active,
                SUM(CASE WHEN status = 'CHECKED_OUT' THEN 1 ELSE 0 END) as checked_out,
                SUM(CASE WHEN gender = 'MALE' THEN 1 ELSE 0 END) as male,
                SUM(CASE WHEN gender = 'FEMALE' THEN 1 ELSE 0 END) as female
              FROM students`;
    
    const params = [];
    if (buildingId) {
      sql += ' WHERE building_id = ?';
      params.push(buildingId);
    }

    const [rows] = await pool.query(sql, params);
    return rows[0];
  }
}

module.exports = Student;