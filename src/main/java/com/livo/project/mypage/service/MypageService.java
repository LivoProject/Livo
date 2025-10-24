package com.livo.project.mypage.service;

import com.livo.project.auth.domain.entity.User;

import com.livo.project.auth.repository.UserRepository;
import com.livo.project.auth.security.AppUserDetails;

import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.domain.Reservation;
import com.livo.project.lecture.repository.LectureRepository;
import com.livo.project.mypage.domain.dto.MypageDto;
import com.livo.project.mypage.domain.dto.MypageLectureDto;
import com.livo.project.mypage.domain.dto.ProgressDto;
import com.livo.project.mypage.domain.dto.ReservationDto;
import com.livo.project.mypage.domain.entity.LectureProgress;
import com.livo.project.mypage.repository.*;
import com.livo.project.notice.domain.dto.NoticeDto;
import com.livo.project.notice.domain.entity.Notice;
import com.livo.project.review.domain.Review;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


import java.time.LocalDate;
import java.util.List;

import java.time.temporal.ChronoUnit;


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
    private final UserRepository userRepository;



    private final MypageProgressRepository mypageProgressRepository;


    /** 현재 인증 정보에서 (provider, providerId) 또는 id 추출 */
    private ResolvedPrincipal resolveCurrent() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null)
            throw new RuntimeException("인증 정보가 없습니다.");

        Object principal = auth.getPrincipal();

        // 1) LOCAL
        if (principal instanceof AppUserDetails aud) {
            return ResolvedPrincipal.builder()
                    .userId(aud.getId())
                    .provider("LOCAL")
                    .providerId(aud.getUsername()) // 현 구조: providerId=email
                    .email(aud.getUsername())
                    .build();
        }

        // 2) OIDC (Google 등)
        if (principal instanceof OidcUser oidc) {
            String email = oidc.getEmail();
            String providerId = oidc.getSubject(); // sub
            String provider = "GOOGLE";
            if (auth instanceof OAuth2AuthenticationToken tok) {
                provider = tok.getAuthorizedClientRegistrationId().toUpperCase(); // google -> GOOGLE
            }
            return ResolvedPrincipal.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .build();
        }

        // 3) OAuth2 (Kakao/Naver)
        if (principal instanceof DefaultOAuth2User ou) {
            var attrs = ou.getAttributes();
            String providerId = null;

            if (attrs.get("id") != null) {
                providerId = String.valueOf(attrs.get("id"));               // kakao
            } else if (attrs.get("response") instanceof Map<?,?> resp && resp.get("id") != null) {
                providerId = String.valueOf(resp.get("id"));               // naver
            }

            String email = null;
            if (attrs.get("email") != null) {
                email = String.valueOf(attrs.get("email"));
            } else if (attrs.get("kakao_account") instanceof Map<?,?> ka && ka.get("email") != null) {
                email = String.valueOf(ka.get("email"));
            }

            String provider = "OAUTH2";
            if (auth instanceof OAuth2AuthenticationToken tok) {
                provider = tok.getAuthorizedClientRegistrationId().toUpperCase(); // KAKAO/NAVER
            }

            return ResolvedPrincipal.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .build();
        }

        throw new RuntimeException("지원하지 않는 principal 타입: " + principal.getClass().getName());
    }
    /** 공통: 현재 로그인 사용자 로드 */
    private User loadCurrentUser() {
        ResolvedPrincipal rp = resolveCurrent();

        // PK가 있으면 가장 빠르고 확실
        if (rp.getUserId() != null) {
            return userRepository.findById(rp.getUserId())
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        }

        // 소셜은 (provider, providerId)로 유일성 보장
        if (rp.getProvider() != null && rp.getProviderId() != null) {
            return userRepository.findByProviderAndProviderId(rp.getProvider(), rp.getProviderId())
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        }

        // 혹시 모를 예외적인 케이스 대비(비권장): 이메일만으로는 위험
        throw new RuntimeException("사용자 식별에 실패했습니다.");
    }

    // 기본데이터
    public MypageDto getUserData(String email, String provider) {
        User user = userRepository.findByEmailIgnoreCaseAndProvider(email, provider)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));


        // 공지사항
        List<Notice> notices = mypageNoticeRepository.findTop5ByOrderByCreatedAtDesc();
        List<NoticeDto> noticeDtos = notices.stream().map(NoticeDto::fromEntity).toList();

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
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .build();
    }

    // 프로필 수정
    @Transactional
    public void updateUserProfile(MypageDto dto) {
        User user = loadCurrentUser();

        if (dto.getNickname() != null && !dto.getNickname().isBlank()) user.setNickname(dto.getNickname());
        if (dto.getPhone() != null && !dto.getPhone().isBlank())       user.setPhone(dto.getPhone());
        if (dto.getBirth() != null)                                    user.setBirth(dto.getBirth());
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) user.setName(dto.getUsername());
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            try { user.setGender(User.Gender.valueOf(dto.getGender().trim().toUpperCase())); }
            catch (IllegalArgumentException e) { log.warn("잘못된 gender 값: {}", dto.getGender()); }
        }

        userRepository.save(user);
        log.info("사용자 정보 수정 완료: id={}", user.getId());

    }

    // 비밀번호 변경
    @Transactional
    public void updateUserPassword(String currentPassword, String newPassword) {
        User user = loadCurrentUser();

        // 소셜 계정은 비밀번호가 없거나 의미가 다를 수 있으니 가드
        if (!"LOCAL".equalsIgnoreCase(user.getProvider())) {
            throw new RuntimeException("소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.");
        }


        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        log.info("비밀번호 변경 완료: id={}", user.getId());
    }

    // ===== 내부 클래스 =====
    @lombok.Builder
    @lombok.Getter
    private static class ResolvedPrincipal {
        private final Long userId;       // 로컬(AppUserDetails)일 때만 바로 채워짐
        private final String provider;   // LOCAL / GOOGLE / KAKAO / NAVER ...
        private final String providerId; // LOCAL: email, GOOGLE: sub, KAKAO/NAVER: id
        private final String email;


//        mypageUserRepository.save(user);
//        log.info("비밀번호 변경 완료: {}", email);
    }


    // 좋아요 강의
    public Page<Lecture> getLikedLectures(String email, String provider, Pageable pageable) {
        return mypageLectureRepository.findLikedLecturesByEmail(email, provider, pageable);
    }

    // 좋아요 강의 2개
    public List<Lecture> getTop2LikedLectures(String email, String provider) {
        return mypageLectureRepository.findTop2LikedLecturesByEmail(email, provider);
    }

    // 좋아요 해제
    @Transactional
    public void removeLikedLecture(Integer lectureId, String email, String provider) {
        mypageLectureRepository.deleteLikeByLectureIdAndEmail(lectureId, email, provider);
    }


    // 내 강좌 조회 + 프로그래스
//    public Page<ReservationDto> getMyReservations(String email, Pageable pageable) {
//        Page<Reservation> reservations = mypageReservationRepository.findAllByEmail(email, pageable);
//
//        return reservations.map(r -> {
//            Lecture lecture = r.getLecture();
//
//            Double progressPercent = mypageProgressRepository
//                    .findByLectureAndEmail(lecture, email)
//                    .map(LectureProgress::getProgressPercent)
//                    .orElse(0.0);
//
//            return ReservationDto.of(r, lecture, progressPercent);
//        });
//    }

    public Page<ReservationDto> getMyReservations(String email, Pageable pageable) {
        Page<Reservation> reservations = mypageReservationRepository.findConfirmedByEmail(email, pageable);

        return reservations.map(reservation -> {
            Lecture lecture = reservation.getLecture();

            int progressPercent = mypageProgressRepository.findByLectureAndEmail(lecture, email)
                    .map(LectureProgress::getProgressPercent)
                    .orElse(0.0)
                    .intValue();

            return ReservationDto.of(reservation, lecture, progressPercent);
        });
    }



    // 내 강좌 예약 취소
    @Transactional
    public void removeReservationLecture(Integer lectureId, String email) {
        mypageReservationRepository.cancelByLectureIdAndEmail(lectureId, email);
    }

    // 내 리뷰 조회
    public Page<Review> getMyReviews(String email, String provider, Pageable pageable) {
        return mypageReviewRepository.findAllByEmail(email, provider, pageable);
    }


    // 현재 진행률 저장
    @Transactional
    public void saveProgress(String email, ProgressDto progressDto) {
        // 1️⃣ lectureId로 Lecture 엔티티 조회
        Lecture lecture = lectureRepository.findById(progressDto.getLectureId())
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found: " + progressDto.getLectureId()));

        // 2️⃣ 기존 진행 데이터 조회 or 새로 생성
        LectureProgress progress = mypageProgressRepository.findByLectureAndEmail(lecture, email)
                .orElseGet(() -> new LectureProgress(email, lecture));

        // 3️⃣ 값 갱신
        progress.setProgressPercent(progressDto.getProgressPercent());
        progress.setLastWatchedTime(progressDto.getLastWatchedTime());
        progress.setLastAccessedAt(LocalDateTime.now());

        // 4️⃣ 저장
        mypageProgressRepository.save(progress);
    }

}
