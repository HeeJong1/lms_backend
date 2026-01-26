package com.lmsproject.lms_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String name;
    private String email;
    private String role; // STUDENT, INSTRUCTOR, ADMIN (기본값: STUDENT)
}
