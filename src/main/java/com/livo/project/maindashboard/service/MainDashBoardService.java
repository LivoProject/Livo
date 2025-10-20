package com.livo.project.maindashboard.service;

import com.livo.project.lecture.domain.Category;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.maindashboard.domain.dto.MainDashBoardLectureDto;
import com.livo.project.maindashboard.domain.entity.MainDashBoardLecture;
import com.livo.project.maindashboard.domain.entity.MainDashBoardNotice;
import com.livo.project.maindashboard.domain.dto.MainDashBoardDto;
import com.livo.project.lecture.CategoryRepository;
import com.livo.project.maindashboard.repository.MainDashBoardLectureRepository;
import com.livo.project.maindashboard.repository.MainDashBoardNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainDashBoardService {
    private final CategoryRepository categoryRepository;
    private final MainDashBoardLectureRepository mainDashBoardlectureRepository;
    private final MainDashBoardNoticeRepository mainDashBoardnoticeRepository;

    public MainDashBoardDto getMainPageData() {
        // 카테고리
        List<Category> categories = categoryRepository.findByCategoryLevelOrderByCategoryOrderAsc(1);

        // 추천 강좌 (랜덤 4개)
        List<MainDashBoardLecture> recommended = mainDashBoardlectureRepository.findRandomLectures();

        // 인기 강좌 (평점 높은 순)
        //List<Lecture> popular = lectureRepository.findTop5ByOrderByRatingDesc();

        // 공지사항
        List<MainDashBoardNotice> notices = mainDashBoardnoticeRepository.findTop5ByOrderByIsPinnedDescCreatedAtDesc();

        return new MainDashBoardDto(categories, recommended, notices);
    }

    // 검색용 강의 리스트 반환 (DTO로)
    public List<MainDashBoardLectureDto> getAllLectureDtos() {
        return mainDashBoardlectureRepository.findAll()
                .stream()
                .map(MainDashBoardLectureDto::fromEntity)
                .toList();
    }
}
