package com.lmsproject.lms_backend.controller;

import com.lmsproject.lms_backend.model.ApiResponse;
import com.lmsproject.lms_backend.model.CourseMaterial;
import com.lmsproject.lms_backend.service.CourseMaterialService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseMaterialController {

    private final CourseMaterialService materialService;

    @GetMapping
    public ApiResponse<List<CourseMaterial>> getAllMaterials() {
        try {
            List<CourseMaterial> materials = materialService.getAllMaterials();
            return ApiResponse.success(materials);
        } catch (Exception e) {
            return ApiResponse.error("강의 자료 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/{materialId}")
    public ApiResponse<CourseMaterial> getMaterialById(@PathVariable Long materialId) {
        try {
            return materialService.getMaterialById(materialId)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("강의 자료를 찾을 수 없습니다."));
        } catch (Exception e) {
            return ApiResponse.error("강의 자료 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<CourseMaterial>> getMaterialsByCourseId(@PathVariable Long courseId) {
        try {
            List<CourseMaterial> materials = materialService.getMaterialsByCourseId(courseId);
            return ApiResponse.success(materials);
        } catch (Exception e) {
            return ApiResponse.error("강의 자료 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/category/{category}")
    public ApiResponse<List<CourseMaterial>> getMaterialsByCategory(@PathVariable String category) {
        try {
            List<CourseMaterial> materials = materialService.getMaterialsByCategory(category);
            return ApiResponse.success(materials);
        } catch (Exception e) {
            return ApiResponse.error("강의 자료 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<CourseMaterial> createMaterial(@RequestBody CreateMaterialRequest request) {
        try {
            CourseMaterial material = materialService.createMaterial(
                    request.getCourseId(),
                    request.getUploaderId(),
                    request.getTitle(),
                    request.getDescription(),
                    request.getFilePath(),
                    request.getFileName(),
                    request.getFileSize(),
                    request.getFileType(),
                    request.getCategory()
            );
            return ApiResponse.success(material);
        } catch (Exception e) {
            return ApiResponse.error("강의 자료 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PutMapping("/{materialId}")
    public ApiResponse<CourseMaterial> updateMaterial(@PathVariable Long materialId, @RequestBody UpdateMaterialRequest request) {
        try {
            CourseMaterial material = materialService.updateMaterial(
                    materialId,
                    request.getTitle(),
                    request.getDescription(),
                    request.getCategory()
            );
            return ApiResponse.success(material);
        } catch (Exception e) {
            return ApiResponse.error("강의 자료 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @DeleteMapping("/{materialId}")
    public ApiResponse<Void> deleteMaterial(@PathVariable Long materialId) {
        try {
            materialService.deleteMaterial(materialId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("강의 자료 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/{materialId}/download")
    public ApiResponse<Void> incrementDownloadCount(@PathVariable Long materialId) {
        try {
            materialService.incrementDownloadCount(materialId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("다운로드 카운트 증가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Data
    static class CreateMaterialRequest {
        private Long courseId;
        private Long uploaderId;
        private String title;
        private String description;
        private String filePath;
        private String fileName;
        private Long fileSize;
        private String fileType;
        private String category; // 강의자료, 참고자료, 기타
    }

    @Data
    static class UpdateMaterialRequest {
        private String title;
        private String description;
        private String category;
    }
}
