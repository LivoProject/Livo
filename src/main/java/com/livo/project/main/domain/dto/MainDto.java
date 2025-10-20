package com.livo.project.main.domain.dto;

import com.livo.project.lecture.domain.Category;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.notice.domain.dto.NoticeDto;
import com.livo.project.notice.domain.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MainDto {
    private List<Category> categories;          // 카테고리 목록
    private List<Lecture> recommendedLectures;  // 추천 강좌 (랜덤)
    //private List<Lecture> popularLectures;      // 인기 강좌 (즐겨찾기 수 or 평점)
    private List<NoticeDto> notices; // 공지사항
}