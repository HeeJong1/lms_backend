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
public class AssignmentSubmission {
    private Long submissionId;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
    private Long enrollmentId;
    private String courseName;
    private String courseCode;
    private String content;
    private String filePath;
    private String fileName;
    private Integer score;
    private String feedback;
    private String status; // SUBMITTED, GRADED, RETURNED
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
}
