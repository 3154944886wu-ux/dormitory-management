package com.dormitory.service;

import com.dormitory.mapper.QuestionnaireMapper;
import com.dormitory.mapper.QuestionOptionMapper;
import com.dormitory.mapper.StudentAnswerMapper;
import com.dormitory.model.Questionnaire;
import com.dormitory.model.QuestionOption;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionnaireService {

    private final QuestionnaireMapper questionnaireMapper;
    private final QuestionOptionMapper optionMapper;
    private final StudentAnswerMapper studentAnswerMapper;

    public QuestionnaireService(QuestionnaireMapper questionnaireMapper,
                                QuestionOptionMapper optionMapper,
                                StudentAnswerMapper studentAnswerMapper) {
        this.questionnaireMapper = questionnaireMapper;
        this.optionMapper = optionMapper;
        this.studentAnswerMapper = studentAnswerMapper;
    }

    public List<Questionnaire> findAll() {
        return questionnaireMapper.findAll();
    }

    public List<Questionnaire> findByIsActive(Integer isActive) {
        return questionnaireMapper.findByIsActive(isActive);
    }

    public Questionnaire findById(Long id) {
        Questionnaire q = questionnaireMapper.findById(id);
        if (q != null) {
            q.setOptions(optionMapper.findByQId(id));
        }
        return q;
    }

    public Questionnaire getWithOptions(Long id) {
        return findById(id);
    }

    public List<Questionnaire> findAllWithOptions() {
        List<Questionnaire> questions = questionnaireMapper.findByIsActive(1);
        for (Questionnaire q : questions) {
            q.setOptions(optionMapper.findByQId(q.getId()));
        }
        return questions;
    }

    @Transactional
    public Questionnaire createWithOptions(Questionnaire questionnaire, List<QuestionOption> options) {
        if (questionnaire.getQuestionText() == null || questionnaire.getQuestionText().isBlank()) {
            throw new RuntimeException("题目内容不能为空");
        }
        if (questionnaire.getWeight() == null) {
            questionnaire.setWeight(1);
        }
        if (questionnaire.getIsRequired() == null) {
            questionnaire.setIsRequired(1);
        }
        if (questionnaire.getIsActive() == null) {
            questionnaire.setIsActive(1);
        }

        questionnaireMapper.insert(questionnaire);

        for (QuestionOption option : options) {
            option.setQId(questionnaire.getId());
            optionMapper.insert(option);
        }

        return getWithOptions(questionnaire.getId());
    }

    @Transactional
    public Questionnaire updateWithOptions(Long id, Questionnaire questionnaire, List<QuestionOption> options) {
        Questionnaire existing = questionnaireMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("题目不存在");
        }

        if (questionnaire.getQuestionText() != null) {
            questionnaire.setId(id);
            questionnaireMapper.update(questionnaire);
        }

        if (options != null) {
            optionMapper.deleteByQId(id);
            for (QuestionOption option : options) {
                option.setQId(id);
                optionMapper.insert(option);
            }
        }

        return getWithOptions(id);
    }

    @Transactional
    public Questionnaire updateQuestionStatus(Long id, Integer isActive) {
        Questionnaire existing = questionnaireMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("题目不存在");
        }
        existing.setIsActive(isActive);
        questionnaireMapper.update(existing);
        return getWithOptions(id);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        Questionnaire existing = questionnaireMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("题目不存在");
        }
        int answerCount = studentAnswerMapper.countByQId(id);
        if (answerCount > 0) {
            throw new RuntimeException("该题目已有 " + answerCount + " 个学生作答，无法删除，请先停用");
        }
        optionMapper.deleteByQId(id);
        questionnaireMapper.deleteById(id);
    }

    public List<QuestionOption> getOptions(Long qId) {
        return optionMapper.findByQId(qId);
    }

    @Transactional
    public QuestionOption addOption(QuestionOption option) {
        optionMapper.insert(option);
        return option;
    }

    @Transactional
    public QuestionOption updateOption(QuestionOption option) {
        optionMapper.update(option);
        return option;
    }

    @Transactional
    public void deleteOption(Long optionId) {
        optionMapper.deleteById(optionId);
    }
}
