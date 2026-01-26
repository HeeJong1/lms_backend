package com.lmsproject.lms_backend.mapper;

import com.lmsproject.lms_backend.model.Enrollment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EnrollmentMapper {
    List<Enrollment> findAll();
    Optional<Enrollment> findById(Long enrollmentId);
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);
    List<Enrollment> findByStatus(String status);
    int insertEnrollment(Enrollment enrollment);
    int updateEnrollment(Enrollment enrollment);
    int deleteEnrollment(Long enrollmentId);
}
