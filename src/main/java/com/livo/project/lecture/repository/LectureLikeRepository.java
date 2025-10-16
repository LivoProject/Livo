package com.livo.project.lecture.repository;

import com.livo.project.lecture.domain.LectureLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureLikeRepository extends JpaRepository<LectureLike,Integer> {

    //특정 강좌에 해당 유저가 이미 좋아요를 눌렀는지
    boolean existsByLectureIdAndEmail(int lectureId, String email);

    //좋아요 취소
    void deleteByLectureIdAndEmail(int lectureId, String email);
}
