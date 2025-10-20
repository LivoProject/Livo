package com.livo.project.main.service;

import com.livo.project.lecture.domain.Category;
import com.livo.project.main.domain.dto.MainLectureDto;
import com.livo.project.main.domain.entity.MainLecture;
import com.livo.project.main.domain.entity.MainNotice;
import com.livo.project.main.domain.dto.MainDto;
import com.livo.project.lecture.CategoryRepository;
import com.livo.project.main.repository.MainLectureRepository;
import com.livo.project.main.repository.MainNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {
    private final CategoryRepository categoryRepository;
    private final MainLectureRepository mainLectureRepository;
    private final MainNoticeRepository mainNoticeRepository;

    public MainDto getMainPageData() {
        // 카테고리
        List<Category> categories = categoryRepository.findByCategoryLevelOrderByCategoryOrderAsc(1);

        // 추천 강좌 (랜덤 4개)
        List<MainLecture> recommended = mainLectureRepository.findRandomLectures();

        // 인기 강좌 (평점 높은 순)
        //List<Lecture> popular = lectureRepository.findTop5ByOrderByRatingDesc();

        // 공지사항
        List<MainNotice> notices = mainNoticeRepository.findTop5ByOrderByIsPinnedDescCreatedAtDesc();

        return new MainDto(categories, recommended, notices);
    }


}
