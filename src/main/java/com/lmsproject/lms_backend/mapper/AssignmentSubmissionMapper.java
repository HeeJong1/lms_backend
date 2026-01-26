package com.lmsproject.lms_backend.mapper;

import com.lmsproject.lms_backend.model.AssignmentSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AssignmentSubmissionMapper {
    List<AssignmentSubmission> findAll();
    Optional<AssignmentSubmission> findById(Long submissionId);
    Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(@Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId);
    List<AssignmentSubmission> findByAssignmentId(Long assignmentId);
    List<AssignmentSubmission> findByStudentId(Long studentId);
    List<AssignmentSubmission> findByEnrollmentId(Long enrollmentId);
    int insertSubmission(AssignmentSubmission submission);
    int updateSubmission(AssignmentSubmission submission);
    int deleteSubmission(Long submissionId);
    int countByAssignmentId(Long assignmentId);
    int countByAssignmentIdAndStatus(@Param("assignmentId") Long assignmentId, @Param("status") String status);
}
