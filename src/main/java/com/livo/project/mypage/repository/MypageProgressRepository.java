package com.livo.project.mypage.repository;

import com.livo.project.lecture.domain.Lecture;
import com.livo.project.mypage.domain.entity.LectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MypageProgressRepository extends JpaRepository<LectureProgress, Long> {
   // Optional<LectureProgress> findByLectureAndEmail(Lecture lecture, String email);

    @Query("""
    SELECT lp
    FROM LectureProgress lp
    WHERE lp.email = :email
      AND lp.lecture = :lecture
""")
   Optional<LectureProgress> findByLectureAndUser(
           @Param("lecture") Lecture lecture,
           @Param("email") String email,
           @Param("provider") String provider // provider는 그냥 받기만, 쿼리에 안 써도 됨
   );
}
