package com.lmsproject.lms_backend.mapper;

import com.lmsproject.lms_backend.model.Course;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CourseMapper {
    List<Course> findAll();
    Optional<Course> findById(Long courseId);
    List<Course> findByInstructorId(Long instructorId);
    int insertCourse(Course course);
    int updateCourse(Course course);
    int updateCurrentStudents(Long courseId, Integer currentStudents);
}
