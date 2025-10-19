package com.livo.project.mypage.repository;

import com.livo.project.mypage.domain.entity.MypageLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MypageLectureRepository extends JpaRepository<MypageLecture, Integer> {
    @Query(value = "SELECT * FROM lecture ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<MypageLecture> findRandomLectures();
}