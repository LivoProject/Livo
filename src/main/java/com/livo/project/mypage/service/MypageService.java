package com.livo.project.mypage.service;

import com.livo.project.auth.domain.entity.User;

import com.livo.project.auth.repository.UserRepository;
import com.livo.project.auth.security.AppUserDetails;

import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.domain.Reservation;
import com.livo.project.lecture.repository.LectureRepository;
import com.livo.project.mypage.domain.dto.*;
import com.livo.project.mypage.domain.entity.LectureProgress;
import com.livo.project.mypage.repository.*;
import com.livo.project.mypage.repository.projection.LikedLectureProjection;
import com.livo.project.notice.domain.dto.NoticeDto;
import com.livo.project.notice.domain.entity.Notice;
import com.livo.project.payment.domain.Payment;
import com.livo.project.review.domain.Review;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


import java.time.LocalDate;
import java.util.List;

import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;


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
    private final MypagePaymentRepository mypagePaymentRepository;


    /**
     * 현재 인증 정보에서 (provider, providerId) 또는 id 추출
     */
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
            } else if (attrs.get("response") instanceof Map<?, ?> resp && resp.get("id") != null) {
                providerId = String.valueOf(resp.get("id"));               // naver
            }

            String email = null;
            if (attrs.get("email") != null) {
                email = String.valueOf(attrs.get("email"));
            } else if (attrs.get("kakao_account") instanceof Map<?, ?> ka && ka.get("email") != null) {
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

    /**
     * 공통: 현재 로그인 사용자 로드
     */
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
        List<Notice> notices = mypageNoticeRepository.findTop3ByOrderByCreatedAtDesc();
        List<NoticeDto> noticeDtos = notices.stream().map(NoticeDto::fromEntity).toList();

        // 추천 강좌
//        List<Lecture> recommended = mypageLectureRepository.findRandomLectures();

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
                //.recommendedLectures(recommended)
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .build();
    }

    // 프로필 수정
    @Transactional
    public void updateUserProfile(MypageDto dto) {
        User user = loadCurrentUser();

        if (dto.getNickname() != null && !dto.getNickname().isBlank()) user.setNickname(dto.getNickname());
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) user.setPhone(dto.getPhone());
        if (dto.getBirth() != null) user.setBirth(dto.getBirth());
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) user.setName(dto.getUsername());
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            try {
                user.setGender(User.Gender.valueOf(dto.getGender().trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 gender 값: {}", dto.getGender());
            }
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
    public Page<MypageLikedLectureDto> getLikedLecturesWithProgress(String email, String provider, Pageable pageable) {
        Page<LikedLectureProjection> results = mypageLectureRepository.findLikedLecturesWithProgress(email, provider, pageable);


        return results.map(row -> {
            Integer lectureId = row.getLectureId();

            boolean isReserved = mypageReservationRepository.existsByLecture_LectureIdAndUser_Email(lectureId, email);

            return MypageLikedLectureDto.builder()
                    .lectureId(lectureId)
                    .title(row.getTitle())
                    .tutorName(row.getTutorName())
                    .price(row.getPrice() == null ? 0 : row.getPrice())
                    .thumbnailUrl(row.getThumbnailUrl())
                    .progressPercent(row.getProgressPercent() != null ? row.getProgressPercent() : 0.0)
                    .reserved(row.getReserved() != null && row.getReserved() == 1)
                    .build();
        });
    }

    // 좋아요 강의 2개
    public List<LikedLectureProjection> getTop2LikedLectures(String email, String provider) {
        return mypageLectureRepository.findTop2LikedLecturesByEmailWithProgress(email, provider);
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

    public Page<MypageReservationDto> getMyReservations(String email, String provider, Pageable pageable) {
        Page<Reservation> reservations = mypageReservationRepository.findAllByEmailAndProvider(email, provider, pageable);

        return reservations.map(reservation -> {
            Lecture lecture = reservation.getLecture();

            int progressPercent = mypageProgressRepository.findByLectureAndUser(lecture, email, provider)
                    .map(LectureProgress::getProgressPercent)
                    .orElse(0.0)
                    .intValue();

            return MypageReservationDto.of(reservation, lecture, progressPercent);
        });
    }


    // 내 강좌 예약 취소
    @Transactional
    @CacheEvict(cacheNames = {"chartTopLectures", "chartMonthlyRevenue"}, allEntries = true)
    public void removeReservationLecture(Integer lectureId, String email) {
        int changed = mypageReservationRepository.cancelByLectureIdAndEmail(lectureId, email);
        System.out.println("변경된 행 수: " + changed);
        if (changed == 0) {
            throw new RuntimeException("해당 예약을 찾을 수 없거나 이미 취소되었습니다.");
        }
    }


    // 내 리뷰 조회
    public Page<Review> getMyReviews(String email, Pageable pageable) {
        return mypageReviewRepository.findAllByEmail(email, pageable);
    }


    // 현재 진행률 저장
    @Transactional
    public void saveProgress(String email, String provider, MypageProgressDto mypageProgressDto) {
        // lectureId로 Lecture 엔티티 조회
        Lecture lecture = lectureRepository.findById(mypageProgressDto.getLectureId())
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found: " + mypageProgressDto.getLectureId()));

        // 기존 진행 데이터 조회 or 새로 생성
        LectureProgress progress = mypageProgressRepository.findByLectureAndUser(lecture, email, provider)
                .orElseGet(() -> new LectureProgress(email, lecture));

        // 값 갱신
        progress.setProgressPercent(mypageProgressDto.getProgressPercent());
        progress.setLastWatchedTime(mypageProgressDto.getLastWatchedTime());
        progress.setLastAccessedAt(LocalDateTime.now());

        // 저장
        mypageProgressRepository.save(progress);
    }

    // 유저별 진행률 가져오기
    public MypageProgressDto getUserProgress(String email, String provider, int lectureId) {
        if (email == null) return null;

        return mypageProgressRepository.findByLectureAndUser(
                new Lecture(lectureId),
                email,
                provider
        ).map(progress -> new MypageProgressDto(
                progress.getLecture().getLectureId(),
                progress.getProgressPercent(),
                progress.getLastWatchedTime()
        )).orElse(null);
    }

    // 최근 학습한 강의
    @Transactional(readOnly = true)
    public LectureProgress getRecentLecture(String email) {
        List<LectureProgress> list = mypageProgressRepository.findTopWithLectureByEmail(email);
        return list.isEmpty() ? null : list.get(0);
    }

    // 학습 목표
    public int getTotalStudyHours(String email) {
        Double totalSeconds = mypageProgressRepository.sumTotalWatchedTime(email);
        if (totalSeconds == null) return 0;  // ✅ null-safe 처리
        return (int) (totalSeconds / 3600);
    }

    public int getCompletedLectureCount(String email) {
        Integer count = mypageProgressRepository.countByEmailAndProgressPercentGreaterThanEqual(email, 100.0);
        return (count != null) ? count : 0;
    }

    public int getStudyDaysThisMonth(String email) {
        return mypageProgressRepository.countDistinctDaysThisMonth(email);
    }

    // 진행중인 강의
    @Transactional(readOnly = true)
    public long getInProgressLectureCount(String email) {
        return mypageProgressRepository.countInProgressByEmail(email);
    }


    // 이번주 진행 시간
    @Transactional(readOnly = true)
    public double getWeeklyStudyHours(String email) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        LocalDateTime endOfWeek = now.with(DayOfWeek.SUNDAY).toLocalDate().atTime(23, 59, 59);

        Double totalSeconds = mypageProgressRepository.sumWeeklyStudySeconds(email, startOfWeek, endOfWeek);
        return totalSeconds != null ? totalSeconds / 3600.0 : 0.0; // 초 → 시간
    }

    // 내 결제 내역
    @Transactional
    public Page<Payment> getMyPayments(String email, Pageable pageable) {
        return mypagePaymentRepository.findAllByEmail(email, pageable);
    }

    public List<Payment> getRecentPayments(String email, int limit) {
        return mypagePaymentRepository.findTop3ByUserEmailOrderByApprovedAtDesc(email);
    }


    // 예약완료된 2개 강의
    @Transactional(readOnly = true)
    public List<MypageReservationDto> getRecentConfirmedLectures(String email, String provider) {
        Pageable limit = PageRequest.of(0, 3);
        List<Reservation> reservations =
                mypageReservationRepository.findTop3ConfirmedByEmailAndProvider(email, provider, limit);

        return reservations.stream()
                .map(r -> {
                    Lecture lecture = r.getLecture();


                    double progress = mypageProgressRepository.findByLectureAndUser(lecture, email, provider)
                            .map(LectureProgress::getProgressPercent)
                            .orElse(0.0);

                    return MypageReservationDto.of(r, lecture, progress);
                })
                .collect(Collectors.toList());
    }

    // 내 강좌 검색
    // Service
    @Transactional(readOnly = true)
    public Page<MypageReservationDto> searchMyReservations(
            String email, String provider, String keyword, String sort, Pageable pageable
    ) {
        if ("popular".equals(sort)) {
            // 인기순은 아래 별도 메서드 사용 (쿼리 내부 ORDER BY)
            Pageable unSorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            return mypageReservationRepository.findMyReservationsOrderByLikes(email, provider, keyword, unSorted);
        }

        // 최신/오래된 — Pageable 정렬로 처리 (결제일)
        Sort.Direction dir = "old".equals(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(dir, "r.createdAt")); // JPA가 alias를 인식 못하면 "createdAt"만 써도 됨

        return mypageReservationRepository.findMyReservationsForList(email, provider, keyword, sortedPageable);
    }

    // 좋아요 한 강좌 정렬
    public List<LikedLectureProjection> getLikedLectures(
            String email,
            String provider,
            String sort
    ) {
        return mypageLectureRepository.findLikedLecturesDynamicSortSimple(email, provider, sort);
    }

}

