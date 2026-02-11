package com.lmsproject.lms_backend.controller;

import com.lmsproject.lms_backend.model.ApiResponse;
import com.lmsproject.lms_backend.model.Attendance;
import com.lmsproject.lms_backend.service.AttendanceService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/* 출석 관리 API 컨트롤러 - 출석 조회/입력 화면 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceService attendanceService;

    /* 출석 목록 화면 - 전체 출석 조회 (app/attendance/page.tsx, app/admin/attendance/page.tsx) */
    @GetMapping
    public ApiResponse<List<Attendance>> getAllAttendance() {
        try {
            List<Attendance> attendance = attendanceService.getAllAttendance();
            return ApiResponse.success(attendance);
        } catch (Exception e) {
            return ApiResponse.error("출석 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 출석 상세 화면 (app/attendance/page.tsx) */
    @GetMapping("/{attendanceId}")
    public ApiResponse<Attendance> getAttendanceById(@PathVariable Long attendanceId) {
        try {
            return attendanceService.getAttendanceById(attendanceId)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("출석 정보를 찾을 수 없습니다."));
        } catch (Exception e) {
            return ApiResponse.error("출석 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 출석 목록 화면 - 수강신청별 조회 (app/attendance/page.tsx) */
    @GetMapping("/enrollment/{enrollmentId}")
    public ApiResponse<List<Attendance>> getAttendanceByEnrollmentId(@PathVariable Long enrollmentId) {
        try {
            List<Attendance> attendance = attendanceService.getAttendanceByEnrollmentId(enrollmentId);
            return ApiResponse.success(attendance);
        } catch (Exception e) {
            return ApiResponse.error("출석 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 출석률 조회 화면 (app/attendance/page.tsx) */
    @GetMapping("/enrollment/{enrollmentId}/rate")
    public ApiResponse<Map<String, Object>> getAttendanceRate(@PathVariable Long enrollmentId) {
        try {
            BigDecimal rate = attendanceService.calculateAttendanceRate(enrollmentId);
            return ApiResponse.success(Map.of("attendanceRate", rate));
        } catch (Exception e) {
            return ApiResponse.error("출석률 계산 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 출석 목록 화면 - 학생별 조회 (app/attendance/page.tsx) */
    @GetMapping("/student/{studentId}")
    public ApiResponse<List<Attendance>> getAttendanceByStudentId(@PathVariable Long studentId) {
        try {
            List<Attendance> attendance = attendanceService.getAttendanceByStudentId(studentId);
            return ApiResponse.success(attendance);
        } catch (Exception e) {
            return ApiResponse.error("출석 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 출석 목록 화면 - 강의별 조회 (app/admin/attendance/page.tsx) */
    @GetMapping("/course/{courseId}")
    public ApiResponse<List<Attendance>> getAttendanceByCourseId(@PathVariable Long courseId) {
        try {
            List<Attendance> attendance = attendanceService.getAttendanceByCourseId(courseId);
            return ApiResponse.success(attendance);
        } catch (Exception e) {
            return ApiResponse.error("출석 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 출석 입력 화면 (app/admin/attendance/page.tsx) */
    @PostMapping
    public ApiResponse<Attendance> createAttendance(@RequestBody CreateAttendanceRequest request) {
        try {
            Attendance attendance = attendanceService.createAttendance(
                    request.getEnrollmentId(),
                    request.getAttendanceDate(),
                    request.getStatus(),
                    request.getNotes()
            );
            return ApiResponse.success(attendance);
        } catch (Exception e) {
            return ApiResponse.error("출석 기록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 출석 수정 화면 (app/admin/attendance/page.tsx) */
    @PutMapping("/{attendanceId}")
    public ApiResponse<Attendance> updateAttendance(@PathVariable Long attendanceId, @RequestBody UpdateAttendanceRequest request) {
        try {
            Attendance attendance = attendanceService.updateAttendance(
                    attendanceId,
                    request.getStatus(),
                    request.getNotes()
            );
            return ApiResponse.success(attendance);
        } catch (Exception e) {
            return ApiResponse.error("출석 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /* 출석 삭제 화면 (app/admin/attendance/page.tsx) */
    @DeleteMapping("/{attendanceId}")
    public ApiResponse<Void> deleteAttendance(@PathVariable Long attendanceId) {
        try {
            attendanceService.deleteAttendance(attendanceId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("출석 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Data
    static class CreateAttendanceRequest {
        private Long enrollmentId;
        private LocalDate attendanceDate;
        private String status; // PRESENT, ABSENT, LATE, EXCUSED
        private String notes;
    }

    @Data
    static class UpdateAttendanceRequest {
        private String status;
        private String notes;
    }
}
