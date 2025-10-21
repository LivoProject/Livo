package com.livo.project.mypage.repository;

import com.livo.project.lecture.domain.LectureLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MypageLectureLikeRepository extends JpaRepository<LectureLike, Integer> {
    List<LectureLike> findByEmail(String email);
}
