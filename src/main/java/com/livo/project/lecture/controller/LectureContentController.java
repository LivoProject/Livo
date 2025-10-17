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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

        //리뷰 목록
        List<Review> reviews = reviewService.getReviewsByLectureId(lectureId);

        //리뷰 평균
        Double avgStar = reviewService.getAverageStarByLecture(lectureId);

        //로그인 및 수강여부 확인 -> 리뷰 작성 위해!
        boolean isLoggedIn = (userDetails != null);
        boolean isEnrolled = false;
        if (isLoggedIn) {
            String email = userDetails.getUsername();
            isEnrolled = reservationService.isUserEnrolled(lectureId, email);
        }

        //첨부파일
        List<Attachment> attachments = attachmentService.getAttachmentsByLectureId(lectureId);


        model.addAttribute("lecture", lecture);
        model.addAttribute("chapters", chapters);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgStar", avgStar);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("isEnrolled", isEnrolled);
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
