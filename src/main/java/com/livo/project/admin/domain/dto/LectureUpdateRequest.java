package com.livo.project.admin.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LectureUpdateRequest {
    private Integer lectureId;

    private String title;
    private String tutorName;
    private String tutorInfo;
    private String content;

    private Boolean isFree;

    private Integer totalCount;
    private Integer price;

    private LocalDate lectureStart;
    private LocalDate lectureEnd;

    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;

    private Integer categoryId;
}
