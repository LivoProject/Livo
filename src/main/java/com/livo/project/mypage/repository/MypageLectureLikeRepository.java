package com.livo.project.mypage.repository;

import com.livo.project.lecture.domain.LectureLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MypageLectureLikeRepository extends JpaRepository<LectureLike, Integer> {
    Page<LectureLike> findByEmail(String email, Pageable pageable);
}
