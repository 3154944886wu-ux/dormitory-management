package com.dormitory.utils;

import com.dormitory.model.Announcement;

public final class AnnouncementAccess {

    private AnnouncementAccess() {
    }

    public static boolean isPublished(Announcement announcement) {
        return announcement != null && Integer.valueOf(1).equals(announcement.getStatus());
    }
}
