package com.livo.project.mypage.repository;

import com.livo.project.lecture.domain.Reservation;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MypageReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r FROM Reservation r WHERE r.user.email = :email AND r.status = 'CONFIRMED'")
    Page<Reservation> findConfirmedByEmail(String email, Pageable pageable);

   // void deleteByLectureIdAndEmail(Integer lectureId, String email);

    @Modifying
    @Query("UPDATE Reservation r SET r.status = 'CANCEL' WHERE r.lecture.lectureId = :lectureId AND r.user.email = :email")
    int cancelByLectureIdAndEmail(@Param("lectureId") Integer lectureId,
                                   @Param("email") String email);

//    @Modifying
//    @Transactional
//    @Query("DELETE FROM Reservation r WHERE r.user.id = :userId AND r.lecture.lectureId = :lectureId")
//    void deleteByUserIdAndLectureId(@Param("email") String email, @Param("lectureId") Integer lectureId);
}