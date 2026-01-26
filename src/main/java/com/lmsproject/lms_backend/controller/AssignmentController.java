package com.lmsproject.lms_backend.controller;

import com.lmsproject.lms_backend.model.ApiResponse;
import com.lmsproject.lms_backend.model.Assignment;
import com.lmsproject.lms_backend.model.AssignmentSubmission;
import com.lmsproject.lms_backend.service.AssignmentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssignmentController {

    private final AssignmentService assignmentService;

    // Assignment 관련 엔드포인트
    @GetMapping
    public ApiResponse<List<Assignment>> getAllAssignments() {
        try {
            List<Assignment> assignments = assignmentService.getAllAssignments();
            return ApiResponse.success(assignments);
        } catch (Exception e) {
            return ApiResponse.error("과제 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/{assignmentId}")
    public ApiResponse<Assignment> getAssignmentById(@PathVariable Long assignmentId) {
        try {
            return assignmentService.getAssignmentById(assignmentId)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("과제를 찾을 수 없습니다."));
        } catch (Exception e) {
            return ApiResponse.error("과제 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<Assignment>> getAssignmentsByCourseId(@PathVariable Long courseId) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByCourseId(courseId);
            return ApiResponse.success(assignments);
        } catch (Exception e) {
            return ApiResponse.error("과제 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/instructor/{instructorId}")
    public ApiResponse<List<Assignment>> getAssignmentsByInstructorId(@PathVariable Long instructorId) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByInstructorId(instructorId);
            return ApiResponse.success(assignments);
        } catch (Exception e) {
            return ApiResponse.error("과제 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<Assignment> createAssignment(@RequestBody CreateAssignmentRequest request) {
        try {
            Assignment assignment = assignmentService.createAssignment(
                    request.getCourseId(),
                    request.getInstructorId(),
                    request.getTitle(),
                    request.getDescription(),
                    request.getDueDate(),
                    request.getMaxScore()
            );
            return ApiResponse.success(assignment);
        } catch (Exception e) {
            return ApiResponse.error("과제 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PutMapping("/{assignmentId}")
    public ApiResponse<Assignment> updateAssignment(@PathVariable Long assignmentId, @RequestBody UpdateAssignmentRequest request) {
        try {
            Assignment assignment = assignmentService.updateAssignment(
                    assignmentId,
                    request.getTitle(),
                    request.getDescription(),
                    request.getDueDate(),
                    request.getMaxScore()
            );
            return ApiResponse.success(assignment);
        } catch (Exception e) {
            return ApiResponse.error("과제 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @DeleteMapping("/{assignmentId}")
    public ApiResponse<Void> deleteAssignment(@PathVariable Long assignmentId) {
        try {
            assignmentService.deleteAssignment(assignmentId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("과제 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // AssignmentSubmission 관련 엔드포인트
    @GetMapping("/submissions")
    public ApiResponse<List<AssignmentSubmission>> getAllSubmissions() {
        try {
            List<AssignmentSubmission> submissions = assignmentService.getAllSubmissions();
            return ApiResponse.success(submissions);
        } catch (Exception e) {
            return ApiResponse.error("제출물 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/submissions/{submissionId}")
    public ApiResponse<AssignmentSubmission> getSubmissionById(@PathVariable Long submissionId) {
        try {
            return assignmentService.getSubmissionById(submissionId)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("제출물을 찾을 수 없습니다."));
        } catch (Exception e) {
            return ApiResponse.error("제출물 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/{assignmentId}/submissions")
    public ApiResponse<List<AssignmentSubmission>> getSubmissionsByAssignmentId(@PathVariable Long assignmentId) {
        try {
            List<AssignmentSubmission> submissions = assignmentService.getSubmissionsByAssignmentId(assignmentId);
            return ApiResponse.success(submissions);
        } catch (Exception e) {
            return ApiResponse.error("제출물 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/submissions/student/{studentId}")
    public ApiResponse<List<AssignmentSubmission>> getSubmissionsByStudentId(@PathVariable Long studentId) {
        try {
            List<AssignmentSubmission> submissions = assignmentService.getSubmissionsByStudentId(studentId);
            return ApiResponse.success(submissions);
        } catch (Exception e) {
            return ApiResponse.error("제출물 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/submissions")
    public ApiResponse<AssignmentSubmission> submitAssignment(@RequestBody SubmitAssignmentRequest request) {
        try {
            AssignmentSubmission submission = assignmentService.submitAssignment(
                    request.getAssignmentId(),
                    request.getStudentId(),
                    request.getEnrollmentId(),
                    request.getContent(),
                    request.getFilePath(),
                    request.getFileName()
            );
            return ApiResponse.success(submission);
        } catch (Exception e) {
            return ApiResponse.error("과제 제출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PutMapping("/submissions/{submissionId}")
    public ApiResponse<AssignmentSubmission> updateSubmission(@PathVariable Long submissionId, @RequestBody UpdateSubmissionRequest request) {
        try {
            AssignmentSubmission submission = assignmentService.updateSubmission(
                    submissionId,
                    request.getContent(),
                    request.getFilePath(),
                    request.getFileName()
            );
            return ApiResponse.success(submission);
        } catch (Exception e) {
            return ApiResponse.error("제출물 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PutMapping("/submissions/{submissionId}/grade")
    public ApiResponse<AssignmentSubmission> gradeSubmission(@PathVariable Long submissionId, @RequestBody GradeSubmissionRequest request) {
        try {
            AssignmentSubmission submission = assignmentService.gradeSubmission(
                    submissionId,
                    request.getScore(),
                    request.getFeedback()
            );
            return ApiResponse.success(submission);
        } catch (Exception e) {
            return ApiResponse.error("과제 채점 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @DeleteMapping("/submissions/{submissionId}")
    public ApiResponse<Void> deleteSubmission(@PathVariable Long submissionId) {
        try {
            assignmentService.deleteSubmission(submissionId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("제출물 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Data
    static class CreateAssignmentRequest {
        private Long courseId;
        private Long instructorId;
        private String title;
        private String description;
        private LocalDateTime dueDate;
        private Integer maxScore;
    }

    @Data
    static class UpdateAssignmentRequest {
        private String title;
        private String description;
        private LocalDateTime dueDate;
        private Integer maxScore;
    }

    @Data
    static class SubmitAssignmentRequest {
        private Long assignmentId;
        private Long studentId;
        private Long enrollmentId;
        private String content;
        private String filePath;
        private String fileName;
    }

    @Data
    static class UpdateSubmissionRequest {
        private String content;
        private String filePath;
        private String fileName;
    }

    @Data
    static class GradeSubmissionRequest {
        private Integer score;
        private String feedback;
    }
}
