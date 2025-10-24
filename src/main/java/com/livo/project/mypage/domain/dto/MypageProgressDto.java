package com.livo.project.mypage.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MypageProgressDto {
    private Integer lectureId;
    private Double progressPercent;
    private Double lastWatchedTime;
}
