package com.livo.project.review.controller;

import com.livo.project.review.domain.Review;
import com.livo.project.review.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lecture")
public class ReviewController {

    private final ReviewService reviewService;

    public  ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // 민영 리뷰 등록
    @PostMapping("/content/{lectureId}/review")
    public String saveReview(@PathVariable int lectureId,
                             @RequestParam("reviewStar") int reviewStar,
                             @RequestParam("reviewContent") String reviewContent) {

        String userEmail = "test@livo.com"; //임시 유저: 로그인 연결해야 해!!
        int reservationId = 1; // 나중에 진짜 세션 로직 생기면 교체

        Review review = new Review();
        review.setReservationId(reservationId);
        review.setReviewStar(reviewStar);
        review.setReviewContent(reviewContent);

        reviewService.saveReview(review);

        return "redirect:/lecture/content/" + lectureId;
    }


}
