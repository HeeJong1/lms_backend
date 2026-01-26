package com.lmsproject.lms_backend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 Origin 설정
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://127.0.0.1:3000");

        // 허용할 HTTP 메서드 (와일드카드 사용)
        config.addAllowedMethod(CorsConfiguration.ALL);

        // 허용할 헤더
        config.addAllowedHeader(CorsConfiguration.ALL);

        // 노출할 헤더
        config.addExposedHeader(CorsConfiguration.ALL);

        // preflight 요청의 캐시 시간 (초)
        config.setMaxAge(3600L);

        // 모든 경로에 CORS 설정 적용
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
