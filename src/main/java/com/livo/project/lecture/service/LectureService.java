package com.livo.project.lecture.service;


import com.livo.project.lecture.domain.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LectureService {

    // 강좌 입력(관리자페이지)
    Lecture saveLecture(Lecture lecture, int categoryId);

    // 강좌 삭제
    boolean deleteLecture(int lectureId);

    //강좌 수정
    Lecture editLecture(int lectureId);
    Lecture updateLecture(Lecture lecture, int categoryId);

    // 강좌 전체 조회
    List<Lecture> findAll();

    // 강좌 상세 조회
    Optional<Lecture> findById(int lectureId);

    // 카테고리별 검색
    List<Lecture> findByCategoryId(int categoryId);
    List<Lecture> findAllByMainCategory(int mainCategoryId);

    // 페이지 + 키워드검색
    Page<Lecture> getLecturePage(Pageable pageable);
    Page<Lecture> searchLecturePage(String keyword, Pageable pageable);

}


