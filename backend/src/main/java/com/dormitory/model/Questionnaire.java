package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Questionnaire {
    private Long id;
    private String questionText;
    private String questionType;
    private Integer isRequired;
    private Integer weight;
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<QuestionOption> options;
}
