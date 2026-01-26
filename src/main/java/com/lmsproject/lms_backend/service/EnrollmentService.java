package com.lmsproject.lms_backend.service;

import com.lmsproject.lms_backend.mapper.CourseMapper;
import com.lmsproject.lms_backend.mapper.EnrollmentMapper;
import com.lmsproject.lms_backend.model.Course;
import com.lmsproject.lms_backend.model.Enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;
    private final CourseMapper courseMapper;

    public List<Enrollment> getAllEnrollments() {
        return enrollmentMapper.findAll();
    }

    public Optional<Enrollment> getEnrollmentById(Long enrollmentId) {
        return enrollmentMapper.findById(enrollmentId);
    }

    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentMapper.findByStudentId(studentId);
    }

    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentMapper.findByCourseId(courseId);
    }

    public List<Enrollment> getEnrollmentsByStatus(String status) {
        return enrollmentMapper.findByStatus(status);
    }

    @Transactional
    public Enrollment applyEnrollment(Long studentId, Long courseId) {
        // 이미 신청한 강의인지 확인
        Optional<Enrollment> existing = enrollmentMapper.findByStudentIdAndCourseId(studentId, courseId);
        if (existing.isPresent()) {
            throw new RuntimeException("이미 신청한 강의입니다.");
        }

        // 강의 정보 확인
        Optional<Course> courseOpt = courseMapper.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("강의를 찾을 수 없습니다.");
        }

        Course course = courseOpt.get();

        // 강의 정원 확인
        if ("FULL".equals(course.getStatus())
                || course.getCurrentStudents() >= course.getMaxStudents()) {
            throw new RuntimeException("강의 정원이 가득 찼습니다.");
        }

        // 학점 확인: 현재 학기 신청 학점 + 신청할 강의 학점이 18학점을 초과하는지 확인
        int courseCredits = course.getCredits() != null ? course.getCredits() : 3;
        int currentSemesterCredits = calculateCurrentSemesterCredits(studentId);

        if (currentSemesterCredits + courseCredits > 18) {
            throw new RuntimeException(
                    String.format("한 학기 최대 18학점까지 신청 가능합니다. 현재 신청 학점: %d학점, 신청할 강의: %d학점",
                            currentSemesterCredits, courseCredits)
            );
        }

        // 수강신청 생성
        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .status("PENDING")
                .credits(courseCredits)
                .build();

        enrollmentMapper.insertEnrollment(enrollment);
        return enrollmentMapper.findById(enrollment.getEnrollmentId()).orElse(enrollment);
    }

    /**
     * 현재 학기 신청 학점 계산 (대기중 + 승인된 강의)
     */
    private int calculateCurrentSemesterCredits(Long studentId) {
        List<Enrollment> currentSemesterEnrollments = enrollmentMapper.findByStudentId(studentId)
                .stream()
                .filter(e -> e.getStatus().equals("PENDING") || e.getStatus().equals("APPROVED"))
                .filter(e -> {
                    // 현재 학기: 올해 1월~6월은 1학기, 7월~12월은 2학기로 가정
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();
                    java.time.LocalDateTime appliedAt = e.getAppliedAt();
                    if (appliedAt == null) {
                        return false;
                    }

                    int currentMonth = now.getMonthValue();
                    int appliedMonth = appliedAt.getMonthValue();
                    int currentYear = now.getYear();
                    int appliedYear = appliedAt.getYear();

                    // 같은 연도이고 같은 학기인지 확인
                    boolean sameSemester = false;
                    if (currentYear == appliedYear) {
                        if (currentMonth >= 1 && currentMonth <= 6 && appliedMonth >= 1 && appliedMonth <= 6) {
                            sameSemester = true;
                        } else if (currentMonth >= 7 && currentMonth <= 12 && appliedMonth >= 7 && appliedMonth <= 12) {
                            sameSemester = true;
                        }
                    }
                    return sameSemester;
                })
                .collect(java.util.stream.Collectors.toList());

        return currentSemesterEnrollments.stream()
                .mapToInt(e -> e.getCredits() != null ? e.getCredits() : 0)
                .sum();
    }

    @Transactional
    public Enrollment approveEnrollment(Long enrollmentId) {
        Optional<Enrollment> enrollmentOpt = enrollmentMapper.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("수강신청을 찾을 수 없습니다.");
        }

        Enrollment enrollment = enrollmentOpt.get();

        if (!"PENDING".equals(enrollment.getStatus())) {
            throw new RuntimeException("승인 대기 중인 신청만 승인할 수 있습니다.");
        }

        // 강의 정원 확인
        Optional<Course> courseOpt = courseMapper.findById(enrollment.getCourseId());
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("강의를 찾을 수 없습니다.");
        }

        Course course = courseOpt.get();
        if (course.getCurrentStudents() >= course.getMaxStudents()) {
            throw new RuntimeException("강의 정원이 가득 찼습니다.");
        }

        // 수강신청 승인
        enrollment.setStatus("APPROVED");
        enrollmentMapper.updateEnrollment(enrollment);

        // 강의 현재 학생 수 증가 (트리거가 있지만 확실하게 업데이트)
        int newCurrentStudents = course.getCurrentStudents() + 1;
        courseMapper.updateCurrentStudents(course.getCourseId(), newCurrentStudents);

        // 정원이 가득 찼는지 확인하여 상태 업데이트
        if (newCurrentStudents >= course.getMaxStudents()) {
            course.setStatus("FULL");
            courseMapper.updateCourse(course);
        }

        return enrollmentMapper.findById(enrollmentId).orElse(enrollment);
    }

    @Transactional
    public Enrollment rejectEnrollment(Long enrollmentId, String rejectionReason) {
        Optional<Enrollment> enrollmentOpt = enrollmentMapper.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("수강신청을 찾을 수 없습니다.");
        }

        Enrollment enrollment = enrollmentOpt.get();

        if (!"PENDING".equals(enrollment.getStatus())) {
            throw new RuntimeException("승인 대기 중인 신청만 반려할 수 있습니다.");
        }

        // 수강신청 반려
        enrollment.setStatus("REJECTED");
        enrollment.setRejectionReason(rejectionReason);
        enrollmentMapper.updateEnrollment(enrollment);

        return enrollmentMapper.findById(enrollmentId).orElse(enrollment);
    }

    @Transactional
    public Enrollment cancelEnrollment(Long enrollmentId, Long studentId) {
        Optional<Enrollment> enrollmentOpt = enrollmentMapper.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("수강신청을 찾을 수 없습니다.");
        }

        Enrollment enrollment = enrollmentOpt.get();

        // 본인의 신청만 취소 가능
        if (!enrollment.getStudentId().equals(studentId)) {
            throw new RuntimeException("본인의 수강신청만 취소할 수 있습니다.");
        }

        // 승인된 신청만 취소 가능 (대기 중인 것은 삭제)
        if ("APPROVED".equals(enrollment.getStatus())) {
            enrollment.setStatus("CANCELLED");
            enrollmentMapper.updateEnrollment(enrollment);

            // 강의 현재 학생 수 감소
            Optional<Course> courseOpt = courseMapper.findById(enrollment.getCourseId());
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();
                int newCurrentStudents = Math.max(0, course.getCurrentStudents() - 1);
                courseMapper.updateCurrentStudents(course.getCourseId(), newCurrentStudents);

                // FULL 상태에서 벗어났는지 확인하여 상태 업데이트
                if ("FULL".equals(course.getStatus()) && newCurrentStudents < course.getMaxStudents()) {
                    course.setStatus("OPEN");
                    courseMapper.updateCourse(course);
                }
            }
        } else if ("PENDING".equals(enrollment.getStatus())) {
            // 대기 중인 신청은 삭제
            enrollmentMapper.deleteEnrollment(enrollmentId);
            enrollment.setStatus("CANCELLED");
        } else {
            throw new RuntimeException("취소할 수 없는 상태입니다.");
        }

        return enrollment;
    }
}
