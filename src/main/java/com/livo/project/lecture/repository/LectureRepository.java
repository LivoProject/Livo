package com.livo.project.lecture.repository;

import com.livo.project.lecture.domain.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LectureRepository extends JpaRepository<Lecture, Integer> {
    // 카테고리별 검색
    List<Lecture> findByCategoryId(int categoryId);

    // 제목에 특정 단어 포함된 검색
    List<Lecture> findByTitleContaining(String keyword);

    // 페이징용 전체 조회
    Page<Lecture> findAll(Pageable pageable);

    // 검색 + 페이징 조합
    Page<Lecture> findByTitleContaining(String keyword, Pageable pageable);

}
