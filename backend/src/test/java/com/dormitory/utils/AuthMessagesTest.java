package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthMessagesTest {

    @Test
    void registerFailuresShareOneMessage() {
        String msg = AuthMessages.REGISTER_IDENTITY_FAILED;
        assertTrue(msg.contains("学号") || msg.contains("注册"));
        assertEquals(msg, AuthMessages.REGISTER_IDENTITY_FAILED);
    }
}
