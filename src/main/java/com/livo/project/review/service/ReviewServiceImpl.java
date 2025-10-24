package com.livo.project.review.service;

import com.livo.project.review.domain.Review;
import com.livo.project.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public List<Review> getReviewsByLectureId(int lectureId) {
        return reviewRepository.findReviewsByLectureId(lectureId);
    }

    @Override
    public Double getAverageStarByLecture(int lectureId) {
        Double avg = reviewRepository.findAverageStarByLectureId(lectureId);
        return avg != null ? Math.round(avg * 10) / 10.0 : 0.0; // 소수점 한 자리 반올림
    }

    @Override
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public Page<Review> getReviewsByLectureIdPaged(int lectureId, Pageable pageable) {
        return reviewRepository.findReviewsByLectureIdPaged(lectureId, pageable);
    }

    // 단일 리뷰 조회
    @Override
    public Review getReviewById(int reviewUId) {
        return reviewRepository.findById(reviewUId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));
    }

    // 리뷰 수정 (Ajax 기반, DTO/엔티티 통째로 받기)
    @Override
    public void updateReview(int reviewUId, Review updatedReview, String email) {
        Review review = reviewRepository.findById(reviewUId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // 내용 수정 (Ajax로 받은 데이터 반영)
        review.setReviewStar(updatedReview.getReviewStar());
        review.setReviewContent(updatedReview.getReviewContent());

        // 수정 시각 업데이트 (이거 꼭 필요!)
        review.setUpdatedAt(new Date());

        // JPA 변경감지로 자동 업데이트됨
        reviewRepository.save(review);
    }

    // 리뷰 삭제
    @Override
    public void deleteReview(int reviewUId, String email) {
        Review review = reviewRepository.findById(reviewUId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        reviewRepository.delete(review);
    }
}
