package com.livo.project.main.repository;

import com.livo.project.lecture.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MainLectureRepository extends JpaRepository<Lecture, Integer> {
    // 추천 (랜덤 4개)
    @Query(value = "SELECT * FROM lecture ORDER BY RAND() LIMIT 4", nativeQuery = true)
    List<Lecture> findRandomLectures();

    // 인기 (평점 높은 순 5개)
    //<Lecture> findTop5ByOrderByRatingDesc();
}