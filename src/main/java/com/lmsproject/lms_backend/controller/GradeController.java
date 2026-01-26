package com.lmsproject.lms_backend.controller;

import com.lmsproject.lms_backend.model.ApiResponse;
import com.lmsproject.lms_backend.model.Grade;
import com.lmsproject.lms_backend.service.GradeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GradeController {

    private final GradeService gradeService;

    @GetMapping
    public ApiResponse<List<Grade>> getAllGrades() {
        try {
            List<Grade> grades = gradeService.getAllGrades();
            return ApiResponse.success(grades);
        } catch (Exception e) {
            return ApiResponse.error("성적 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/{gradeId}")
    public ApiResponse<Grade> getGradeById(@PathVariable Long gradeId) {
        try {
            return gradeService.getGradeById(gradeId)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("성적을 찾을 수 없습니다."));
        } catch (Exception e) {
            return ApiResponse.error("성적 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public ApiResponse<Grade> getGradeByEnrollmentId(@PathVariable Long enrollmentId) {
        try {
            return gradeService.getGradeByEnrollmentId(enrollmentId)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("성적을 찾을 수 없습니다."));
        } catch (Exception e) {
            return ApiResponse.error("성적 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<Grade>> getGradesByStudentId(@PathVariable Long studentId) {
        try {
            List<Grade> grades = gradeService.getGradesByStudentId(studentId);
            return ApiResponse.success(grades);
        } catch (Exception e) {
            return ApiResponse.error("성적 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<Grade>> getGradesByCourseId(@PathVariable Long courseId) {
        try {
            List<Grade> grades = gradeService.getGradesByCourseId(courseId);
            return ApiResponse.success(grades);
        } catch (Exception e) {
            return ApiResponse.error("성적 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<Grade> createGrade(@RequestBody CreateGradeRequest request) {
        try {
            Grade grade = gradeService.createGrade(
                    request.getEnrollmentId(),
                    request.getMidtermScore(),
                    request.getFinalScore(),
                    request.getAssignmentScore(),
                    request.getAttendanceScore(),
                    request.getRemarks()
            );
            return ApiResponse.success(grade);
        } catch (Exception e) {
            return ApiResponse.error("성적 입력 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PutMapping("/{gradeId}")
    public ApiResponse<Grade> updateGrade(@PathVariable Long gradeId, @RequestBody UpdateGradeRequest request) {
        try {
            Grade grade = gradeService.updateGrade(
                    gradeId,
                    request.getMidtermScore(),
                    request.getFinalScore(),
                    request.getAssignmentScore(),
                    request.getAttendanceScore(),
                    request.getRemarks()
            );
            return ApiResponse.success(grade);
        } catch (Exception e) {
            return ApiResponse.error("성적 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @DeleteMapping("/{gradeId}")
    public ApiResponse<Void> deleteGrade(@PathVariable Long gradeId) {
        try {
            gradeService.deleteGrade(gradeId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("성적 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Data
    static class CreateGradeRequest {
        private Long enrollmentId;
        private Integer midtermScore;
        private Integer finalScore;
        private Integer assignmentScore;
        private Integer attendanceScore;
        private String remarks;
    }

    @Data
    static class UpdateGradeRequest {
        private Integer midtermScore;
        private Integer finalScore;
        private Integer assignmentScore;
        private Integer attendanceScore;
        private String remarks;
    }
}
