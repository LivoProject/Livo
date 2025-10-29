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
    private final ReservationRepository reservationRepository;
    private final CategoryRepository categoryRepository;

    // 전체 강좌 리스트
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "12") int size,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lecturePage = lectureService.getLecturePage(pageable);

        Map<Integer, Double> avgStarMap = new HashMap<>();
        Map<Integer, Integer> reviewCountMap = new HashMap<>();

        lecturePage.getContent().forEach(lecture -> {
            Double avgStar = reviewService.getAverageStarByLecture(lecture.getLectureId());
            int reviewCount = reviewService.getReviewsByLectureId(lecture.getLectureId()).size();
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

    // 키워드 검색
    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "12") int size,
                         Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lecturePage = (keyword != null && !keyword.trim().isEmpty())
                ? lectureService.searchLecturePage(keyword, pageable)
                : lectureService.getLecturePage(pageable);

        Map<Integer, Double> avgStarMap = new HashMap<>();
        Map<Integer, Integer> reviewCountMap = new HashMap<>();

        lecturePage.getContent().forEach(lecture -> {
            Double avgStar = reviewService.getAverageStarByLecture(lecture.getLectureId());
            int reviewCount = reviewService.getReviewsByLectureId(lecture.getLectureId()).size();
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

    //  비동기 필터링 (주제 / 세부분류 / 키워드)
    @GetMapping("/filter")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> filter(
            @RequestParam(value = "mainCategory", required = false) Integer mainCategory,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lecturePage;

        //  세부분류 선택 시
        if (subCategory != null && !subCategory.isEmpty()) {
            Category category = categoryRepository.findByCategoryName(subCategory);
            if (category != null) {
                lecturePage = (keyword != null && !keyword.isBlank())
                        ? lectureService.searchByCategoryAndKeyword(category.getCategoryId(), keyword, pageable)
                        : lectureService.getLecturePageByCategory(category.getCategoryId(), pageable);
            } else {
                lecturePage = Page.empty();
            }

            //  mainCategory 선택 시
        } else if (mainCategory != null) {
            lecturePage = (keyword != null && !keyword.isBlank())
                    ? lectureService.searchByMainCategoryAndKeyword(mainCategory, keyword, pageable)
                    : lectureService.getLecturePageByMainCategory(mainCategory, pageable);

            //  keyword만 있을 경우
        } else if (keyword != null && !keyword.isBlank()) {
            lecturePage = lectureService.searchLecturePage(keyword, pageable);

            //  아무 조건도 없을 경우
        } else {
            lecturePage = lectureService.getLecturePage(pageable);
        }

        //  JSON 응답
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
