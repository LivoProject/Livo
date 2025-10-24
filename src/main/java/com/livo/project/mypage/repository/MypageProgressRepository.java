package com.livo.project.mypage.repository;

import com.livo.project.lecture.domain.Lecture;
import com.livo.project.mypage.domain.entity.LectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MypageProgressRepository extends JpaRepository<LectureProgress, Long> {
    Optional<LectureProgress> findByLectureAndEmail(Lecture lecture, String email);
}
