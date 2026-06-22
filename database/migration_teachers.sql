-- 管理人员表（工号体系，与 students.student_no 类似）
CREATE TABLE IF NOT EXISTS managers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_no VARCHAR(6) NOT NULL UNIQUE COMMENT '工号（6位）',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    user_id BIGINT COMMENT '关联用户ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 1在职, 0停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY uk_manager_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理人员表';

-- 将已有 MANAGER 账号（6位工号格式）回填到 managers 表
INSERT INTO managers (employee_no, name, user_id, phone, email, status)
SELECT u.username, COALESCE(u.nickname, u.username), u.id, u.phone, u.email, COALESCE(u.status, 1)
FROM users u
WHERE u.role = 'MANAGER'
  AND u.username REGEXP '^[0-9]{6}$'
  AND NOT EXISTS (SELECT 1 FROM managers m WHERE m.user_id = u.id);
