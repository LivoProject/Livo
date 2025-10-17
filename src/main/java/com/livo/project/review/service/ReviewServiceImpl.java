package com.livo.project.review.service;

import com.livo.project.review.domain.Review;
import com.livo.project.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
