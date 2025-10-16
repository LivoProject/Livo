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


        LocalDate created = user.getCreatedAt().toLocalDate();
        long days = ChronoUnit.DAYS.between(created, LocalDate.now());


        return MypageDto.builder()
                .userId(user.getId())
                .username(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .birth(user.getBirth())
                .role(String.valueOf(user.getRoleId()))
                .joinDate(created.toString())
                .joinDays(days)
                // .likedLectures(null) // 나중에 연동 시 교체
                //.reviews(null)
                .build();

        }
}