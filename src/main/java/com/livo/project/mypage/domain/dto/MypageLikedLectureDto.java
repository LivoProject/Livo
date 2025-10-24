package com.livo.project.mypage.domain.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MypageLikedLectureDto {
    private Integer lectureId;
    private String title;
    private String tutorName;
    private int price;
    private String thumbnailUrl;
    private Double progressPercent;
    private boolean reserved;
}