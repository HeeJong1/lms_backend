package com.lmsproject.lms_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    private Long assignmentId;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private Long instructorId;
    private String instructorName;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer maxScore;
    private Integer submissionCount;
    private Integer gradedCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
