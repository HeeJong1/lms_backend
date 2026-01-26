package com.lmsproject.lms_backend.controller;

import com.lmsproject.lms_backend.mapper.UserMapper;
import com.lmsproject.lms_backend.model.ApiResponse;
import com.lmsproject.lms_backend.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestController {

    private final UserMapper userMapper;
    private final DataSource dataSource;

    @GetMapping("/db")
    public ApiResponse<Map<String, Object>> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 데이터베이스 연결 테스트
            try (Connection conn = dataSource.getConnection()) {
                result.put("status", "success");
                result.put("message", "데이터베이스 연결 성공");
                result.put("database", conn.getCatalog());
                result.put("url", conn.getMetaData().getURL());

                // 사용자 조회 테스트
                try {
                    var testUser = userMapper.findByUsername("student1");
                    result.put("userTest", testUser.isPresent() ? "사용자 조회 성공" : "사용자 없음");
                    if (testUser.isPresent()) {
                        User user = testUser.get();
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("username", user.getUsername());
                        userInfo.put("name", user.getName());
                        userInfo.put("role", user.getRole());
                        userInfo.put("passwordLength", user.getPassword() != null ? user.getPassword().length() : 0);
                        result.put("testUserInfo", userInfo);
                    }
                } catch (Exception e) {
                    result.put("userTest", "사용자 조회 실패: " + e.getMessage());
                }

                log.info("Database test successful");
                return ApiResponse.success(result);
            }
        } catch (Exception e) {
            log.error("Database test failed", e);
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("error", e.getClass().getName());
            if (e.getCause() != null) {
                result.put("cause", e.getCause().getMessage());
            }
            return ApiResponse.error("데이터베이스 연결 실패: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 모든 샘플 사용자 조회
            List<String> usernames = List.of("student1", "student2", "instructor1", "instructor2", "admin");
            List<Map<String, Object>> users = usernames.stream()
                    .map(username -> {
                        Map<String, Object> userInfo = new HashMap<>();
                        try {
                            var userOpt = userMapper.findByUsername(username);
                            if (userOpt.isPresent()) {
                                User user = userOpt.get();
                                userInfo.put("username", user.getUsername());
                                userInfo.put("name", user.getName());
                                userInfo.put("role", user.getRole());
                                userInfo.put("email", user.getEmail());
                                userInfo.put("passwordLength", user.getPassword() != null ? user.getPassword().length() : 0);
                                userInfo.put("exists", true);
                            } else {
                                userInfo.put("username", username);
                                userInfo.put("exists", false);
                            }
                        } catch (Exception e) {
                            userInfo.put("username", username);
                            userInfo.put("exists", false);
                            userInfo.put("error", e.getMessage());
                        }
                        return userInfo;
                    })
                    .collect(Collectors.toList());

            result.put("users", users);
            result.put("total", users.size());
            result.put("existing", users.stream().filter(u -> Boolean.TRUE.equals(u.get("exists"))).count());

            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("Failed to get users", e);
            result.put("error", e.getMessage());
            return ApiResponse.error("사용자 목록 조회 실패: " + e.getMessage());
        }
    }
}
