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


    //  사용자 리스트 (ACTIVE만)
    Page<Lecture> findByVisibility(Lecture.LectureVisibility visibility, Pageable pageable);

    //  사용자 검색 (ACTIVE + 키워드)
    Page<Lecture> findByVisibilityAndTitleContaining(
            Lecture.LectureVisibility visibility,
            String keyword,
            Pageable pageable
    );

    //  카테고리만 선택한 경우 (ACTIVE + Category)
    Page<Lecture> findByVisibilityAndCategory_CategoryId(
            Lecture.LectureVisibility visibility,
            int categoryId,
            Pageable pageable
    );

    //  상위 카테고리만 선택한 경우 (ACTIVE + ParentCategory)
    @Query("""
        SELECT l
        FROM LectureEntity l 
        WHERE l.visibility = 'ACTIVE'
          AND (l.category.categoryId = :mainCategoryId 
               OR l.category.parent.categoryId = :mainCategoryId)
    """)
    Page<Lecture> findAllByMainCategory(@Param("mainCategoryId") int mainCategoryId, Pageable pageable);

    //  단일 조회 (상세는 수강자 예외 처리하므로 기존 유지)
    Optional<Lecture> findByLectureId(int lectureId);


    @Query("""
        SELECT l 
        FROM LectureEntity l 
        WHERE l.visibility = 'ACTIVE'
          AND l.category.categoryId = :categoryId
          AND (l.title LIKE %:keyword% OR l.tutorName LIKE %:keyword%)
    """)
    Page<Lecture> findByCategoryAndKeyword(@Param("categoryId") int categoryId,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);

    @Query("""
        SELECT l 
        FROM LectureEntity l 
        WHERE l.visibility = 'ACTIVE'
          AND (l.category.categoryId = :mainCategoryId 
               OR l.category.parent.categoryId = :mainCategoryId)
          AND (l.title LIKE %:keyword% OR l.tutorName LIKE %:keyword%)
    """)
    Page<Lecture> findByMainCategoryAndKeyword(@Param("mainCategoryId") int mainCategoryId,
                                               @Param("keyword") String keyword,
                                               Pageable pageable);


}
