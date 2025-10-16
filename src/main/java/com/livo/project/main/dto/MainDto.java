package com.livo.project.main.dto;

import com.livo.project.main.domain.Category;
import com.livo.project.main.domain.Lecture;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MainDTO {
    private List<Category> categories;          // 카테고리 목록
    private List<Lecture> recommendedLectures;  // 추천 강좌 (랜덤)
    //private List<Lecture> popularLectures;      // 인기 강좌 (즐겨찾기 수 or 평점)
}