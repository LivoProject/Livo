package com.livo.project.admin.repository;

import com.livo.project.lecture.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureAdminRepository extends JpaRepository<Lecture,Integer> {
    List<Lecture> findTop5ByOrderByLectureIdDesc();
}
