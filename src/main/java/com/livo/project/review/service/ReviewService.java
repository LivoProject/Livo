package com.livo.project.review.service;

import com.livo.project.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    // 특정 강좌에 대한 모든 리뷰 조회
    List<Review> getReviewsByLectureId(int lectureId);

    // 특정 강좌에 대한 리뷰 평균
    Double getAverageStarByLecture(int lectureId);

    // 리뷰 등록
    Review saveReview(Review review);

    // 리뷰 페이징
    Page<Review> getReviewsByLectureIdPaged(int lectureId, Pageable pageable);

    // 단일 리뷰 조회 (수정 모달용)
    Review getReviewById(int reviewUId);

    // 리뷰 수정
    void updateReview(int reviewUId, int reviewStar, String reviewContent, String email);

    // 리뷰 삭제
    void deleteReview(int reviewUId, String email);
}
