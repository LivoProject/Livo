package com.livo.project.mypage.repository;

import com.livo.project.lecture.domain.Reservation;
import com.livo.project.mypage.domain.dto.MypageReservationDto;
import jakarta.transaction.Transactional;
import org.eclipse.tags.shaded.org.apache.regexp.RE;
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


    // 내 예약 강좌
    @Query("SELECT r FROM Reservation r WHERE r.user.email = :email AND (r.status = 'CONFIRMED' OR r.status = 'PAID')")
    Page<Reservation> findAllByEmailAndProvider(@Param("email") String email, @Param("provider") String provider, Pageable pageable);


    // 예약 취소
    @Modifying
    @Query("UPDATE Reservation r SET r.status = 'CANCEL' WHERE r.lecture.lectureId = :lectureId AND r.user.email = :email")
    int cancelByLectureIdAndEmail(@Param("lectureId") Integer lectureId,
                                  @Param("email") String email);

    // 좋아요한 강좌가 예약된 강좌인지 확인
    boolean existsByLecture_LectureIdAndUser_Email(Integer lectureId, String email);

    // 예약완료된 최근 3개 강의
    @Query("""
                SELECT r 
                FROM Reservation r
                JOIN FETCH r.lecture l
                JOIN FETCH r.user u
                WHERE u.email = :email
                  AND u.provider = :provider
                  AND l.status != 'ENDED'
                  AND r.status = 'CONFIRMED'
                ORDER BY r.createdAt DESC
            """)
    List<Reservation> findTop3ConfirmedByEmailAndProvider(@Param("email") String email,
                                                          @Param("provider") String provider,
                                                          Pageable pageable);


    //내 강좌 검색
    @Query("""
                SELECT r
                FROM Reservation r
                JOIN r.lecture l
                JOIN r.user u
                WHERE u.email = :email
                  AND u.provider = :provider
                  AND r.status = 'CONFIRMED'
                  AND (
                      LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                      OR LOWER(l.tutorName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            """)
    Page<Reservation> searchByKeyword(@Param("email") String email,
                                      @Param("provider") String provider,
                                      @Param("keyword") String keyword,
                                      Pageable pageable);


    // 최신순 + 오래된순
    @Query("""
            SELECT new com.livo.project.mypage.domain.dto.MypageReservationDto(
                r.reservationId,
                l.lectureId,
                l.title,
                l.tutorName,
                l.thumbnailUrl,
                l.lectureStart,
                l.lectureEnd,
                l.visibility,
                l.status,
                l.price,
                COALESCE(lp.progressPercent, 0),
                0L /* 기본 likeCount */
            )
            FROM Reservation r
            JOIN r.user u
            JOIN r.lecture l
            LEFT JOIN LectureProgress lp
                ON lp.lecture = l AND lp.email = u.email
            WHERE u.email = :email
              AND l.visibility = 'ACTIVE'
              AND u.provider = :provider
              AND (:keyword IS NULL
                   OR l.title LIKE CONCAT('%', :keyword, '%')
                   OR l.tutorName LIKE CONCAT('%', :keyword, '%'))
           GROUP BY r.reservationId, l.lectureId, l.title, l.tutorName, l.thumbnailUrl,
                    l.lectureStart, l.lectureEnd, l.visibility, l.status, l.price, lp.progressPercent
            """)
    Page<MypageReservationDto> findMyReservationsForList(
            @Param("email") String email,
            @Param("provider") String provider,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    // 인기순
    // Repository
    @Query("""
            SELECT new com.livo.project.mypage.domain.dto.MypageReservationDto(
                r.reservationId,
                l.lectureId,
                l.title,
                l.tutorName,
                l.thumbnailUrl,
                l.lectureStart,
                l.lectureEnd,
                l.visibility,
                l.status,
                l.price,
                COALESCE(lp.progressPercent, 0),
                COUNT(DISTINCT ll.likeId)
            )
            FROM Reservation r
            JOIN r.user u
            JOIN r.lecture l
            LEFT JOIN LectureProgress lp
                ON lp.lecture = l AND lp.email = u.email
            LEFT JOIN LectureLike ll
                ON ll.lecture = l
            WHERE u.email = :email
              AND l.visibility = 'ACTIVE'
              AND u.provider = :provider
              AND (:keyword IS NULL
                   OR l.title LIKE CONCAT('%', :keyword, '%')
                   OR l.tutorName LIKE CONCAT('%', :keyword, '%'))
            GROUP BY r.reservationId, l.lectureId, l.title, l.tutorName, l.thumbnailUrl,
                     l.lectureStart, l.lectureEnd, l.visibility, l.status, l.price, lp.progressPercent
            ORDER BY COUNT(DISTINCT ll.likeId) DESC, MAX(r.createdAt) DESC
            """)
    Page<MypageReservationDto> findMyReservationsOrderByLikes(
            @Param("email") String email,
            @Param("provider") String provider,
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
