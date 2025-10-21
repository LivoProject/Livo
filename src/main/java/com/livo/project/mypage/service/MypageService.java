package com.livo.project.mypage.service;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;
import com.livo.project.auth.security.AppUserDetails;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.mypage.domain.dto.MypageDto;
import com.livo.project.mypage.repository.MypageLectureRepository;
import com.livo.project.mypage.repository.MypageNoticeRepository;
import com.livo.project.notice.domain.dto.NoticeDto;
import com.livo.project.notice.domain.entity.Notice;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 마이페이지 서비스
 * - 유저 데이터 조회 및 수정, 비밀번호 변경 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MypageNoticeRepository mypageNoticeRepository;
    private final MypageLectureRepository mypageLectureRepository;

    // 마이페이지 기본 데이터 조회
    public MypageDto getUserData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AppUserDetails) auth.getPrincipal()).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        // 공지사항
        List<Notice> notices = mypageNoticeRepository.findTop5ByOrderByCreatedAtDesc();
        List<NoticeDto> noticeDtos = notices.stream()
                .map(NoticeDto::fromEntity)
                .toList();

        // 추천 강좌
        List<Lecture> recommended = mypageLectureRepository.findRandomLectures();

        return MypageDto.builder()
                .userId(user.getId())
                .username(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .birth(user.getBirth())
                .gender(user.getGender() != null ? user.getGender().toString() : null)
                .joinDate(user.getCreatedAt() != null ? user.getCreatedAt().toLocalDate().toString() : "")
                .notices(noticeDtos)
                .recommendedLectures(recommended)
                .build();
    }

    /** 프로필 수정 (ID 기반) */
    @Transactional
    public void updateUserProfile(MypageDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AppUserDetails) auth.getPrincipal()).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (dto.getNickname() != null && !dto.getNickname().isBlank()) user.setNickname(dto.getNickname());
        if (dto.getPhone() != null && !dto.getPhone().isBlank())       user.setPhone(dto.getPhone());

        // birth: DTO가 LocalDate면 그대로, String만 있다면 주석처럼 파싱
        if (dto.getBirth() != null) {
            user.setBirth(dto.getBirth());
        }
        // if (dto.getBirthStr() != null && !dto.getBirthStr().isBlank()) {
        //     user.setBirth(LocalDate.parse(dto.getBirthStr()));
        // }

        if (dto.getUsername() != null && !dto.getUsername().isBlank()) user.setName(dto.getUsername());

        // 성별: 리플렉션 제거, enum 직접 매핑
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            try {
                user.setGender(User.Gender.valueOf(dto.getGender().trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 gender 값: {}", dto.getGender());
            }
        }

        userRepository.save(user);
        log.info("사용자 정보 수정 완료: id={}", userId);
    }

    //  비밀번호 변경 (ID 기반)
    @Transactional
    public void updateUserPassword(String currentPassword, String newPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AppUserDetails) auth.getPrincipal()).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("비밀번호 변경 완료: id={}", userId);
    }
}