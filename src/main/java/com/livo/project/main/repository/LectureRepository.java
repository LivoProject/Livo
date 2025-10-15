package com.livo.project.main.repository;

import com.livo.project.main.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Integer> {
    List<Lecture> findTop5ByOrderByLectureIdDesc(); // 최신 5개
}
