package com.livo.project.mypage.service;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;
import com.livo.project.mypage.dto.MypageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;

    public MypageDto getUserData(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저 없음"));


        // 가입일수 계산
        LocalDate created = user.getCreatedAt().toLocalDate();
        long days = ChronoUnit.DAYS.between(created, LocalDate.now());

        // 폰번호 010 형식으로 변환
        // 앞에 +01 를 0 으로 바꾸면 됨
        // 인덱스 0ㅂ

        return MypageDto.builder()
                .userId(user.getId())
                .username(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .birth(user.getBirth())
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .role(String.valueOf(user.getRoleId()))
                .joinDate(created.toString())
                .joinDays(days)
                // .likedLectures(null) // 나중에 연동 시 교체
                //.reviews(null)
                .build();

        }
}