package com.lmsproject.lms_backend.mapper;

import com.lmsproject.lms_backend.model.Assignment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AssignmentMapper {
    List<Assignment> findAll();
    Optional<Assignment> findById(Long assignmentId);
    List<Assignment> findByCourseId(Long courseId);
    List<Assignment> findByInstructorId(Long instructorId);
    int insertAssignment(Assignment assignment);
    int updateAssignment(Assignment assignment);
    int deleteAssignment(Long assignmentId);
}
