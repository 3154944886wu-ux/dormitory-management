package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileAccessPolicyTest {

    @Test
    void adminCanReadAnyFileManagerNeedsOwnership() {
        assertTrue(FileAccessPolicy.canRead("ADMIN", false));
        assertFalse(FileAccessPolicy.canRead("MANAGER", false));
        assertTrue(FileAccessPolicy.canRead("MANAGER", true));
        assertTrue(FileAccessPolicy.canRead("admin", true));
    }

    @Test
    void studentDependsOnOwnership() {
        assertTrue(FileAccessPolicy.canRead("STUDENT", true));
        assertFalse(FileAccessPolicy.canRead("STUDENT", false));
        assertFalse(FileAccessPolicy.canRead("student", false));
    }

    @Test
    void unknownRoleDenied() {
        assertFalse(FileAccessPolicy.canRead(null, true));
        assertFalse(FileAccessPolicy.canRead("GUEST", true));
    }
}
