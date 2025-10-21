package com.livo.project.main.domain.dto;

import com.livo.project.lecture.domain.Lecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainLectureDto {

    private int lectureId;
    private String title;
    private String tutorName;
    private String content;
    private String thumbnail;
    private int price;

    public static MainLectureDto fromEntity(Lecture lecture) {
        return MainLectureDto.builder()
                .lectureId(lecture.getLectureId())
                .title(lecture.getTitle())
                .tutorName(lecture.getTutorName())
                .content(lecture.getContent())
                .price(lecture.getPrice())
                .build();
    }
}
