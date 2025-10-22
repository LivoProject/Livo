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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

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

        return "redirect:/lecture/content/" + lectureId + "?reviewed=success#review";
    }

    // 리뷰 페이징_ 더보기 방식! (JSON 응답)
    @GetMapping("/content/{lectureId}/reviews")
    @ResponseBody
    public Map<String, Object> getReviews(
            @PathVariable int lectureId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewService.getReviewsByLectureIdPaged(lectureId, pageable);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

        // Page<Review> → Page<ReviewDto> 변환
        Page<ReviewDto> dtoPage = reviewPage.map(r -> new ReviewDto(
                r.getReviewUId(),
                r.getReservation().getLecture().getLectureId(),
                r.getReservation().getUser().getName(),
                r.getReservation().getUser().getEmail(),
                r.getReviewStar(),
                r.getReviewContent(),
                sdf.format(r.getCreatedAt())
        ));

        // 로그인 상태 및 유저 이메일 추가 -> 신고하기 위해!
        boolean isLoggedIn = (userDetails != null);
        String loggedInUserEmail = isLoggedIn ? userDetails.getUsername() : null;

        // Map으로 묶어서 리턴 (JSON 구조)
        Map<String, Object> response = new HashMap<>();
        response.put("content", dtoPage.getContent());
        response.put("last", dtoPage.isLast());
        response.put("isLoggedIn", isLoggedIn);
        response.put("loggedInUserEmail", loggedInUserEmail);

        return response;
    }

    // 단일 리뷰 조회 (수정 모달용)
    @GetMapping("/review/{reviewUId}")
    @ResponseBody
    public ReviewDto getReview(@PathVariable int reviewUId) {
        Review review = reviewService.getReviewById(reviewUId);
        return ReviewDto.fromEntity(review);
    }

    // 리뷰 수정
    @PostMapping("/review/edit")
    public String editReview(@RequestParam("reviewUId") int reviewUId,
                             @RequestParam("reviewStar") int reviewStar,
                             @RequestParam("reviewContent") String reviewContent,
                             @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        reviewService.updateReview(reviewUId, reviewStar, reviewContent, email);

        // lectureId는 DTO 변환에서 사용하므로 다시 redirect 시 필요
        Review updatedReview = reviewService.getReviewById(reviewUId);
        int lectureId = updatedReview.getReservation().getLecture().getLectureId();

        return "redirect:/lecture/content/" + lectureId + "#review";
    }

    // 리뷰 삭제
    @DeleteMapping("/review/{reviewUId}")
    @ResponseBody
    public ResponseEntity<?> deleteReview(@PathVariable int reviewUId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        reviewService.deleteReview(reviewUId, email);
        return ResponseEntity.ok().build();
    }

}
