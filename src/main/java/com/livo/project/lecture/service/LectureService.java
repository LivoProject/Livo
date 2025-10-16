package com.livo.project.lecture.service;


import com.livo.project.lecture.domain.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LectureService {

    // 강좌 전체 조회
    List<Lecture> findAll();

    // 강좌 상세 조회 (PK 기준)
    Optional<Lecture> findById(int lectureId);

    // 카테고리별 강좌 조회
    List<Lecture> findByCategoryId(int categoryId);

    // 제목 검색
    List<Lecture> findByTitleContaining(String keyword);

    // 페이지
    Page<Lecture> getLecturePage(Pageable pageable);
    Page<Lecture> searchLecturePage(String keyword, Pageable pageable);
}


