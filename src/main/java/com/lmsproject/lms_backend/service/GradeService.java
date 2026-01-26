package com.lmsproject.lms_backend.service;

import com.lmsproject.lms_backend.mapper.EnrollmentMapper;
import com.lmsproject.lms_backend.mapper.GradeMapper;
import com.lmsproject.lms_backend.model.Enrollment;
import com.lmsproject.lms_backend.model.Grade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeMapper gradeMapper;
    private final EnrollmentMapper enrollmentMapper;

    public List<Grade> getAllGrades() {
        return gradeMapper.findAll();
    }

    public Optional<Grade> getGradeById(Long gradeId) {
        return gradeMapper.findById(gradeId);
    }

    public Optional<Grade> getGradeByEnrollmentId(Long enrollmentId) {
        return gradeMapper.findByEnrollmentId(enrollmentId);
    }

    public List<Grade> getGradesByStudentId(Long studentId) {
        return gradeMapper.findByStudentId(studentId);
    }

    public List<Grade> getGradesByCourseId(Long courseId) {
        return gradeMapper.findByCourseId(courseId);
    }

    @Transactional
    public Grade createGrade(Long enrollmentId, Integer midtermScore, Integer finalScore,
                            Integer assignmentScore, Integer attendanceScore, String remarks) {
        // 수강신청 정보 확인
        Optional<Enrollment> enrollmentOpt = enrollmentMapper.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("수강신청 정보를 찾을 수 없습니다.");
        }

        Enrollment enrollment = enrollmentOpt.get();
        if (!"APPROVED".equals(enrollment.getStatus())) {
            throw new RuntimeException("승인된 수강신청에만 성적을 입력할 수 있습니다.");
        }

        // 이미 성적이 있는지 확인
        Optional<Grade> existingGrade = gradeMapper.findByEnrollmentId(enrollmentId);
        if (existingGrade.isPresent()) {
            throw new RuntimeException("이미 성적이 입력되어 있습니다.");
        }

        Grade grade = Grade.builder()
                .enrollmentId(enrollmentId)
                .studentId(enrollment.getStudentId())
                .courseId(enrollment.getCourseId())
                .midtermScore(midtermScore != null ? midtermScore : 0)
                .finalScore(finalScore != null ? finalScore : 0)
                .assignmentScore(assignmentScore != null ? assignmentScore : 0)
                .attendanceScore(attendanceScore != null ? attendanceScore : 0)
                .remarks(remarks)
                .build();

        gradeMapper.insertGrade(grade);
        return gradeMapper.findById(grade.getGradeId()).orElseThrow();
    }

    @Transactional
    public Grade updateGrade(Long gradeId, Integer midtermScore, Integer finalScore,
                            Integer assignmentScore, Integer attendanceScore, String remarks) {
        Optional<Grade> gradeOpt = gradeMapper.findById(gradeId);
        if (gradeOpt.isEmpty()) {
            throw new RuntimeException("성적 정보를 찾을 수 없습니다.");
        }

        Grade grade = gradeOpt.get();
        grade.setMidtermScore(midtermScore != null ? midtermScore : grade.getMidtermScore());
        grade.setFinalScore(finalScore != null ? finalScore : grade.getFinalScore());
        grade.setAssignmentScore(assignmentScore != null ? assignmentScore : grade.getAssignmentScore());
        grade.setAttendanceScore(attendanceScore != null ? attendanceScore : grade.getAttendanceScore());
        grade.setRemarks(remarks != null ? remarks : grade.getRemarks());

        gradeMapper.updateGrade(grade);
        return gradeMapper.findById(gradeId).orElseThrow();
    }

    @Transactional
    public void deleteGrade(Long gradeId) {
        Optional<Grade> gradeOpt = gradeMapper.findById(gradeId);
        if (gradeOpt.isEmpty()) {
            throw new RuntimeException("성적 정보를 찾을 수 없습니다.");
        }
        gradeMapper.deleteGrade(gradeId);
    }
}
