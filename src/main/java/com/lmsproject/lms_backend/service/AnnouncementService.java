package com.lmsproject.lms_backend.service;

import com.lmsproject.lms_backend.mapper.AnnouncementMapper;
import com.lmsproject.lms_backend.mapper.CourseMapper;
import com.lmsproject.lms_backend.mapper.UserMapper;
import com.lmsproject.lms_backend.model.Announcement;
import com.lmsproject.lms_backend.model.Course;
import com.lmsproject.lms_backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;

    public List<Announcement> getAllAnnouncements() {
        return announcementMapper.findAll();
    }

    public Optional<Announcement> getAnnouncementById(Long announcementId) {
        Optional<Announcement> announcement = announcementMapper.findById(announcementId);
        if (announcement.isPresent()) {
            announcementMapper.incrementViewCount(announcementId);
        }
        return announcement;
    }

    public List<Announcement> getAnnouncementsByCourseId(Long courseId) {
        return announcementMapper.findByCourseId(courseId);
    }

    public List<Announcement> getAnnouncementsByAuthorId(Long authorId) {
        return announcementMapper.findByAuthorId(authorId);
    }

    public List<Announcement> getAnnouncementsByTargetRole(String targetRole) {
        return announcementMapper.findByTargetRole(targetRole);
    }

    public List<Announcement> getImportantAnnouncements() {
        return announcementMapper.findImportant();
    }

    @Transactional
    public Announcement createAnnouncement(Long authorId, Long courseId, String title, String content,
                                          Boolean isImportant, String targetRole) {
        // 작성자 확인
        Optional<User> authorOpt = userMapper.findById(authorId);
        if (authorOpt.isEmpty()) {
            throw new RuntimeException("작성자 정보를 찾을 수 없습니다.");
        }

        // 강의 확인 (courseId가 null이 아닌 경우)
        if (courseId != null) {
            Optional<Course> courseOpt = courseMapper.findById(courseId);
            if (courseOpt.isEmpty()) {
                throw new RuntimeException("강의 정보를 찾을 수 없습니다.");
            }
        }

        Announcement announcement = Announcement.builder()
                .authorId(authorId)
                .courseId(courseId)
                .title(title)
                .content(content)
                .isImportant(isImportant != null ? isImportant : false)
                .targetRole(targetRole)
                .viewCount(0)
                .build();

        announcementMapper.insertAnnouncement(announcement);
        return announcementMapper.findById(announcement.getAnnouncementId()).orElseThrow();
    }

    @Transactional
    public Announcement updateAnnouncement(Long announcementId, String title, String content,
                                           Boolean isImportant, String targetRole) {
        Optional<Announcement> announcementOpt = announcementMapper.findById(announcementId);
        if (announcementOpt.isEmpty()) {
            throw new RuntimeException("공지사항을 찾을 수 없습니다.");
        }

        Announcement announcement = announcementOpt.get();
        announcement.setTitle(title != null ? title : announcement.getTitle());
        announcement.setContent(content != null ? content : announcement.getContent());
        announcement.setIsImportant(isImportant != null ? isImportant : announcement.getIsImportant());
        announcement.setTargetRole(targetRole != null ? targetRole : announcement.getTargetRole());

        announcementMapper.updateAnnouncement(announcement);
        return announcementMapper.findById(announcementId).orElseThrow();
    }

    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        Optional<Announcement> announcementOpt = announcementMapper.findById(announcementId);
        if (announcementOpt.isEmpty()) {
            throw new RuntimeException("공지사항을 찾을 수 없습니다.");
        }
        announcementMapper.deleteAnnouncement(announcementId);
    }
}
