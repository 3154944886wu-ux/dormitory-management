const mysql = require('mysql2/promise');

const pool = mysql.createPool({
  host: 'localhost',
  user: 'root',
  password: 'root123456',
  database: 'dormitory',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});

// 初始化数据库表
async function initDatabase() {
  const connection = await pool.getConnection();
  try {
    // 用户表
    await connection.query(`
      CREATE TABLE IF NOT EXISTS users (
        id INT PRIMARY KEY AUTO_INCREMENT,
        username VARCHAR(50) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL,
        nickname VARCHAR(50),
        phone VARCHAR(20),
        email VARCHAR(100),
        role ENUM('STUDENT', 'ADMIN') DEFAULT 'STUDENT',
        status TINYINT DEFAULT 1 COMMENT '1正常, 0禁用',
        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'
    `);

    // 楼栋表
    await connection.query(`
      CREATE TABLE IF NOT EXISTS buildings (
        id INT PRIMARY KEY AUTO_INCREMENT,
        name VARCHAR(50) NOT NULL COMMENT '楼栋名称',
        floors INT NOT NULL DEFAULT 6 COMMENT '楼层数',
        rooms_per_floor INT NOT NULL DEFAULT 20 COMMENT '每层房间数',
        gender_type ENUM('MALE', 'FEMALE', 'MIXED') DEFAULT 'MALE' COMMENT '楼栋类型',
        manager VARCHAR(50) COMMENT '宿管姓名',
        manager_phone VARCHAR(20) COMMENT '宿管电话',
        status TINYINT DEFAULT 1 COMMENT '1启用, 0停用',
        remark TEXT COMMENT '备注',
        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        UNIQUE KEY uk_name (name)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼栋表'
    `);

    // 房间表
    await connection.query(`
      CREATE TABLE IF NOT EXISTS rooms (
        id INT PRIMARY KEY AUTO_INCREMENT,
        building_id INT NOT NULL COMMENT '楼栋ID',
        room_number VARCHAR(20) NOT NULL COMMENT '房间号',
        floor INT NOT NULL COMMENT '楼层',
        bed_count INT NOT NULL DEFAULT 4 COMMENT '床位总数',
        occupied_beds INT DEFAULT 0 COMMENT '已占用床位',
        status ENUM('EMPTY', 'PARTIAL', 'FULL') DEFAULT 'EMPTY' COMMENT '房间状态',
        remark TEXT COMMENT '备注',
        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        KEY idx_building (building_id),
        KEY idx_room_number (room_number),
        UNIQUE KEY uk_building_room (building_id, room_number),
        FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表'
    `);

    // 学生表
    await connection.query(`
      CREATE TABLE IF NOT EXISTS students (
        id INT PRIMARY KEY AUTO_INCREMENT,
        user_id INT COMMENT '关联用户ID',
        student_no VARCHAR(50) UNIQUE NOT NULL COMMENT '学号',
        name VARCHAR(50) NOT NULL COMMENT '姓名',
        gender ENUM('MALE', 'FEMALE') NOT NULL COMMENT '性别',
        phone VARCHAR(20) COMMENT '联系电话',
        id_card VARCHAR(18) COMMENT '身份证号',
        class_name VARCHAR(50) COMMENT '班级',
        major VARCHAR(50) COMMENT '专业',
        college VARCHAR(100) COMMENT '学院',
        building_id INT COMMENT '楼栋ID',
        room_id INT COMMENT '房间ID',
        bed_number INT COMMENT '床位号',
        status ENUM('ACTIVE', 'CHECKED_OUT') DEFAULT 'ACTIVE' COMMENT '状态',
        check_in_date DATE COMMENT '入住日期',
        check_out_date DATE COMMENT '退宿日期',
        remark TEXT COMMENT '备注',
        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        KEY idx_student_no (student_no),
        KEY idx_building (building_id),
        KEY idx_room (room_id),
        FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE SET NULL,
        FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表'
    `);

    console.log('✅ 数据库表初始化完成');
  } finally {
    connection.release();
  }
}

module.exports = { pool, initDatabase };