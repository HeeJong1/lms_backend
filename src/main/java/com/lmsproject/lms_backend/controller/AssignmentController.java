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

/* 과제 관리 API 컨트롤러 - 과제 목록/제출/채점 화면 */
@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssignmentController {

    private final AssignmentService assignmentService;

    /* 과제 목록 화면 (app/assignments/page.tsx) */
    @GetMapping
    public ApiResponse<List<Assignment>> getAllAssignments() {
        try {
            List<Assignment> assignments = assignmentService.getAllAssignments();
            return ApiResponse.success(assignments);
        } catch (Exception e) {
            return ApiResponse.error("과제 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 과제 상세 화면 (app/assignments/page.tsx) */
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

    /* 과제 목록 화면 - 강의별 조회 (app/assignments/page.tsx) */
    @GetMapping("/course/{courseId}")
    public ApiResponse<List<Assignment>> getAssignmentsByCourseId(@PathVariable Long courseId) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByCourseId(courseId);
            return ApiResponse.success(assignments);
        } catch (Exception e) {
            return ApiResponse.error("과제 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 과제 목록 화면 - 강사별 조회 (app/assignments/page.tsx) */
    @GetMapping("/instructor/{instructorId}")
    public ApiResponse<List<Assignment>> getAssignmentsByInstructorId(@PathVariable Long instructorId) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByInstructorId(instructorId);
            return ApiResponse.success(assignments);
        } catch (Exception e) {
            return ApiResponse.error("과제 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 과제 생성 화면 (app/assignments/page.tsx) */
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

    /* 과제 수정 화면 (app/assignments/page.tsx) */
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

    /* 과제 삭제 화면 (app/assignments/page.tsx) */
    @DeleteMapping("/{assignmentId}")
    public ApiResponse<Void> deleteAssignment(@PathVariable Long assignmentId) {
        try {
            assignmentService.deleteAssignment(assignmentId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("과제 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 과제 제출물 목록 화면 (app/assignments/page.tsx) */
    @GetMapping("/submissions")
    public ApiResponse<List<AssignmentSubmission>> getAllSubmissions() {
        try {
            List<AssignmentSubmission> submissions = assignmentService.getAllSubmissions();
            return ApiResponse.success(submissions);
        } catch (Exception e) {
            return ApiResponse.error("제출물 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 과제 제출물 상세 화면 (app/assignments/page.tsx) */
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

    /* 과제 제출물 목록 화면 - 과제별 조회 (app/assignments/page.tsx) */
    @GetMapping("/{assignmentId}/submissions")
    public ApiResponse<List<AssignmentSubmission>> getSubmissionsByAssignmentId(@PathVariable Long assignmentId) {
        try {
            List<AssignmentSubmission> submissions = assignmentService.getSubmissionsByAssignmentId(assignmentId);
            return ApiResponse.success(submissions);
        } catch (Exception e) {
            return ApiResponse.error("제출물 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 과제 제출물 목록 화면 - 학생별 조회 (app/assignments/page.tsx) */
    @GetMapping("/submissions/student/{studentId}")
    public ApiResponse<List<AssignmentSubmission>> getSubmissionsByStudentId(@PathVariable Long studentId) {
        try {
            List<AssignmentSubmission> submissions = assignmentService.getSubmissionsByStudentId(studentId);
            return ApiResponse.success(submissions);
        } catch (Exception e) {
            return ApiResponse.error("제출물 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 과제 제출 화면 (app/assignments/page.tsx) */
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

    /* 과제 제출물 수정 화면 (app/assignments/page.tsx) */
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

    /* 과제 채점 화면 (app/assignments/page.tsx) */
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

    /* 과제 제출물 삭제 화면 (app/assignments/page.tsx) */
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
