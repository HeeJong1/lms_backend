package com.lmsproject.lms_backend.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.lmsproject.lms_backend.mapper")
public class MyBatisConfig {
}
