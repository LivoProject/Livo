package com.livo.project.review.controller;

import com.livo.project.auth.security.AppUserDetails;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lecture")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReservationService reservationService;

    // 리뷰 등록
    @PostMapping("/content/{lectureId}/review")
    public String saveReview(@PathVariable int lectureId,
                             @RequestParam("reviewStar") int reviewStar,
                             @RequestParam("reviewContent") String reviewContent,
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

        // 실제 수강신청 내역에서 reservationId 가져오기
        Integer reservationId = reservationService.findReservationIdByEmailAndLectureId(email, lectureId, provider);

        // 혹시 수강내역이 없으면 (안전하게 null 체크만)
        if (reservationId == null) {
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
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewService.getReviewsByLectureIdPaged(lectureId, pageable);

        // fromEntity()를 사용해 변환
        Page<ReviewDto> dtoPage = reviewPage.map(ReviewDto::fromEntity);

        // 로그인 상태 및 유저 이메일 추가 -> 신고하기 위해!
        boolean isLoggedIn = (authentication != null && authentication.isAuthenticated());
        String loggedInUserEmail = null;

        if (isLoggedIn) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof AppUserDetails appUser) {
                loggedInUserEmail = appUser.getEmail();
            } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
                loggedInUserEmail = (String) oAuthUser.getAttribute("email");
            }
        }

        // Map으로 묶어서 리턴 (JSON 구조)
        Map<String, Object> response = new HashMap<>();
        response.put("content", dtoPage.getContent());
        response.put("last", dtoPage.isLast());
        response.put("isLoggedIn", isLoggedIn);
        response.put("loggedInUserEmail", loggedInUserEmail);

        return response;
    }

    // 단일 리뷰 조회 (수정용: Ajax로 기존 내용 불러오기)
    @GetMapping("/review/{reviewUId}")
    @ResponseBody
    public ReviewDto getReview(@PathVariable int reviewUId) {
        Review review = reviewService.getReviewById(reviewUId);
        return ReviewDto.fromEntity(review);
    }

    // 리뷰 수정 (Ajax 기반)
    @PutMapping("/review/{reviewUId}")
    @ResponseBody
    public ResponseEntity<?> updateReview(@PathVariable int reviewUId,
                                          @RequestBody Review updatedReview,
                                          Authentication authentication) {

        Object principal = authentication.getPrincipal();
        String email = null;

        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
            email = (String) oAuthUser.getAttribute("email");
        }

        reviewService.updateReview(reviewUId, updatedReview, email);
        return ResponseEntity.ok("SUCCESS");
    }

    // 리뷰 삭제
    @DeleteMapping("/review/{reviewUId}")
    @ResponseBody
    public ResponseEntity<?> deleteReview(@PathVariable int reviewUId,
                                          Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String email = null;

        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
            email = (String) oAuthUser.getAttribute("email");
        }

        reviewService.deleteReview(reviewUId, email);
        return ResponseEntity.ok().build();
    }

}
