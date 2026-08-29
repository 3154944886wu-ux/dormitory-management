package com.dormitory.utils;

/**
 * 问卷选项热改：已有作答时禁止整表替换（FK CASCADE 会清空答案）。
 */
public final class QuestionnaireEditPolicy {

    private QuestionnaireEditPolicy() {
    }

    public static boolean canReplaceOptions(int answerCount) {
        return answerCount <= 0;
    }
}
