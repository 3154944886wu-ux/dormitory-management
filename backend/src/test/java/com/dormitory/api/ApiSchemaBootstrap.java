package com.dormitory.api;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 在 Spring 上下文启动前准备独立库 {@code dormitory_it}。
 */
final class ApiSchemaBootstrap {

    static final String JDBC_DB =
            "jdbc:mysql://127.0.0.1:3306/dormitory_it?useUnicode=true&characterEncoding=utf-8"
                    + "&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false";

    private static final String JDBC_ROOT =
            "jdbc:mysql://127.0.0.1:3306/?allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=utf-8";

    private ApiSchemaBootstrap() {
    }

    static synchronized void ensurePrepared() {
        Path schemaFile = schemaPath();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String adminHash = encoder.encode("admin123");
        String studentHash = encoder.encode("123456");
        String managerHash = encoder.encode("mgrpass1");
        try (Connection conn = DriverManager.getConnection(JDBC_ROOT, "root", "")) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DROP DATABASE IF EXISTS dormitory_it");
                st.executeUpdate("CREATE DATABASE dormitory_it DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                    "无法连接本机 MySQL 以创建 dormitory_it。接口契约测试需要 MySQL 8，root 空密码（与 .cursor/install.sh 一致）。",
                    e);
        }

        String schemaSql;
        try {
            schemaSql = Files.readString(schemaFile)
                    .replace("CREATE DATABASE IF NOT EXISTS dormitory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;", "")
                    .replace("USE dormitory;", "USE dormitory_it;");
        } catch (Exception e) {
            throw new IllegalStateException("无法读取 " + schemaFile, e);
        }

        try (Connection conn = DriverManager.getConnection(JDBC_ROOT, "root", "")) {
            ScriptUtils.executeSqlScript(conn, new ByteArrayResource(schemaSql.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("导入 schema.sql 到 dormitory_it 失败", e);
        }

        String fixture = fixtureSql()
                .replace("__ADMIN_HASH__", adminHash)
                .replace("__STUDENT_HASH__", studentHash)
                .replace("__MANAGER_HASH__", managerHash);
        try (Connection conn = DriverManager.getConnection(JDBC_DB, "root", "")) {
            ScriptUtils.executeSqlScript(conn, new ByteArrayResource(fixture.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("导入接口契约夹具失败", e);
        }
    }

    private static Path schemaPath() {
        Path cwd = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path direct = cwd.resolve("database/schema.sql");
        if (Files.exists(direct)) {
            return direct;
        }
        Path fromBackend = cwd.resolve("../database/schema.sql").normalize();
        if (Files.exists(fromBackend)) {
            return fromBackend;
        }
        throw new IllegalStateException("找不到 database/schema.sql，cwd=" + cwd);
    }

    private static String fixtureSql() {
        return """
                SET NAMES utf8mb4;
                SET FOREIGN_KEY_CHECKS = 0;
                INSERT INTO users (id, username, password, nickname, role, status) VALUES
                  (1, 'admin', '__ADMIN_HASH__', '系统管理员', 'ADMIN', 1),
                  (2, '20230001', '__STUDENT_HASH__', '张伟', 'STUDENT', 1),
                  (3, 'mgr_b1', '__MANAGER_HASH__', '1号楼宿管', 'MANAGER', 1),
                  (4, '20230003', '__STUDENT_HASH__', '李娜', 'STUDENT', 1);
                INSERT INTO buildings (id, name, floors, rooms_per_floor, gender_type, gender_limit, status) VALUES
                  (1, '1号楼', 6, 20, 'MALE', 'MALE', 1),
                  (2, '2号楼', 6, 20, 'FEMALE', 'FEMALE', 1);
                INSERT INTO rooms (id, building_id, room_number, floor, capacity, current_count, status, room_type) VALUES
                  (1, 1, '0101', 1, 4, 0, 1, '4人间'),
                  (2, 2, '0101', 1, 4, 0, 1, '4人间');
                INSERT INTO students (id, student_no, name, gender, class_name, user_id, room_id, status) VALUES
                  (1, '20230001', '张伟', '男', '计算机科学与技术2023级1班', 2, 1, 1),
                  (2, '20230002', '王磊', '男', '计算机科学与技术2023级1班', NULL, 1, 1),
                  (3, '20230003', '李娜', '女', '软件工程2023级1班', 4, 2, 1);
                INSERT INTO manager_scope (user_id, building_id, class_name, status) VALUES
                  (3, 1, NULL, 1);
                INSERT INTO college (id, name) VALUES (1, '计算机与人工智能学院');
                INSERT INTO dorm_batch (id, name, college_id, match_status) VALUES
                  (1, '空批次-接口测试', 1, 'running');
                INSERT INTO leave_requests (student_id, leave_type, reason, start_time, end_time, status) VALUES
                  (1, 0, '1号楼学生请假', '2026-08-28 08:00:00', '2026-08-28 20:00:00', 0),
                  (3, 0, '2号楼学生请假', '2026-08-28 08:00:00', '2026-08-28 20:00:00', 0);
                INSERT INTO utility_fees (room_id, year, month, electricity_fee, water_fee, total_fee, status) VALUES
                  (1, 2026, 8, 10.00, 5.00, 15.00, 0),
                  (2, 2026, 8, 20.00, 8.00, 28.00, 0);
                INSERT INTO inspection_records (building_id, room_id, inspector_id, inspector_name, inspection_time, result) VALUES
                  (1, 1, 1, '系统管理员', '2026-08-28 10:00:00', 'PASS'),
                  (2, 2, 1, '系统管理员', '2026-08-28 11:00:00', 'PASS');
                INSERT INTO repairs (room_id, student_id, type, description, status) VALUES
                  (1, 1, '电器', '1号楼灯管', 0),
                  (2, 3, '水管', '2号楼漏水', 0);
                SET FOREIGN_KEY_CHECKS = 1;
                """;
    }
}
