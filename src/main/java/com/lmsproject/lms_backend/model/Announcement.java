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
public class Announcement {
    private Long announcementId;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private Long authorId;
    private String authorName;
    private String title;
    private String content;
    private Boolean isImportant;
    private String targetRole; // STUDENT, INSTRUCTOR, ALL, null=전체
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
