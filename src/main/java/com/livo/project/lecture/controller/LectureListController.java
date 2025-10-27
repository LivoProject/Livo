package com.livo.project.lecture.controller;

import com.livo.project.lecture.domain.Category;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.CategoryRepository;
import com.livo.project.lecture.repository.ReservationRepository;
import com.livo.project.lecture.service.LectureService;
import com.livo.project.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/lecture")
public class LectureListController {

    private final LectureService lectureService;
    private final ReviewService reviewService;
    private final ReservationRepository reservationRepository; // 민영 추가 마지막!!
    private final CategoryRepository categoryRepository;

    // 전체 강좌 리스트 (페이징 포함)
    @GetMapping("/list")
    public String list(
            @RequestParam(defaultValue = "0") int page,   // 페이지 번호 (0부터 시작)
            @RequestParam(defaultValue = "12") int size,   // 한 페이지당 12개씩
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lecturePage = lectureService.getLecturePage(pageable);

        // 각 강좌별 평균 별점 + 리뷰 수 계산
        Map<Integer, Double> avgStarMap = new HashMap<>();
        Map<Integer, Integer> reviewCountMap = new HashMap<>();

        lecturePage.getContent().forEach(lecture -> {
            Double avgStar = reviewService.getAverageStarByLecture(lecture.getLectureId());
            int reviewCount = reviewService.getReviewsByLectureId(lecture.getLectureId()).size();

            // 민영 reservationCount 세팅
            int activeCount = reservationRepository.countActiveReservations(lecture.getLectureId());
            lecture.setReservationCount(activeCount);

            avgStarMap.put(lecture.getLectureId(), avgStar != null ? avgStar : 0.0);
            reviewCountMap.put(lecture.getLectureId(), reviewCount);
        });

        model.addAttribute("lecturePage", lecturePage);
        model.addAttribute("lectures", lecturePage.getContent());
        model.addAttribute("avgStarMap", avgStarMap);
        model.addAttribute("reviewCountMap", reviewCountMap);
        return "lecture/list";
    }

    // 키워드 검색 (페이징 포함)
    @GetMapping("/search")
    public String search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lecturePage;

        // 키워드가 있으면 검색, 없으면 전체 목록
        if (keyword != null && !keyword.trim().isEmpty()) {
            lecturePage = lectureService.searchLecturePage(keyword, pageable);
        } else {
            lecturePage = lectureService.getLecturePage(pageable);
        }

        // 각 강좌별 평균 별점 + 리뷰 수 계산 (list()과 동일)
        Map<Integer, Double> avgStarMap = new HashMap<>();
        Map<Integer, Integer> reviewCountMap = new HashMap<>();

        lecturePage.getContent().forEach(lecture -> {
            Double avgStar = reviewService.getAverageStarByLecture(lecture.getLectureId());
            int reviewCount = reviewService.getReviewsByLectureId(lecture.getLectureId()).size();

            // 검색 결과 페이지에도 reservationCount 세팅
            int activeCount = reservationRepository.countActiveReservations(lecture.getLectureId());
            lecture.setReservationCount(activeCount);

            avgStarMap.put(lecture.getLectureId(), avgStar != null ? avgStar : 0.0);
            reviewCountMap.put(lecture.getLectureId(), reviewCount);
        });

        model.addAttribute("lecturePage", lecturePage);
        model.addAttribute("lectures", lecturePage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("avgStarMap", avgStarMap);
        model.addAttribute("reviewCountMap", reviewCountMap);

        return "lecture/list";
    }

    // ✅ 비동기 필터링 (주제/세부분류) - 페이징 지원 버전
    @GetMapping("/filter")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> filter(
            @RequestParam(value = "mainCategory", required = false) Integer mainCategory,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lecturePage;

        if (subCategory != null && !subCategory.isEmpty()) {
            Category category = categoryRepository.findByCategoryName(subCategory);
            lecturePage = (category != null)
                    ? lectureService.getLecturePageByCategory(category.getCategoryId(), pageable)
                    : Page.empty();
        } else if (mainCategory != null) {
            lecturePage = lectureService.getLecturePageByMainCategory(mainCategory, pageable);
        } else {
            lecturePage = lectureService.getLecturePage(pageable);
        }

        // ✅ 프론트에서 JSP와 동일한 데이터 구성
        List<Map<String, Object>> lectureData = lecturePage.getContent().stream().map(lecture -> {
            Map<String, Object> map = new HashMap<>();
            map.put("lectureId", lecture.getLectureId());
            map.put("title", lecture.getTitle());
            map.put("tutorName", lecture.getTutorName());
            map.put("price", lecture.getPrice());
            map.put("thumbnailUrl", lecture.getThumbnailUrl());

            Double avgStar = reviewService.getAverageStarByLecture(lecture.getLectureId());
            int reviewCount = reviewService.getReviewsByLectureId(lecture.getLectureId()).size();
            int activeCount = reservationRepository.countActiveReservations(lecture.getLectureId());

            map.put("avgStar", avgStar != null ? avgStar : 0.0);
            map.put("reviewCount", reviewCount);
            map.put("reservationCount", activeCount);
            return map;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("lectures", lectureData);
        response.put("totalPages", lecturePage.getTotalPages());
        response.put("currentPage", lecturePage.getNumber());

        return ResponseEntity.ok(response);
    }
}

