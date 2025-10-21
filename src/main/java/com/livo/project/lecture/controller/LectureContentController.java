package com.livo.project.lecture.controller;

import com.livo.project.lecture.domain.Attachment;
import com.livo.project.lecture.domain.ChapterList;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.service.AttachmentService;
import com.livo.project.lecture.service.ChapterListService;
import com.livo.project.lecture.service.LectureService;
import com.livo.project.lecture.service.ReservationService;
import com.livo.project.review.domain.Review;
import com.livo.project.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/lecture")
public class LectureContentController {

    private final LectureService lectureService;
    private final ChapterListService chapterListService;
    private final AttachmentService attachmentService;
    private final ReviewService reviewService;
    private final ReservationService reservationService;

    // 강좌 상세 (강의 목록 + 리뷰 + 첨부파일 조회!!)
    @GetMapping("/content/{lectureId}")
    public String lectureContent(@PathVariable int lectureId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {

        Lecture lecture = lectureService.findById(lectureId).orElseThrow();

        //강의 목록
        List<ChapterList> chapters = chapterListService.getChaptersByLecture(lectureId);

        //리뷰 목록 (첫 5개만)
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewService.getReviewsByLectureIdPaged(lectureId, pageable);
        List<Review> reviews = reviewPage.getContent();

        // 평균별점 & 전체개수 (list.jsp 방식으로 똑같이!)
        Double avgStar = reviewService.getAverageStarByLecture(lectureId);
        int reviewCount = reviewService.getReviewsByLectureId(lectureId).size();

        Map<Integer, Double> avgStarMap = new HashMap<>();
        Map<Integer, Integer> reviewCountMap = new HashMap<>();
        avgStarMap.put(lectureId, avgStar != null ? avgStar : 0.0);
        reviewCountMap.put(lectureId, reviewCount);

        //로그인 및 수강여부 확인 -> 리뷰 작성 위해!
        boolean isLoggedIn = (userDetails != null);
        boolean isEnrolled = false;
        String loggedInUserEmail = null;

        if (isLoggedIn) {
            loggedInUserEmail = userDetails.getUsername();
            isEnrolled = reservationService.isUserEnrolled(lectureId, loggedInUserEmail);
        }

        //첨부파일
        List<Attachment> attachments = attachmentService.getAttachmentsByLectureId(lectureId);

        model.addAttribute("lecture", lecture);
        model.addAttribute("chapters", chapters);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgStarMap", avgStarMap);
        model.addAttribute("reviewCountMap", reviewCountMap);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("isEnrolled", isEnrolled);
        model.addAttribute("loggedInUserEmail", loggedInUserEmail);
        model.addAttribute("attachments", attachments);

        return "lecture/content";
    }

    // 무료강의 수강신청 -민영: 이미 신청한 강의일 경우 로직 추가해야해!
    @PostMapping("/enroll/{lectureId}")
    public String enrollFreeLecture(@PathVariable int lectureId,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/login"; // 로그인 안 되어 있으면 로그인 페이지로
        }

        String userEmail = userDetails.getUsername();

        // 무료강의 수강신청 내역 DB 저장 => 바로 confirmed!
        reservationService.saveReservation(lectureId, userEmail);

        // 마이페이지 리다이렉트
        return "redirect:/mypage";
    }

}
