package com.livo.project.lecture.controller;

import com.livo.project.lecture.domain.*;
import com.livo.project.lecture.service.*;
import com.livo.project.review.domain.Review;
import com.livo.project.review.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/lecture")
public class LectureController {

    private final LectureService lectureService;
    private final ChapterListService chapterListService;
    private final AttachmentService attachmentService;
    private final ReviewService reviewService;
    private final ReservationService reservationService;

    public LectureController(LectureService lectureService,
                             ChapterListService chapterListService,
                             AttachmentService attachmentService,
                             ReviewService reviewService, ReservationService reservationService) {
        this.lectureService = lectureService;
        this.chapterListService = chapterListService;
        this.attachmentService = attachmentService;
        this.reviewService = reviewService;
        this.reservationService = reservationService;
    }

    // 전체 강좌 리스트 (페이징 포함)
    @GetMapping("/list")
    public String list(
            @RequestParam(defaultValue = "0") int page,   // 페이지 번호 (0부터 시작)
            @RequestParam(defaultValue = "9") int size,   // 한 페이지당 9개씩
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lecturePage = lectureService.getLecturePage(pageable);

        model.addAttribute("lecturePage", lecturePage);
        model.addAttribute("lectures", lecturePage.getContent());
        return "lecture/list";
    }

    // 키워드 검색 (페이징 포함)
    @GetMapping("/search")
    public String search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lecturePage;

        // 키워드가 있으면 검색, 없으면 전체 목록
        if (keyword != null && !keyword.trim().isEmpty()) {
            lecturePage = lectureService.searchLecturePage(keyword, pageable);
        } else {
            lecturePage = lectureService.getLecturePage(pageable);
        }

        model.addAttribute("lecturePage", lecturePage);
        model.addAttribute("lectures", lecturePage.getContent());
        model.addAttribute("keyword", keyword);
        return "lecture/list";
    }

    // 통합 검색 (주제 / 세부분류 / 키워드)
    @GetMapping("/filter")
    public String filter(
            @RequestParam(value = "mainCategory", required = false) Integer mainCategory,
            @RequestParam(value = "subCategory", required = false) Integer subCategory,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<Lecture> results;

        if (subCategory != null) {
            results = lectureService.findByCategoryId(subCategory);
        } else if (mainCategory != null) {
            results = lectureService.findByCategoryId(mainCategory);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            results = lectureService.findByTitleContaining(keyword);
        } else {
            results = lectureService.findAll();
        }

        model.addAttribute("lectures", results);
        return "lecture/list";
    }

    // 카테고리별 검색
    @GetMapping("/category/{categoryId}")
    public String listByCategory(@PathVariable("categoryId") int categoryId, Model model) {
        List<Lecture> lectures = lectureService.findByCategoryId(categoryId);
        model.addAttribute("lectures", lectures);
        return "lecture/list";
    }

    // 강좌 상세 (강의 목록 + 리뷰 + 첨부파일 조회!!)
    @GetMapping("/content/{lectureId}")
    public String lectureContent(@PathVariable int lectureId, Model model) {
        Lecture lecture = lectureService.findById(lectureId).orElseThrow();

        //강의 목록
        List<ChapterList> chapters = chapterListService.getChaptersByLecture(lectureId);

        //리뷰 목록
        List<Review> reviews = reviewService.getReviewsByLectureId(lectureId);

        //리뷰 평균
        Double avgStar = reviewService.getAverageStarByLecture(lectureId);

        //첨부파일
        List<Attachment> attachments = attachmentService.getAttachmentsByLectureId(lectureId);

        model.addAttribute("lecture", lecture);
        model.addAttribute("chapters", chapters);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgStar", avgStar);
        model.addAttribute("attachments", attachments);

        return "lecture/content";
    }

    // 무료강의 수강 신청하면 DB 저장, 마이페이지로!! -> 더 수정 예정
    @PostMapping("/enroll/{lectureId}")
    public String enrollFreeLecture(@PathVariable int lectureId) {
        // 임시 유저 (로그인 연결 전)
        String userEmail = "test@livo.com";

        // 무료강의 수강신청 내역 DB 저장 => 근데 pending임!! 바로 confirmed로 가도록 수정 예정
        reservationService.saveReservation(lectureId, userEmail);

        // 마이페이지 URL로 리다이렉트
        return "#";
    }



}
