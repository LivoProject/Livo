package com.livo.project.mypage.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDto {
    private Integer lectureId;
    private Integer progressPercent;
    private Double lastWatchedTime;
}
