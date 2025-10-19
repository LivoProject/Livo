package com.livo.project.maindashboard.repository;

import com.livo.project.maindashboard.domain.entity.MainDashBoardLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MainDashBoardLectureRepository extends JpaRepository<MainDashBoardLecture, Integer> {
    // 추천 (랜덤 4개)
    @Query(value = "SELECT * FROM lecture ORDER BY RAND() LIMIT 4", nativeQuery = true)
    List<MainDashBoardLecture> findRandomLectures();



    // 인기 (평점 높은 순 5개)
    //<Lecture> findTop5ByOrderByRatingDesc();

}
