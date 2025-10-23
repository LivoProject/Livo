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
    private Long lectureId;
    private String title;
    private String tutorName;
    private double progressPercent;
}
