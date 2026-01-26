package com.lmsproject.lms_backend.mapper;

import com.lmsproject.lms_backend.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long userId);
    int insertUser(User user);
    int updateUser(User user);
}
