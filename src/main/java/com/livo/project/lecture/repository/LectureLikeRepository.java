package com.livo.project.lecture.repository;

import com.livo.project.lecture.domain.LectureLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureLikeRepository extends JpaRepository<LectureLike, Integer> {

    boolean existsByLectureIdAndEmail(int lectureId, String email);

    void deleteByLectureIdAndEmail(int lectureId, String email);
}
