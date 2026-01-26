package com.lmsproject.lms_backend.service;

import com.lmsproject.lms_backend.mapper.AssignmentMapper;
import com.lmsproject.lms_backend.mapper.AssignmentSubmissionMapper;
import com.lmsproject.lms_backend.mapper.CourseMapper;
import com.lmsproject.lms_backend.mapper.EnrollmentMapper;
import com.lmsproject.lms_backend.mapper.UserMapper;
import com.lmsproject.lms_backend.model.Assignment;
import com.lmsproject.lms_backend.model.AssignmentSubmission;
import com.lmsproject.lms_backend.model.Course;
import com.lmsproject.lms_backend.model.Enrollment;
import com.lmsproject.lms_backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentMapper assignmentMapper;
    private final AssignmentSubmissionMapper submissionMapper;
    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final UserMapper userMapper;

    // Assignment 관련 메서드
    public List<Assignment> getAllAssignments() {
        return assignmentMapper.findAll();
    }

    public Optional<Assignment> getAssignmentById(Long assignmentId) {
        return assignmentMapper.findById(assignmentId);
    }

    public List<Assignment> getAssignmentsByCourseId(Long courseId) {
        return assignmentMapper.findByCourseId(courseId);
    }

    public List<Assignment> getAssignmentsByInstructorId(Long instructorId) {
        return assignmentMapper.findByInstructorId(instructorId);
    }

    @Transactional
    public Assignment createAssignment(Long courseId, Long instructorId, String title, String description,
                                      LocalDateTime dueDate, Integer maxScore) {
        // 강의 확인
        Optional<Course> courseOpt = courseMapper.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("강의 정보를 찾을 수 없습니다.");
        }

        // 강사 확인
        Optional<User> instructorOpt = userMapper.findById(instructorId);
        if (instructorOpt.isEmpty()) {
            throw new RuntimeException("강사 정보를 찾을 수 없습니다.");
        }

        Assignment assignment = Assignment.builder()
                .courseId(courseId)
                .instructorId(instructorId)
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .maxScore(maxScore != null ? maxScore : 100)
                .build();

        assignmentMapper.insertAssignment(assignment);
        return assignmentMapper.findById(assignment.getAssignmentId()).orElseThrow();
    }

    @Transactional
    public Assignment updateAssignment(Long assignmentId, String title, String description,
                                       LocalDateTime dueDate, Integer maxScore) {
        Optional<Assignment> assignmentOpt = assignmentMapper.findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            throw new RuntimeException("과제를 찾을 수 없습니다.");
        }

        Assignment assignment = assignmentOpt.get();
        assignment.setTitle(title != null ? title : assignment.getTitle());
        assignment.setDescription(description != null ? description : assignment.getDescription());
        assignment.setDueDate(dueDate != null ? dueDate : assignment.getDueDate());
        assignment.setMaxScore(maxScore != null ? maxScore : assignment.getMaxScore());

        assignmentMapper.updateAssignment(assignment);
        return assignmentMapper.findById(assignmentId).orElseThrow();
    }

    @Transactional
    public void deleteAssignment(Long assignmentId) {
        Optional<Assignment> assignmentOpt = assignmentMapper.findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            throw new RuntimeException("과제를 찾을 수 없습니다.");
        }
        assignmentMapper.deleteAssignment(assignmentId);
    }

    // AssignmentSubmission 관련 메서드
    public List<AssignmentSubmission> getAllSubmissions() {
        return submissionMapper.findAll();
    }

    public Optional<AssignmentSubmission> getSubmissionById(Long submissionId) {
        return submissionMapper.findById(submissionId);
    }

    public Optional<AssignmentSubmission> getSubmissionByAssignmentAndStudent(Long assignmentId, Long studentId) {
        return submissionMapper.findByAssignmentIdAndStudentId(assignmentId, studentId);
    }

    public List<AssignmentSubmission> getSubmissionsByAssignmentId(Long assignmentId) {
        return submissionMapper.findByAssignmentId(assignmentId);
    }

    public List<AssignmentSubmission> getSubmissionsByStudentId(Long studentId) {
        return submissionMapper.findByStudentId(studentId);
    }

    public List<AssignmentSubmission> getSubmissionsByEnrollmentId(Long enrollmentId) {
        return submissionMapper.findByEnrollmentId(enrollmentId);
    }

    @Transactional
    public AssignmentSubmission submitAssignment(Long assignmentId, Long studentId, Long enrollmentId,
                                                 String content, String filePath, String fileName) {
        // 과제 확인
        Optional<Assignment> assignmentOpt = assignmentMapper.findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            throw new RuntimeException("과제를 찾을 수 없습니다.");
        }

        // 수강신청 확인
        Optional<Enrollment> enrollmentOpt = enrollmentMapper.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("수강신청 정보를 찾을 수 없습니다.");
        }

        Enrollment enrollment = enrollmentOpt.get();
        if (!enrollment.getStudentId().equals(studentId)) {
            throw new RuntimeException("학생 정보가 일치하지 않습니다.");
        }

        if (!"APPROVED".equals(enrollment.getStatus())) {
            throw new RuntimeException("승인된 수강신청에만 과제를 제출할 수 있습니다.");
        }

        // 이미 제출한 과제인지 확인
        Optional<AssignmentSubmission> existing = submissionMapper.findByAssignmentIdAndStudentId(assignmentId, studentId);
        if (existing.isPresent()) {
            throw new RuntimeException("이미 제출한 과제입니다.");
        }

        AssignmentSubmission submission = AssignmentSubmission.builder()
                .assignmentId(assignmentId)
                .studentId(studentId)
                .enrollmentId(enrollmentId)
                .content(content)
                .filePath(filePath)
                .fileName(fileName)
                .status("SUBMITTED")
                .build();

        submissionMapper.insertSubmission(submission);
        return submissionMapper.findById(submission.getSubmissionId()).orElseThrow();
    }

    @Transactional
    public AssignmentSubmission updateSubmission(Long submissionId, String content, String filePath, String fileName) {
        Optional<AssignmentSubmission> submissionOpt = submissionMapper.findById(submissionId);
        if (submissionOpt.isEmpty()) {
            throw new RuntimeException("제출물을 찾을 수 없습니다.");
        }

        AssignmentSubmission submission = submissionOpt.get();
        submission.setContent(content != null ? content : submission.getContent());
        submission.setFilePath(filePath != null ? filePath : submission.getFilePath());
        submission.setFileName(fileName != null ? fileName : submission.getFileName());

        submissionMapper.updateSubmission(submission);
        return submissionMapper.findById(submissionId).orElseThrow();
    }

    @Transactional
    public AssignmentSubmission gradeSubmission(Long submissionId, Integer score, String feedback) {
        Optional<AssignmentSubmission> submissionOpt = submissionMapper.findById(submissionId);
        if (submissionOpt.isEmpty()) {
            throw new RuntimeException("제출물을 찾을 수 없습니다.");
        }

        AssignmentSubmission submission = submissionOpt.get();
        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setStatus("GRADED");

        submissionMapper.updateSubmission(submission);
        return submissionMapper.findById(submissionId).orElseThrow();
    }

    @Transactional
    public void deleteSubmission(Long submissionId) {
        Optional<AssignmentSubmission> submissionOpt = submissionMapper.findById(submissionId);
        if (submissionOpt.isEmpty()) {
            throw new RuntimeException("제출물을 찾을 수 없습니다.");
        }
        submissionMapper.deleteSubmission(submissionId);
    }
}
