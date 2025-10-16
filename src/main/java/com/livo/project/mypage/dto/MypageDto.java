package com.livo.project.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MypageDto {

    //  유저 기본 정보
    private Long userId;
    private String username;
    private String email;
    private String nickname;
    private String phone;
    private LocalDate birth;
    private String gender;
    private String role;
    private String joinDate;
    private Long joinDays;


    // 좋아요한 강의 목록
    // private List<Lecture> likedLectures;

    // 작성한 리뷰 목록
    //private List<Review> reviews;


}
