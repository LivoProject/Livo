package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.LectureRepository;
import com.livo.project.lecture.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<Lecture> findAll() {
        return lectureRepository.findAll();
    }

    @Override
    public Optional<Lecture> findById(int lectureId) {
        return lectureRepository.findById(lectureId);
    }


    @Override
    public List<Lecture> findAllByMainCategory(int mainCategoryId) {
        // 리스트 버전 (다른 곳에서 필요할 수도 있음)
        return lectureRepository.findAllByMainCategory(mainCategoryId, Pageable.unpaged()).getContent();
    }

    // 페이징 기반 하위 카테고리 강좌 조회
    @Override
    public Page<Lecture> getLecturePageByCategory(int categoryId, Pageable pageable) {
        return lectureRepository.findByVisibilityAndCategory_CategoryId(Lecture.LectureVisibility.ACTIVE, categoryId, pageable);
    }

    // 페이징 기반 상위 카테고리(mainCategory) 조회
    @Override
    public Page<Lecture> getLecturePageByMainCategory(int mainCategoryId, Pageable pageable) {
        return lectureRepository.findAllByMainCategory(mainCategoryId, pageable);
    }

    @Override
    public Page<Lecture> getLecturePage(Pageable pageable) {
        return lectureRepository.findByVisibility(Lecture.LectureVisibility.ACTIVE, pageable);
    }

    @Override
    public Page<Lecture> searchLecturePage(String keyword, Pageable pageable) {
        return lectureRepository.findByVisibilityAndTitleContaining(Lecture.LectureVisibility.ACTIVE, keyword, pageable);
    }

    @Override
    public Page<Lecture> searchByCategoryAndKeyword(int categoryId, String keyword, Pageable pageable) {
        return lectureRepository.findByCategoryAndKeyword(categoryId, keyword, pageable);
    }

    @Override
    public Page<Lecture> searchByMainCategoryAndKeyword(int mainCategoryId, String keyword, Pageable pageable) {
        return lectureRepository.findByMainCategoryAndKeyword(mainCategoryId, keyword, pageable);
    }

    // 민영 또 추가
    @Override
    public Page<Lecture> getFilteredLecturePage(
            String filter,
            String mainCategory,
            String subCategory,
            String keyword,
            Pageable pageable) {

        // 기본 ACTIVE만
        Page<Lecture> lecturePage;

        // 1️⃣ 무료 강의
        if ("free".equals(filter)) {
            lecturePage = lectureRepository.findAll(
                    (root, query, cb) -> cb.and(
                            cb.equal(root.get("visibility"), Lecture.LectureVisibility.ACTIVE),
                            cb.or(
                                    cb.equal(root.get("price"), 0),
                                    cb.isTrue(root.get("isFree"))
                            )
                    ),
                    pageable
            );

            // 2️⃣ 인기순
        } else if ("popular".equals(filter)) {
            Pageable sortedPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "reservationCount"));
            lecturePage = lectureRepository.findByVisibility(Lecture.LectureVisibility.ACTIVE, sortedPage);

            // 3️⃣ 최신순
        } else if ("latest".equals(filter)) {
            Pageable sortedPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "lectureId"));
            lecturePage = lectureRepository.findByVisibility(Lecture.LectureVisibility.ACTIVE, sortedPage);

            // 4️⃣ 전체
        } else {
            lecturePage = lectureRepository.findByVisibility(Lecture.LectureVisibility.ACTIVE, pageable);
        }

        return lecturePage;
    }

}
