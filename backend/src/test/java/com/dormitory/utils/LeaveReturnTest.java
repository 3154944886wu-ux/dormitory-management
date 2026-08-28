package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeaveReturnTest {

    @Test
    void onlyApprovedLeaveCanConfirmReturn() {
        assertTrue(LeaveReturn.canConfirm(1));
    }

    @Test
    void otherStatusesCannotConfirmReturn() {
        assertFalse(LeaveReturn.canConfirm(0));
        assertFalse(LeaveReturn.canConfirm(2));
        assertFalse(LeaveReturn.canConfirm(3));
        assertFalse(LeaveReturn.canConfirm(4));
        assertFalse(LeaveReturn.canConfirm(null));
    }
}
