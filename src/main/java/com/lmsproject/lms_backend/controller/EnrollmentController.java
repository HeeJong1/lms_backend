package com.lmsproject.lms_backend.controller;

import com.lmsproject.lms_backend.model.ApiResponse;
import com.lmsproject.lms_backend.model.Enrollment;
import com.lmsproject.lms_backend.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EnrollmentController {
    
    private final EnrollmentService enrollmentService;
    
    @GetMapping
    public ApiResponse<List<Enrollment>> getAllEnrollments() {
        try {
            List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
            return ApiResponse.success(enrollments);
        } catch (Exception e) {
            return ApiResponse.error("수강신청 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @GetMapping("/{enrollmentId}")
    public ApiResponse<Enrollment> getEnrollmentById(@PathVariable Long enrollmentId) {
        try {
            return enrollmentService.getEnrollmentById(enrollmentId)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("수강신청을 찾을 수 없습니다."));
        } catch (Exception e) {
            return ApiResponse.error("수강신청 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @GetMapping("/student/{studentId}")
    public ApiResponse<List<Enrollment>> getEnrollmentsByStudent(@PathVariable Long studentId) {
        try {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
            return ApiResponse.success(enrollments);
        } catch (Exception e) {
            return ApiResponse.error("수강신청 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @GetMapping("/course/{courseId}")
    public ApiResponse<List<Enrollment>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        try {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
            return ApiResponse.success(enrollments);
        } catch (Exception e) {
            return ApiResponse.error("수강신청 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @GetMapping("/status/{status}")
    public ApiResponse<List<Enrollment>> getEnrollmentsByStatus(@PathVariable String status) {
        try {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStatus(status);
            return ApiResponse.success(enrollments);
        } catch (Exception e) {
            return ApiResponse.error("수강신청 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @PostMapping("/apply")
    public ApiResponse<Enrollment> applyEnrollment(@RequestBody Map<String, Long> request) {
        try {
            Long studentId = request.get("studentId");
            Long courseId = request.get("courseId");
            
            if (studentId == null || courseId == null) {
                return ApiResponse.error("학생 ID와 강의 ID가 필요합니다.");
            }
            
            Enrollment enrollment = enrollmentService.applyEnrollment(studentId, courseId);
            return ApiResponse.success("수강신청이 완료되었습니다.", enrollment);
        } catch (Exception e) {
            return ApiResponse.error("수강신청 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @PostMapping("/{enrollmentId}/approve")
    public ApiResponse<Enrollment> approveEnrollment(@PathVariable Long enrollmentId) {
        try {
            Enrollment enrollment = enrollmentService.approveEnrollment(enrollmentId);
            return ApiResponse.success("수강신청이 승인되었습니다.", enrollment);
        } catch (Exception e) {
            return ApiResponse.error("수강신청 승인 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @PostMapping("/{enrollmentId}/reject")
    public ApiResponse<Enrollment> rejectEnrollment(
            @PathVariable Long enrollmentId,
            @RequestBody Map<String, String> request) {
        try {
            String rejectionReason = request.getOrDefault("rejectionReason", "사유 없음");
            Enrollment enrollment = enrollmentService.rejectEnrollment(enrollmentId, rejectionReason);
            return ApiResponse.success("수강신청이 반려되었습니다.", enrollment);
        } catch (Exception e) {
            return ApiResponse.error("수강신청 반려 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @PostMapping("/{enrollmentId}/cancel")
    public ApiResponse<Enrollment> cancelEnrollment(
            @PathVariable Long enrollmentId,
            @RequestBody Map<String, Long> request) {
        try {
            Long studentId = request.get("studentId");
            
            if (studentId == null) {
                return ApiResponse.error("학생 ID가 필요합니다.");
            }
            
            Enrollment enrollment = enrollmentService.cancelEnrollment(enrollmentId, studentId);
            return ApiResponse.success("수강신청이 취소되었습니다.", enrollment);
        } catch (Exception e) {
            return ApiResponse.error("수강신청 취소 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @PostMapping("/batch/approve")
    public ApiResponse<List<Enrollment>> approveEnrollmentsBatch(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> enrollmentIds = (List<Integer>) request.get("enrollmentIds");
            
            if (enrollmentIds == null || enrollmentIds.isEmpty()) {
                return ApiResponse.error("승인할 수강신청을 선택해주세요.");
            }
            
            List<Enrollment> approvedEnrollments = new java.util.ArrayList<>();
            List<String> errors = new java.util.ArrayList<>();
            
            for (Integer id : enrollmentIds) {
                try {
                    Enrollment enrollment = enrollmentService.approveEnrollment(id.longValue());
                    approvedEnrollments.add(enrollment);
                } catch (Exception e) {
                    errors.add("ID " + id + ": " + e.getMessage());
                }
            }
            
            if (approvedEnrollments.isEmpty()) {
                return ApiResponse.error("승인된 수강신청이 없습니다: " + String.join(", ", errors));
            }
            
            String message = approvedEnrollments.size() + "건의 수강신청이 승인되었습니다.";
            if (!errors.isEmpty()) {
                message += " (" + errors.size() + "건 실패)";
            }
            
            return ApiResponse.success(message, approvedEnrollments);
        } catch (Exception e) {
            return ApiResponse.error("일괄 승인 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @PostMapping("/batch/reject")
    public ApiResponse<List<Enrollment>> rejectEnrollmentsBatch(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> enrollmentIds = (List<Integer>) request.get("enrollmentIds");
            String rejectionReason = (String) request.getOrDefault("rejectionReason", "사유 없음");
            
            if (enrollmentIds == null || enrollmentIds.isEmpty()) {
                return ApiResponse.error("반려할 수강신청을 선택해주세요.");
            }
            
            List<Enrollment> rejectedEnrollments = new java.util.ArrayList<>();
            List<String> errors = new java.util.ArrayList<>();
            
            for (Integer id : enrollmentIds) {
                try {
                    Enrollment enrollment = enrollmentService.rejectEnrollment(id.longValue(), rejectionReason);
                    rejectedEnrollments.add(enrollment);
                } catch (Exception e) {
                    errors.add("ID " + id + ": " + e.getMessage());
                }
            }
            
            if (rejectedEnrollments.isEmpty()) {
                return ApiResponse.error("반려된 수강신청이 없습니다: " + String.join(", ", errors));
            }
            
            String message = rejectedEnrollments.size() + "건의 수강신청이 반려되었습니다.";
            if (!errors.isEmpty()) {
                message += " (" + errors.size() + "건 실패)";
            }
            
            return ApiResponse.success(message, rejectedEnrollments);
        } catch (Exception e) {
            return ApiResponse.error("일괄 반려 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
