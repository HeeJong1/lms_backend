package com.lmsproject.lms_backend.mapper;

import com.lmsproject.lms_backend.model.Announcement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AnnouncementMapper {
    List<Announcement> findAll();
    Optional<Announcement> findById(Long announcementId);
    List<Announcement> findByCourseId(Long courseId);
    List<Announcement> findByAuthorId(Long authorId);
    List<Announcement> findByTargetRole(String targetRole);
    List<Announcement> findImportant();
    int insertAnnouncement(Announcement announcement);
    int updateAnnouncement(Announcement announcement);
    int deleteAnnouncement(Long announcementId);
    int incrementViewCount(Long announcementId);
}
