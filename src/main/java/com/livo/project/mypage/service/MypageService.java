package com.livo.project.mypage.service;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.domain.Reservation;
import com.livo.project.lecture.repository.LectureRepository;
import com.livo.project.mypage.domain.dto.MypageDto;
import com.livo.project.mypage.domain.dto.ReservationDto;
import com.livo.project.mypage.repository.*;
import com.livo.project.notice.domain.dto.NoticeDto;
import com.livo.project.notice.domain.entity.Notice;
import com.livo.project.review.domain.Review;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * 마이페이지 서비스
 * - 유저 데이터 조회 및 수정, 비밀번호 변경 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {

    private final MypageUserRepository mypageUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final MypageNoticeRepository mypageNoticeRepository;
    private final MypageLectureRepository mypageLectureRepository;
    private final MypageReservationRepository mypageReservationRepository;
    private final MypageReviewRepository mypageReviewRepository;
    private final LectureRepository lectureRepository;

    // 마이페이지 기본 데이터 조회
    public MypageDto getUserData(String email) {
        User user = mypageUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 공지사항
        List<Notice> notices = mypageNoticeRepository.findTop5ByOrderByCreatedAtDesc();
        List<NoticeDto> noticeDtos = notices.stream()
                .map(NoticeDto::fromEntity)
                .toList();

        // 추천 강좌
        List<Lecture> recommended = mypageLectureRepository.findRandomLectures();

        long joinDays = 1;
        if (user.getCreatedAt() != null) {
            long days = ChronoUnit.DAYS.between(
                    user.getCreatedAt().toLocalDate(),
                    LocalDate.now()
            );
            joinDays = Math.max(days + 1, 1); // 0일 → 1일로 보정
        }

        return MypageDto.builder()
                .userId(user.getId())
                .username(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .birth(user.getBirth())
                .gender(user.getGender() != null ? user.getGender().toString() : null)
                .joinDays(joinDays)
                .notices(noticeDtos)
                .recommendedLectures(recommended)
                .build();
    }

    // 일반 정보 수정 (이메일 기반)
    @Transactional
    public void updateUserProfile(String email, MypageDto dto) {
        User user = mypageUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setBirth(dto.getBirth());
        user.setName(dto.getUsername());

        //  gender만 리플렉션으로 변환해서 세팅 (Entity / Enum import X)
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            try {
                Class<?> genderClass = Class.forName("com.livo.project.auth.domain.entity.Gender");
                Object genderEnum = Enum.valueOf((Class<Enum>) genderClass, dto.getGender().toUpperCase());
                user.getClass().getMethod("setGender", genderClass).invoke(user, genderEnum);
            } catch (Exception e) {
                log.warn("잘못된 gender 값: {}", dto.getGender(), e);
            }
        }

        log.info("사용자 정보 수정 완료: {}", email);
    }

    // 비밀번호 변경
    @Transactional
    public void updateUserPassword(String currentPassword, String newPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = mypageUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        mypageUserRepository.save(user);
        log.info("비밀번호 변경 완료: {}", email);
    }


    // 좋아요 강의
    public Page<Lecture> getLikedLectures(String email, Pageable pageable) {
        return mypageLectureRepository.findLikedLecturesByEmail(email, pageable);
    }

    // 좋아요 강의 2개
    public List<Lecture> getTop2LikedLectures(String email) {
        return mypageLectureRepository.findTop2LikedLecturesByEmail(email);
    }

    // 좋아요 해제
    @Transactional
    public void removeLikedLecture(Integer lectureId, String email) {
        mypageLectureRepository.deleteLikeByLectureIdAndEmail(lectureId, email);
    }


    // 내 강좌 조회
    public Page<ReservationDto> getMyReservations(String email, Pageable pageable) {
        Page<Reservation> reservations = mypageReservationRepository.findAllByEmail(email, pageable);


        return reservations.map(r -> {
            Lecture l = lectureRepository.findById(r.getLectureId()).orElse(null);
            return (l != null) ? ReservationDto.of(r, l) : null;
        });
    }

    // 내 강좌 예약 취소
    @Transactional
    public void removeReservationLecture(String email, Integer lectureId) {
        mypageReservationRepository.deleteByEmailAndLectureId(email,lectureId);
    }

    // 내 리뷰 조회
    public Page<Review> getMyReviews(String email, Pageable pageable) {
        return mypageReviewRepository.findAllByEmail(email, pageable);
    }


}

