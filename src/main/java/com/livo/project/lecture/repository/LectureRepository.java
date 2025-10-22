package com.livo.project.lecture.repository;

import com.livo.project.lecture.domain.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("lectureLectureRepository")
public interface LectureRepository extends JpaRepository<Lecture, Integer> {

    // 전체 강좌 조회 (페이징 포함)
    Page<Lecture> findAll(Pageable pageable);

    // 키워드 검색
    Page<Lecture> findByTitleContaining(String keyword, Pageable pageable);

    // 카테고리별 검색
    List<Lecture> findByCategory_CategoryId(int categoryId);

    // 상위 카테고리(mainCategory)에 속한 하위 카테고리 강좌까지 조회
    @Query("SELECT l FROM LectureEntity l WHERE l.category.categoryId = :mainCategoryId OR l.category.parent.categoryId = :mainCategoryId")
    List<Lecture> findAllByMainCategory(@Param("mainCategoryId") int mainCategoryId);

}
