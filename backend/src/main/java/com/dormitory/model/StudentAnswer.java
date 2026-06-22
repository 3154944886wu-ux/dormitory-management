package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentAnswer {
    private Long id;
    private Long studentId;
    private Long qId;
    private Long optionId;
    private LocalDateTime submitTime;

    private String studentName;
    private String questionText;
    private String optionText;
}
