package com.livo.project.mypage.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MypageLectureDto {
    private Integer lectureId;
    private String title;
    private String tutorName;
    private int price;
    private String thumbnailUrl;
    private int progressPercent;

}
