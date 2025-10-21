package com.livo.project.review.controller;

import com.livo.project.lecture.service.ReservationService;
import com.livo.project.review.domain.Review;
import com.livo.project.review.domain.dto.ReviewDto;
import com.livo.project.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lecture")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReservationService reservationService;

    // 민영 리뷰 등록
    @PostMapping("/content/{lectureId}/review")
    public String saveReview(@PathVariable int lectureId,
                             @RequestParam("reviewStar") int reviewStar,
                             @RequestParam("reviewContent") String reviewContent,
                             @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();

        // 실제 수강신청 내역에서 reservationId 가져오기
        Integer reservationId = reservationService.findReservationIdByEmailAndLectureId(userEmail, lectureId);

        // 혹시 수강내역이 없으면 (안전하게 null 체크만)
        if (reservationId == null) {
            // 폼 자체는 안 보이지만 혹시 직접 POST 접근한 경우 대비
            return "redirect:/error/unauthorized";
        }

        Review review = new Review();
        review.setReservationId(reservationId);
        review.setReviewStar(reviewStar);
        review.setReviewContent(reviewContent);

        reviewService.saveReview(review);

        return "redirect:/lecture/content/" + lectureId;
    }

    // 리뷰 페이징_ 더보기 방식! (JSON 응답)
    @GetMapping("/content/{lectureId}/reviews")
    @ResponseBody
    public Page<ReviewDto> getReviews(
            @PathVariable int lectureId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewService.getReviewsByLectureIdPaged(lectureId, pageable);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

        // Page<Review> → Page<ReviewDto> 변환 (순환참조 방지)
        return reviewPage.map(r -> new ReviewDto(
                r.getReviewUId(),
                r.getReservation().getUser().getName(),
                r.getReservation().getUser().getEmail(),
                r.getReviewStar(),
                r.getReviewContent(),
                sdf.format(r.getCreatedAt())
        ));
    }


}
