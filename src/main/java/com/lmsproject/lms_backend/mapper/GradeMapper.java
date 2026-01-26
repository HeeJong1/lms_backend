package com.lmsproject.lms_backend.mapper;

import com.lmsproject.lms_backend.model.Grade;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface GradeMapper {
    List<Grade> findAll();
    Optional<Grade> findById(Long gradeId);
    Optional<Grade> findByEnrollmentId(Long enrollmentId);
    List<Grade> findByStudentId(Long studentId);
    List<Grade> findByCourseId(Long courseId);
    int insertGrade(Grade grade);
    int updateGrade(Grade grade);
    int deleteGrade(Long gradeId);
}
