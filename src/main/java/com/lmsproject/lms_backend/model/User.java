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
public class User {
    private Long userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private String role; // STUDENT, INSTRUCTOR, ADMIN
    private Integer grade; // 학년 (1, 2, 3, 4)
    private Integer totalCredits; // 총 이수 학점
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
