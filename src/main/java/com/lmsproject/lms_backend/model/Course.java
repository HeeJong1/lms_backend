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
public class Course {

    private Long courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private Long instructorId;
    private String instructorName;
    private Integer maxStudents;
    private Integer currentStudents;
    private Integer credits; // 학점
    private String status; // OPEN, CLOSED, FULL
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
