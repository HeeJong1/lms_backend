package com.lmsproject.lms_backend.service;

import com.lmsproject.lms_backend.mapper.CourseMaterialMapper;
import com.lmsproject.lms_backend.mapper.CourseMapper;
import com.lmsproject.lms_backend.mapper.UserMapper;
import com.lmsproject.lms_backend.model.Course;
import com.lmsproject.lms_backend.model.CourseMaterial;
import com.lmsproject.lms_backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseMaterialService {

    private final CourseMaterialMapper materialMapper;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;

    public List<CourseMaterial> getAllMaterials() {
        return materialMapper.findAll();
    }

    public Optional<CourseMaterial> getMaterialById(Long materialId) {
        return materialMapper.findById(materialId);
    }

    public List<CourseMaterial> getMaterialsByCourseId(Long courseId) {
        return materialMapper.findByCourseId(courseId);
    }

    public List<CourseMaterial> getMaterialsByCategory(String category) {
        return materialMapper.findByCategory(category);
    }

    @Transactional
    public CourseMaterial createMaterial(Long courseId, Long uploaderId, String title, String description,
                                       String filePath, String fileName, Long fileSize, String fileType,
                                       String category) {
        // 강의 확인
        Optional<Course> courseOpt = courseMapper.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("강의 정보를 찾을 수 없습니다.");
        }

        // 업로더 확인
        Optional<User> uploaderOpt = userMapper.findById(uploaderId);
        if (uploaderOpt.isEmpty()) {
            throw new RuntimeException("업로더 정보를 찾을 수 없습니다.");
        }

        CourseMaterial material = CourseMaterial.builder()
                .courseId(courseId)
                .uploaderId(uploaderId)
                .title(title)
                .description(description)
                .filePath(filePath)
                .fileName(fileName)
                .fileSize(fileSize)
                .fileType(fileType)
                .category(category != null ? category : "기타")
                .downloadCount(0)
                .build();

        materialMapper.insertMaterial(material);
        return materialMapper.findById(material.getMaterialId()).orElseThrow();
    }

    @Transactional
    public CourseMaterial updateMaterial(Long materialId, String title, String description, String category) {
        Optional<CourseMaterial> materialOpt = materialMapper.findById(materialId);
        if (materialOpt.isEmpty()) {
            throw new RuntimeException("강의 자료를 찾을 수 없습니다.");
        }

        CourseMaterial material = materialOpt.get();
        material.setTitle(title != null ? title : material.getTitle());
        material.setDescription(description != null ? description : material.getDescription());
        material.setCategory(category != null ? category : material.getCategory());

        materialMapper.updateMaterial(material);
        return materialMapper.findById(materialId).orElseThrow();
    }

    @Transactional
    public void deleteMaterial(Long materialId) {
        Optional<CourseMaterial> materialOpt = materialMapper.findById(materialId);
        if (materialOpt.isEmpty()) {
            throw new RuntimeException("강의 자료를 찾을 수 없습니다.");
        }
        materialMapper.deleteMaterial(materialId);
    }

    @Transactional
    public void incrementDownloadCount(Long materialId) {
        materialMapper.incrementDownloadCount(materialId);
    }
}
