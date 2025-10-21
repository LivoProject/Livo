package com.livo.project.main.service;

import com.livo.project.lecture.domain.Category;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.CategoryRepository;
import com.livo.project.main.domain.dto.MainDto;
import com.livo.project.main.repository.MainLectureRepository;
import com.livo.project.main.repository.MainNoticeRepository;
import com.livo.project.notice.domain.dto.NoticeDto;
import com.livo.project.notice.domain.entity.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {
    private final CategoryRepository mainCategoryRepository;
    private final MainLectureRepository mainLectureRepository;
    private final MainNoticeRepository mainNoticeRepository;

    public MainDto getMainPageData() {
        // 카테고리
        List<Category> categories = mainCategoryRepository.findByCategoryLevelOrderByCategoryOrderAsc(1);

        // 추천 강좌 (랜덤 4개)
        List<Lecture> recommended = mainLectureRepository.findRandomLectures();

        // 인기 강좌 (평점 높은 순)
        List<Lecture> popular = mainLectureRepository.findTop10LecturesByLikes();

        // 공지사항
        List<Notice> notices = mainNoticeRepository.findTop5ByOrderByCreatedAtDesc();
        List<NoticeDto> noticeDtos = notices.stream()
                .map(NoticeDto::fromEntity)
                .toList();

        return new MainDto(categories, recommended, popular, noticeDtos);
    }


}
