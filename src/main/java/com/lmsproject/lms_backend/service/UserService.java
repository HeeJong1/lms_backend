package com.lmsproject.lms_backend.service;

import com.lmsproject.lms_backend.mapper.UserMapper;
import com.lmsproject.lms_backend.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public Optional<User> findByUsername(String username) {
        try {
            log.debug("Finding user by username: {}", username);
            Optional<User> user = userMapper.findByUsername(username);
            log.debug("User found: {}", user.isPresent());
            return user;
        } catch (Exception e) {
            log.error("Error finding user by username: {}", username, e);
            throw e;
        }
    }

    public Optional<User> findById(Long userId) {
        return userMapper.findById(userId);
    }

    public User register(String username, String password, String name, String email, String role) {
        log.info("Register attempt for username: {}", username);

        // 입력값 검증
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("아이디를 입력해주세요.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("비밀번호를 입력해주세요.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("이름을 입력해주세요.");
        }

        // 아이디 중복 확인
        Optional<User> existingUser = userMapper.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        // 역할 기본값 설정 (STUDENT)
        if (role == null || role.trim().isEmpty()) {
            role = "STUDENT";
        }

        // 역할 유효성 검증
        if (!role.equals("STUDENT") && !role.equals("INSTRUCTOR") && !role.equals("ADMIN")) {
            throw new RuntimeException("유효하지 않은 역할입니다.");
        }

        try {
            // 새 사용자 생성
            User newUser = User.builder()
                    .username(username.trim())
                    .password(password)
                    .name(name.trim())
                    .email(email != null ? email.trim() : null)
                    .role(role)
                    .build();

            int result = userMapper.insertUser(newUser);

            if (result > 0) {
                log.info("User registered successfully: {}", username);
                // 비밀번호는 반환하지 않음
                newUser.setPassword(null);
                return newUser;
            } else {
                throw new RuntimeException("회원가입에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("Error during registration for user: {}", username, e);
            if (e.getMessage().contains("duplicate") || e.getMessage().contains("unique")) {
                throw new RuntimeException("이미 사용 중인 사용자명입니다.");
            }
            throw new RuntimeException("회원가입 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public User login(String username, String password) {
        log.info("Login attempt for username: {}", username);

        if (username == null || password == null) {
            log.warn("Username or password is null");
            return null;
        }

        try {
            Optional<User> userOpt = userMapper.findByUsername(username);

            if (userOpt.isEmpty()) {
                log.warn("User not found: {}", username);
                return null;
            }

            User user = userOpt.get();
            String dbPassword = user.getPassword();
            boolean passwordMatch = dbPassword != null && dbPassword.equals(password);

            log.info("User found: {}", user.getUsername());
            log.info("DB password length: {}, Input password length: {}",
                    dbPassword != null ? dbPassword.length() : 0,
                    password != null ? password.length() : 0);
            log.info("Password match: {}", passwordMatch);

            // 실제로는 BCrypt로 암호화된 비밀번호를 비교해야 함
            // 여기서는 간단히 평문 비교 (개발용)
            if (passwordMatch) {
                // 비밀번호는 반환하지 않음
                user.setPassword(null);
                log.info("Login successful for user: {}", username);
                return user;
            } else {
                log.warn("Password mismatch for user: {}", username);
                return null;
            }
        } catch (Exception e) {
            log.error("Error during login for user: {}", username, e);
            throw e;
        }
    }
}
