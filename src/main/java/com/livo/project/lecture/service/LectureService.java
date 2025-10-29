package com.livo.project.lecture.service;


import com.livo.project.lecture.domain.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LectureService {
    // 강좌 전체 조회
    List<Lecture> findAll();

    // 강좌 상세 조회
    Optional<Lecture> findById(int lectureId);

    // 카테고리별 검색
    List<Lecture> findAllByMainCategory(int mainCategoryId);

    // 페이징 기반 카테고리 검색 추가
    Page<Lecture> getLecturePageByCategory(int categoryId, Pageable pageable);

    // 페이징 기반 상위 카테고리 검색 추가
    Page<Lecture> getLecturePageByMainCategory(int mainCategoryId, Pageable pageable);

    // 페이지 + 키워드검색
    Page<Lecture> getLecturePage(Pageable pageable);
    Page<Lecture> searchLecturePage(String keyword, Pageable pageable);

    // 민영 추가 (세부분류 + 키워드)
    Page<Lecture> searchByCategoryAndKeyword(int categoryId, String keyword, Pageable pageable);

    // 민영 추가 (상위카테고리 + 키워드)
    Page<Lecture> searchByMainCategoryAndKeyword(int mainCategoryId, String keyword, Pageable pageable);


}


