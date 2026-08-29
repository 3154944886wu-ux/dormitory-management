package com.dormitory.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionnaireEditPolicyTest {

    @Test
    void cannotReplaceOptionsWhenAnswersExist() {
        assertFalse(QuestionnaireEditPolicy.canReplaceOptions(3));
        assertTrue(QuestionnaireEditPolicy.canReplaceOptions(0));
    }
}
