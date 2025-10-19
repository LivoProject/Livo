package com.livo.project.maindashboard.domain.dto;

import com.livo.project.lecture.domain.Category;
import com.livo.project.maindashboard.domain.entity.MainDashBoardLecture;
import com.livo.project.maindashboard.domain.entity.MainDashBoardNotice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MainDashBoardDto {
    private List<Category> categories;          // 카테고리 목록
    private List<MainDashBoardLecture> recommendedLectures;  // 추천 강좌 (랜덤)
    //private List<Lecture> popularLectures;      // 인기 강좌 (즐겨찾기 수 or 평점)
    private List<MainDashBoardNotice> notices; // 공지사항
}