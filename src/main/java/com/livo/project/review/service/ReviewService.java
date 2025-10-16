package com.livo.project.review.service;

import com.livo.project.review.domain.Review;
import java.util.List;

public interface ReviewService {

    // 특정 강좌에 대한 모든 리뷰 조회
    List<Review> getReviewsByLectureId(int lectureId);

    // 특정 강좌에 대한 리뷰 평균
    Double getAverageStarByLecture(int lectureId);

    // 리뷰 등록
    Review saveReview(Review review);
}
