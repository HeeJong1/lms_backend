package com.lmsproject.lms_backend.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    private Long enrollmentId;
    private Long studentId;
    private Long courseId;
    private String studentName;
    private String courseName;
    private String courseCode;
    private Integer credits; // 학점
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED
    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime cancelledAt;
    private String rejectionReason;
}
