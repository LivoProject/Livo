package com.livo.project.mypage.domain.dto;

import com.livo.project.lecture.domain.Lecture;
import com.livo.project.notice.domain.dto.NoticeDto;
import com.livo.project.notice.domain.entity.Notice;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MypageDto {

    // 유저 기본 정보
    private Long userId;
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String phone;
    private LocalDate birth;
    private String gender;
    private String role;
    private String joinDate;
    private Long joinDays;

    // 공지사항
    private List<NoticeDto> notices;

    // 추천 강좌
    private List<Lecture> recommendedLectures;
}
