package com.livo.project.main.service;

import com.livo.project.main.domain.Category;
import com.livo.project.main.domain.Lecture;
import com.livo.project.main.dto.MainDTO;
import com.livo.project.main.repository.CategoryRepository;
import com.livo.project.main.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {
    private final CategoryRepository categoryRepository;
    private final LectureRepository lectureRepository;

    public MainDTO getMainPageData() {
        // 1️⃣ 카테고리
        List<Category> categories = categoryRepository.findByCategoryLevelOrderByCategoryOrderAsc(1);

        // 2️⃣ 추천 강좌 (랜덤 4개)
        List<Lecture> recommended = lectureRepository.findRandomLectures();

        // 3️⃣ 인기 강좌 (평점 높은 순)
        //List<Lecture> popular = lectureRepository.findTop5ByOrderByRatingDesc();

        return new MainDTO(categories, recommended);
    }
}
