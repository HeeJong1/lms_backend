package com.lmsproject.lms_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    private Long gradeId;
    private Long enrollmentId;
    private Long studentId;
    private Long courseId;
    private String studentName;
    private String courseName;
    private String courseCode;
    private Integer midtermScore;
    private Integer finalScore;
    private Integer assignmentScore;
    private Integer attendanceScore;
    private BigDecimal totalScore;
    private String letterGrade;
    private BigDecimal gpa;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
