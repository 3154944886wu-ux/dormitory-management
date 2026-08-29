package com.dormitory.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入住时刻是否覆盖某个归寝业务日。
 */
public final class OccupancyArrival {

    private OccupancyArrival() {
    }

    public static boolean residingOnBusinessDate(LocalDateTime checkInDate, LocalDate businessDate) {
        if (businessDate == null) {
            return false;
        }
        if (checkInDate == null) {
            return true;
        }
        return !checkInDate.toLocalDate().isAfter(businessDate);
    }
}
