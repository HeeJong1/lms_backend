package com.lmsproject.lms_backend.service;

import com.lmsproject.lms_backend.mapper.AttendanceMapper;
import com.lmsproject.lms_backend.mapper.EnrollmentMapper;
import com.lmsproject.lms_backend.model.Attendance;
import com.lmsproject.lms_backend.model.Enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceMapper attendanceMapper;
    private final EnrollmentMapper enrollmentMapper;

    public List<Attendance> getAllAttendance() {
        return attendanceMapper.findAll();
    }

    public Optional<Attendance> getAttendanceById(Long attendanceId) {
        return attendanceMapper.findById(attendanceId);
    }

    public List<Attendance> getAttendanceByEnrollmentId(Long enrollmentId) {
        return attendanceMapper.findByEnrollmentId(enrollmentId);
    }

    public List<Attendance> getAttendanceByStudentId(Long studentId) {
        return attendanceMapper.findByStudentId(studentId);
    }

    public List<Attendance> getAttendanceByCourseId(Long courseId) {
        return attendanceMapper.findByCourseId(courseId);
    }

    public BigDecimal calculateAttendanceRate(Long enrollmentId) {
        int totalClasses = attendanceMapper.countByEnrollmentId(enrollmentId);
        if (totalClasses == 0) {
            return BigDecimal.valueOf(100.00);
        }

        int presentCount = attendanceMapper.countByEnrollmentIdAndStatus(enrollmentId, "PRESENT") +
                           attendanceMapper.countByEnrollmentIdAndStatus(enrollmentId, "EXCUSED");

        BigDecimal rate = BigDecimal.valueOf(presentCount)
                .divide(BigDecimal.valueOf(totalClasses), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        return rate;
    }

    @Transactional
    public Attendance createAttendance(Long enrollmentId, LocalDate attendanceDate, String status, String notes) {
        // 수강신청 정보 확인
        Optional<Enrollment> enrollmentOpt = enrollmentMapper.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("수강신청 정보를 찾을 수 없습니다.");
        }

        Enrollment enrollment = enrollmentOpt.get();
        if (!"APPROVED".equals(enrollment.getStatus())) {
            throw new RuntimeException("승인된 수강신청에만 출석을 기록할 수 있습니다.");
        }

        // 같은 날짜에 이미 출석 기록이 있는지 확인
        Optional<Attendance> existing = attendanceMapper.findByEnrollmentIdAndDate(enrollmentId, attendanceDate);
        if (existing.isPresent()) {
            throw new RuntimeException("해당 날짜에 이미 출석 기록이 있습니다.");
        }

        if (!List.of("PRESENT", "ABSENT", "LATE", "EXCUSED").contains(status)) {
            throw new RuntimeException("유효하지 않은 출석 상태입니다.");
        }

        Attendance attendance = Attendance.builder()
                .enrollmentId(enrollmentId)
                .studentId(enrollment.getStudentId())
                .courseId(enrollment.getCourseId())
                .attendanceDate(attendanceDate)
                .status(status)
                .notes(notes)
                .build();

        attendanceMapper.insertAttendance(attendance);
        return attendanceMapper.findById(attendance.getAttendanceId()).orElseThrow();
    }

    @Transactional
    public Attendance updateAttendance(Long attendanceId, String status, String notes) {
        Optional<Attendance> attendanceOpt = attendanceMapper.findById(attendanceId);
        if (attendanceOpt.isEmpty()) {
            throw new RuntimeException("출석 정보를 찾을 수 없습니다.");
        }

        if (!List.of("PRESENT", "ABSENT", "LATE", "EXCUSED").contains(status)) {
            throw new RuntimeException("유효하지 않은 출석 상태입니다.");
        }

        Attendance attendance = attendanceOpt.get();
        attendance.setStatus(status);
        attendance.setNotes(notes);

        attendanceMapper.updateAttendance(attendance);
        return attendanceMapper.findById(attendanceId).orElseThrow();
    }

    @Transactional
    public void deleteAttendance(Long attendanceId) {
        Optional<Attendance> attendanceOpt = attendanceMapper.findById(attendanceId);
        if (attendanceOpt.isEmpty()) {
            throw new RuntimeException("출석 정보를 찾을 수 없습니다.");
        }
        attendanceMapper.deleteAttendance(attendanceId);
    }
}
