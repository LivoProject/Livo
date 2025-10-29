package com.livo.project.lecture.repository;

import com.livo.project.lecture.domain.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("lectureLectureRepository")
public interface LectureRepository extends JpaRepository<Lecture, Integer> {

    // 기존 것들 그대로 유지
    Page<Lecture> findAll(Pageable pageable);
    Page<Lecture> findByTitleContaining(String keyword, Pageable pageable);
    List<Lecture> findByCategory_CategoryId(int categoryId);
    Page<Lecture> findByCategory_CategoryId(int categoryId, Pageable pageable);

    @Query("SELECT l FROM LectureEntity l WHERE l.category.categoryId = :mainCategoryId OR l.category.parent.categoryId = :mainCategoryId")
    Page<Lecture> findAllByMainCategory(@Param("mainCategoryId") int mainCategoryId, Pageable pageable);

    Optional<Lecture> findByLectureId(int lectureId);

    // 민영 추가
    @Query("""
        SELECT l 
        FROM LectureEntity l 
        WHERE l.category.categoryId = :categoryId
          AND (l.title LIKE %:keyword% OR l.tutorName LIKE %:keyword%)
    """)
    Page<Lecture> findByCategoryAndKeyword(@Param("categoryId") int categoryId,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);

    @Query("""
        SELECT l 
        FROM LectureEntity l 
        WHERE (l.category.categoryId = :mainCategoryId 
               OR l.category.parent.categoryId = :mainCategoryId)
          AND (l.title LIKE %:keyword% OR l.tutorName LIKE %:keyword%)
    """)
    Page<Lecture> findByMainCategoryAndKeyword(@Param("mainCategoryId") int mainCategoryId,
                                               @Param("keyword") String keyword,
                                               Pageable pageable);
}
