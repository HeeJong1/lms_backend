package com.lmsproject.lms_backend.service;

import com.lmsproject.lms_backend.mapper.CourseMapper;
import com.lmsproject.lms_backend.model.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {
    
    private final CourseMapper courseMapper;
    
    public List<Course> getAllCourses() {
        return courseMapper.findAll();
    }
    
    public Optional<Course> getCourseById(Long courseId) {
        return courseMapper.findById(courseId);
    }
    
    public List<Course> getCoursesByInstructor(Long instructorId) {
        return courseMapper.findByInstructorId(instructorId);
    }
    
    public Course createCourse(Course course) {
        course.setStatus("OPEN");
        // 학점이 없으면 기본값 3학점
        if (course.getCredits() == null || course.getCredits() == 0) {
            course.setCredits(3);
        }
        courseMapper.insertCourse(course);
        return course;
    }
    
    public Course updateCourse(Course course) {
        courseMapper.updateCourse(course);
        return course;
    }
}
