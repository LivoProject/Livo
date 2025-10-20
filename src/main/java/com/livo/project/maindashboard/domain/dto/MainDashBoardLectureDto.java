package com.livo.project.maindashboard.domain.dto;

import com.livo.project.maindashboard.domain.entity.MainDashBoardLecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainDashBoardLectureDto {

    private int lectureId;
    private String title;
    private String tutorName;
    private String content;
    private String thumbnail;
    private int price;

    public static MainDashBoardLectureDto fromEntity(MainDashBoardLecture lecture) {
        return MainDashBoardLectureDto.builder()
                .lectureId(lecture.getLectureId())
                .title(lecture.getTitle())
                .tutorName(lecture.getTutorName())
                .content(lecture.getContent())
                .price(lecture.getPrice())
                .build();
    }
}
