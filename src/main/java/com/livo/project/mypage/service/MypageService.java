package com.livo.project.mypage.service;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;
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
    public MypageDto getUserData(String email) {
        User user = userRepository.findByEmail(email)
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

    // 일반 정보 수정 (이메일 기반)
    @Transactional
    public void updateUserProfile(String email, MypageDto dto) {
        User user = userRepository.findByEmail(email)
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

        userRepository.save(user);
        log.info("사용자 정보 수정 완료: {}", email);
    }

    // 비밀번호 변경
    @Transactional
    public void updateUserPassword(String currentPassword, String newPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("비밀번호 변경 완료: {}", email);
    }
}
