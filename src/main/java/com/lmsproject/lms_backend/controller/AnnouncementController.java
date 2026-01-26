package com.lmsproject.lms_backend.controller;

import com.lmsproject.lms_backend.model.ApiResponse;
import com.lmsproject.lms_backend.model.Announcement;
import com.lmsproject.lms_backend.service.AnnouncementService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public ApiResponse<List<Announcement>> getAllAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.getAllAnnouncements();
            return ApiResponse.success(announcements);
        } catch (Exception e) {
            return ApiResponse.error("공지사항 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/{announcementId}")
    public ApiResponse<Announcement> getAnnouncementById(@PathVariable Long announcementId) {
        try {
            return announcementService.getAnnouncementById(announcementId)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("공지사항을 찾을 수 없습니다."));
        } catch (Exception e) {
            return ApiResponse.error("공지사항 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<Announcement>> getAnnouncementsByCourseId(@PathVariable Long courseId) {
        try {
            List<Announcement> announcements = announcementService.getAnnouncementsByCourseId(courseId);
            return ApiResponse.success(announcements);
        } catch (Exception e) {
            return ApiResponse.error("공지사항 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/author/{authorId}")
    public ApiResponse<List<Announcement>> getAnnouncementsByAuthorId(@PathVariable Long authorId) {
        try {
            List<Announcement> announcements = announcementService.getAnnouncementsByAuthorId(authorId);
            return ApiResponse.success(announcements);
        } catch (Exception e) {
            return ApiResponse.error("공지사항 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/important")
    public ApiResponse<List<Announcement>> getImportantAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.getImportantAnnouncements();
            return ApiResponse.success(announcements);
        } catch (Exception e) {
            return ApiResponse.error("중요 공지사항 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<Announcement> createAnnouncement(@RequestBody CreateAnnouncementRequest request) {
        try {
            Announcement announcement = announcementService.createAnnouncement(
                    request.getAuthorId(),
                    request.getCourseId(),
                    request.getTitle(),
                    request.getContent(),
                    request.getIsImportant(),
                    request.getTargetRole()
            );
            return ApiResponse.success(announcement);
        } catch (Exception e) {
            return ApiResponse.error("공지사항 작성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PutMapping("/{announcementId}")
    public ApiResponse<Announcement> updateAnnouncement(@PathVariable Long announcementId, @RequestBody UpdateAnnouncementRequest request) {
        try {
            Announcement announcement = announcementService.updateAnnouncement(
                    announcementId,
                    request.getTitle(),
                    request.getContent(),
                    request.getIsImportant(),
                    request.getTargetRole()
            );
            return ApiResponse.success(announcement);
        } catch (Exception e) {
            return ApiResponse.error("공지사항 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @DeleteMapping("/{announcementId}")
    public ApiResponse<Void> deleteAnnouncement(@PathVariable Long announcementId) {
        try {
            announcementService.deleteAnnouncement(announcementId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("공지사항 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Data
    static class CreateAnnouncementRequest {
        private Long authorId;
        private Long courseId;
        private String title;
        private String content;
        private Boolean isImportant;
        private String targetRole; // STUDENT, INSTRUCTOR, ALL, null=전체
    }

    @Data
    static class UpdateAnnouncementRequest {
        private String title;
        private String content;
        private Boolean isImportant;
        private String targetRole;
    }
}
