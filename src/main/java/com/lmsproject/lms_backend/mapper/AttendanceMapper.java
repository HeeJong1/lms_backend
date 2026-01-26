package com.lmsproject.lms_backend.mapper;

import com.lmsproject.lms_backend.model.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface AttendanceMapper {
    List<Attendance> findAll();
    Optional<Attendance> findById(Long attendanceId);
    List<Attendance> findByEnrollmentId(Long enrollmentId);
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByCourseId(Long courseId);
    Optional<Attendance> findByEnrollmentIdAndDate(@Param("enrollmentId") Long enrollmentId, @Param("date") LocalDate date);
    int insertAttendance(Attendance attendance);
    int updateAttendance(Attendance attendance);
    int deleteAttendance(Long attendanceId);
    int countByEnrollmentId(Long enrollmentId);
    int countByEnrollmentIdAndStatus(@Param("enrollmentId") Long enrollmentId, @Param("status") String status);
}
