package com.livo.project.mypage.repository;

import com.livo.project.lecture.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MypageLectureRepository extends JpaRepository<Lecture, Integer> {
    @Query(value = "SELECT * FROM lecture ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<Lecture> findRandomLectures();
}