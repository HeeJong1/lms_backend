package com.lmsproject.lms_backend.controller;

import com.lmsproject.lms_backend.model.ApiResponse;
import com.lmsproject.lms_backend.model.LoginRequest;
import com.lmsproject.lms_backend.model.LoginResponse;
import com.lmsproject.lms_backend.model.RegisterRequest;
import com.lmsproject.lms_backend.model.User;
import com.lmsproject.lms_backend.service.UserService;
import com.lmsproject.lms_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/* 인증 관련 API 컨트롤러 - 로그인/회원가입 화면 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    /* 로그인 화면 (app/login/page.tsx) */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("Login request received: username={}", request != null ? request.getUsername() : "null");
        
        try {
            if (request == null || request.getUsername() == null || request.getPassword() == null) {
                log.warn("Invalid login request: request is null or missing credentials");
                return ApiResponse.error("아이디와 비밀번호를 입력해주세요.");
            }
            
            log.debug("Attempting login for user: {}", request.getUsername());
            User user = userService.login(request.getUsername(), request.getPassword());
            
            if (user != null) {
                log.info("Login successful for user: {}", request.getUsername());
                
                // JWT 토큰 생성
                String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
                
                LoginResponse loginResponse = LoginResponse.builder()
                        .token(token)
                        .user(user)
                        .build();
                
                return ApiResponse.success("로그인 성공", loginResponse);
            } else {
                log.warn("Login failed for user: {}", request.getUsername());
                return ApiResponse.error("아이디 또는 비밀번호가 올바르지 않습니다.");
            }
        } catch (Exception e) {
            log.error("Login error for user: {}", request != null ? request.getUsername() : "unknown", e);
            return ApiResponse.error("로그인 중 오류가 발생했습니다: " + e.getMessage() + 
                    (e.getCause() != null ? " (" + e.getCause().getMessage() + ")" : ""));
        }
    }
    
    /* 회원가입 화면 (app/register/page.tsx) */
    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody RegisterRequest request) {
        log.info("Register request received: username={}", request != null ? request.getUsername() : "null");
        
        try {
            if (request == null) {
                log.warn("Invalid register request: request is null");
                return ApiResponse.error("회원가입 정보를 입력해주세요.");
            }
            
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ApiResponse.error("아이디를 입력해주세요.");
            }
            
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ApiResponse.error("비밀번호를 입력해주세요.");
            }
            
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ApiResponse.error("이름을 입력해주세요.");
            }
            
            log.debug("Attempting registration for user: {}", request.getUsername());
            User user = userService.register(
                    request.getUsername(),
                    request.getPassword(),
                    request.getName(),
                    request.getEmail(),
                    request.getRole() != null ? request.getRole() : "STUDENT"
            );
            
            if (user != null) {
                log.info("Registration successful for user: {}", request.getUsername());
                return ApiResponse.success("회원가입이 완료되었습니다.", user);
            } else {
                log.warn("Registration failed for user: {}", request.getUsername());
                return ApiResponse.error("회원가입에 실패했습니다.");
            }
        } catch (RuntimeException e) {
            log.warn("Registration failed for user: {}", request != null ? request.getUsername() : "unknown", e);
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("Registration error for user: {}", request != null ? request.getUsername() : "unknown", e);
            return ApiResponse.error("회원가입 중 오류가 발생했습니다: " + e.getMessage() + 
                    (e.getCause() != null ? " (" + e.getCause().getMessage() + ")" : ""));
        }
    }
}
