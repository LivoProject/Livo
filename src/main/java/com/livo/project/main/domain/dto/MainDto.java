package com.livo.project.main.domain.dto;

import com.livo.project.lecture.domain.Category;
import com.livo.project.main.domain.entity.MainLecture;
import com.livo.project.main.domain.entity.MainNotice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MainDto {
    private List<Category> categories;          // 카테고리 목록
    private List<MainLecture> recommendedLectures;  // 추천 강좌 (랜덤)
    //private List<Lecture> popularLectures;      // 인기 강좌 (즐겨찾기 수 or 평점)
    private List<MainNotice> notices; // 공지사항
}