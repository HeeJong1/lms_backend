package com.lmsproject.lms_backend.controller;

import com.lmsproject.lms_backend.model.ApiResponse;
import com.lmsproject.lms_backend.model.Course;
import com.lmsproject.lms_backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* 강의 관리 API 컨트롤러 - 강의 목록/상세 화면 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseController {
    
    private final CourseService courseService;
    
    /* 강의 목록 화면 (app/courses/page.tsx) */
    @GetMapping
    public ApiResponse<List<Course>> getAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            return ApiResponse.success(courses);
        } catch (Exception e) {
            return ApiResponse.error("강의 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /* 강의 상세 화면 (app/courses/page.tsx) */
    @GetMapping("/{courseId}")
    public ApiResponse<Course> getCourseById(@PathVariable Long courseId) {
        try {
            return courseService.getCourseById(courseId)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("강의를 찾을 수 없습니다."));
        } catch (Exception e) {
            return ApiResponse.error("강의 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /* 강의 목록 화면 - 강사별 강의 조회 (app/courses/page.tsx) */
    @GetMapping("/instructor/{instructorId}")
    public ApiResponse<List<Course>> getCoursesByInstructor(@PathVariable Long instructorId) {
        try {
            List<Course> courses = courseService.getCoursesByInstructor(instructorId);
            return ApiResponse.success(courses);
        } catch (Exception e) {
            return ApiResponse.error("강의 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /* 강의 생성 화면 (app/courses/create/page.tsx) */
    @PostMapping
    public ApiResponse<Course> createCourse(@RequestBody Course course) {
        try {
            Course created = courseService.createCourse(course);
            return ApiResponse.success("강의가 생성되었습니다.", created);
        } catch (Exception e) {
            return ApiResponse.error("강의 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /* 강의 수정 화면 (app/courses/create/page.tsx) */
    @PutMapping("/{courseId}")
    public ApiResponse<Course> updateCourse(@PathVariable Long courseId, @RequestBody Course course) {
        try {
            course.setCourseId(courseId);
            Course updated = courseService.updateCourse(course);
            return ApiResponse.success("강의가 수정되었습니다.", updated);
        } catch (Exception e) {
            return ApiResponse.error("강의 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
