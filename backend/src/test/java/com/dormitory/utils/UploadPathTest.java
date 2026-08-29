package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UploadPathTest {

    @Test
    void acceptsRelativeUploadUrl() {
        assertEquals("2026-08/abc.jpg", UploadPath.relativeFile(" /uploads/2026-08/abc.jpg "));
        assertEquals("2026-08/abc.jpg", UploadPath.relativeFile("2026-08/abc.jpg"));
    }

    @Test
    void rejectsPathTraversalAndAbsolutePaths() {
        assertThrows(IllegalArgumentException.class, () -> UploadPath.relativeFile("../etc/passwd"));
        assertThrows(IllegalArgumentException.class, () -> UploadPath.relativeFile("/uploads/../secret.txt"));
        assertThrows(IllegalArgumentException.class, () -> UploadPath.relativeFile("/etc/passwd"));
        assertThrows(IllegalArgumentException.class, () -> UploadPath.relativeFile(""));
        assertThrows(IllegalArgumentException.class, () -> UploadPath.relativeFile(null));
    }
}
