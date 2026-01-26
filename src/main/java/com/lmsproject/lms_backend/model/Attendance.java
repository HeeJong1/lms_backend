package com.lmsproject.lms_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    private Long attendanceId;
    private Long enrollmentId;
    private Long studentId;
    private Long courseId;
    private String studentName;
    private String courseName;
    private String courseCode;
    private LocalDate attendanceDate;
    private String status; // PRESENT, ABSENT, LATE, EXCUSED
    private String notes;
    private LocalDateTime createdAt;
}
