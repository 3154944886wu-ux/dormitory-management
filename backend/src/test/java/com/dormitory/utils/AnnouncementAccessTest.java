package com.dormitory.utils;

import com.dormitory.model.Announcement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnouncementAccessTest {

    @Test
    void onlyPublishedIsVisibleToStudent() {
        Announcement draft = new Announcement();
        draft.setStatus(0);
        Announcement published = new Announcement();
        published.setStatus(1);
        Announcement offline = new Announcement();
        offline.setStatus(2);

        assertFalse(AnnouncementAccess.isPublished(null));
        assertFalse(AnnouncementAccess.isPublished(draft));
        assertFalse(AnnouncementAccess.isPublished(offline));
        assertTrue(AnnouncementAccess.isPublished(published));
    }
}
