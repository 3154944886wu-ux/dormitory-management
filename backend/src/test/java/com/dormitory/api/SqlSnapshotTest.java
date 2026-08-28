package com.dormitory.api;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlSnapshotTest {

    @Test
    void dormitorySqlIsSchemaOnlyGoldenSnapshot() throws Exception {
        Path dump = snapshotPath();
        String text = Files.readString(dump);
        assertTrue(text.contains("CONVENIENCE SNAPSHOT ONLY"), "应标明便利快照而非真相源");
        assertTrue(text.contains("CREATE TABLE `managers`"));
        assertTrue(text.contains("CREATE TABLE `manager_scope`"));
        assertTrue(text.contains("rectify_remark"));
        assertTrue(text.contains("uk_student_date_type"));
        assertFalse(text.contains("`payment_status`"), "不得保留废弃列 payment_status");
        assertFalse(text.contains("INSERT INTO"), "快照不得包含业务 INSERT");
        String users = slice(text, "CREATE TABLE `users`", "ENGINE=");
        assertFalse(users.contains("`student_id`"), "users 不得保留 dump 遗留 student_id");
    }

    private static Path snapshotPath() {
        Path cwd = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path direct = cwd.resolve("database/dormitory.sql");
        if (Files.exists(direct)) {
            return direct;
        }
        Path fromBackend = cwd.resolve("../database/dormitory.sql").normalize();
        if (Files.exists(fromBackend)) {
            return fromBackend;
        }
        throw new IllegalStateException("找不到 database/dormitory.sql，cwd=" + cwd);
    }

    private static String slice(String text, String start, String endMarker) {
        int from = text.indexOf(start);
        int to = text.indexOf(endMarker, from);
        return text.substring(from, to);
    }
}
