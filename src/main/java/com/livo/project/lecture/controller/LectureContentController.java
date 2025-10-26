package com.livo.project.lecture.controller;

import com.livo.project.auth.security.AppUserDetails;
import com.livo.project.lecture.domain.Attachment;
import com.livo.project.lecture.domain.ChapterList;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.domain.Reservation;
import com.livo.project.lecture.service.AttachmentService;
import com.livo.project.lecture.service.ChapterListService;
import com.livo.project.lecture.service.LectureService;
import com.livo.project.lecture.service.ReservationService;
import com.livo.project.report.service.ReportService;
import com.livo.project.review.domain.Review;
import com.livo.project.review.domain.dto.ReviewDto;
import com.livo.project.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/lecture")
public class LectureContentController {

    private final LectureService lectureService;
    private final ChapterListService chapterListService;
    private final AttachmentService attachmentService;
    private final ReviewService reviewService;
    private final ReservationService reservationService;
    private final ReportService reportService;

    // 강좌 상세 (강의 목록 + 리뷰 + 첨부파일 조회!!)
    @GetMapping("/content/{lectureId}")
    public String lectureContent(@PathVariable int lectureId,
                                 Authentication authentication,
                                 Model model) {

        Lecture lecture = lectureService.findById(lectureId).orElseThrow();

        //강의 목록
        List<ChapterList> chapters = chapterListService.getChaptersByLecture(lectureId);

        //리뷰 목록 (첫 5개만)
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewService.getReviewsByLectureIdPaged(lectureId, pageable);

        // Review → ReviewDto 변환
        List<ReviewDto> reviews = reviewPage.getContent()
                .stream()
                .map(ReviewDto::fromEntity)
                .toList();

        // 평균별점 & 전체개수 (list.jsp 방식으로 똑같이!)
        Double avgStar = reviewService.getAverageStarByLecture(lectureId);
        int reviewCount = reviewService.getReviewsByLectureId(lectureId).size();

        Map<Integer, Double> avgStarMap = new HashMap<>();
        Map<Integer, Integer> reviewCountMap = new HashMap<>();
        avgStarMap.put(lectureId, avgStar != null ? avgStar : 0.0);
        reviewCountMap.put(lectureId, reviewCount);

        //로그인 및 수강여부 확인 -> 리뷰 작성 위해!
        boolean isLoggedIn = (authentication != null && authentication.isAuthenticated());
        boolean isEnrolled = false;
        String email = null;
        String provider = null;
        Reservation.ReservationStatus reservationStatus = null;

        if (isLoggedIn) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof AppUserDetails appUser) {
                email = appUser.getEmail();
                provider = appUser.getProvider();
            } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
                email = (String) oAuthUser.getAttribute("email");
                provider = (String) oAuthUser.getAttribute("provider");
            }

            if (email != null) {
                isEnrolled = reservationService.isUserEnrolled(lectureId, email, provider);
                reservationStatus = reservationService.getReservationStatus(email, lectureId);
            }
        }

        // 추가 부분 : 현재 사용자가 신고한 리뷰 목록
        Set<Integer> reportedIds = Collections.emptySet();
        if (isLoggedIn) {
            reportedIds = reportService.getReportedReviewIdsByUser(email);
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
        model.addAttribute("loggedInUserEmail", email);
        model.addAttribute("attachments", attachments);
        model.addAttribute("reportedIds", reportedIds);
        model.addAttribute("reservationStatus", reservationStatus);

        return "lecture/content";
    }

    // 무료강의 수강신청
    @PostMapping("/enroll/{lectureId}")
    public String enrollFreeLecture(@PathVariable int lectureId,
                                    Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        Object principal = authentication.getPrincipal();
        String email = null;
        String provider = null;

        // 로컬 로그인
        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
            provider = appUser.getProvider();
        }
        // 소셜 로그인
        else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
            email = (String) oAuthUser.getAttribute("email");
            provider = (String) oAuthUser.getAttribute("provider");
        }

        if (email == null) {
            return "redirect:/auth/login";
        }

        // 무료강의 수강신청 DB 저장
        reservationService.saveReservation(lectureId, email, provider);

        return "redirect:/lecture/content/" + lectureId + "?enrolled=success";
    }

}
