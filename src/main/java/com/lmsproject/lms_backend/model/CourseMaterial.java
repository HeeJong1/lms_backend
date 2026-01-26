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
public class CourseMaterial {
    private Long materialId;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private Long uploaderId;
    private String uploaderName;
    private String title;
    private String description;
    private String filePath;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String category; // 강의자료, 참고자료, 기타
    private Integer downloadCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
