package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileOwnershipTest {

    @Test
    void matchesExactUrlInCommaSeparatedList() {
        String stored = "/uploads/2026-08/a.jpg,/uploads/2026-08/b.png";
        assertTrue(FileOwnership.containsUrl(stored, "/uploads/2026-08/a.jpg"));
        assertTrue(FileOwnership.containsUrl(stored, "/uploads/2026-08/b.png"));
        assertFalse(FileOwnership.containsUrl(stored, "/uploads/2026-08/c.jpg"));
    }

    @Test
    void trimsWhitespaceAroundStoredUrls() {
        assertTrue(FileOwnership.containsUrl(" /uploads/x.pdf , /uploads/y.jpg ", "/uploads/x.pdf"));
    }

    @Test
    void rejectsBlankStoredOrRequested() {
        assertFalse(FileOwnership.containsUrl(null, "/uploads/a.jpg"));
        assertFalse(FileOwnership.containsUrl("", "/uploads/a.jpg"));
        assertFalse(FileOwnership.containsUrl("/uploads/a.jpg", null));
        assertFalse(FileOwnership.containsUrl("/uploads/a.jpg", ""));
    }

    @Test
    void parsesUploaderFromPrefixedFilename() {
        String url = "/uploads/2026-08/u12_aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee.png";
        assertEquals(12L, FileOwnership.ownerUserId(url));
        assertTrue(FileOwnership.uploadedBy(url, 12L));
        assertFalse(FileOwnership.uploadedBy(url, 99L));
    }

    @Test
    void stripsUrlsNotUploadedByCurrentUser() {
        String stored = "/uploads/2026-08/u1_aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee.png,"
                + "/uploads/2026-08/u2_aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee.png";
        assertEquals("/uploads/2026-08/u1_aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee.png",
                FileOwnership.keepOwned(stored, 1L));
    }
}
