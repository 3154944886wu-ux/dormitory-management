package com.dormitory.model;

import lombok.Data;

@Data
public class QuestionOption {
    private Long id;
    private Long qId;
    private String optionText;
    private Integer optionValue;
    private Boolean selected;
}
