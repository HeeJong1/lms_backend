package com.lmsproject.lms_backend.mapper;

import com.lmsproject.lms_backend.model.CourseMaterial;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CourseMaterialMapper {
    List<CourseMaterial> findAll();
    Optional<CourseMaterial> findById(Long materialId);
    List<CourseMaterial> findByCourseId(Long courseId);
    List<CourseMaterial> findByCategory(String category);
    int insertMaterial(CourseMaterial material);
    int updateMaterial(CourseMaterial material);
    int deleteMaterial(Long materialId);
    int incrementDownloadCount(Long materialId);
}
