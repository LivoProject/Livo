package com.livo.project.review.domain.dto;

import com.livo.project.review.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private int reviewUId;         // 리뷰 고유 ID
    private int lectureId;         // 강의 ID
    private String userName;       // 작성자 이름
    private String userEmail;      // 작성자 이메일
    private int reviewStar;        // 별점
    private String reviewContent;  // 리뷰 내용
    private String createdAt;      // 작성일 포맷 문자열
    private boolean blocked;
    // Entity → DTO 변환 메서드
    public static ReviewDto fromEntity(Review review) {
        String formattedDate = null;
        if (review.getCreatedAt() != null) {
            formattedDate = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(review.getCreatedAt());
        }

        return new ReviewDto(
                review.getReviewUId(),
                review.getReservation().getLecture().getLectureId(),     // FK 접근
                review.getReservation().getUser().getName(),
                review.getReservation().getUser().getEmail(),
                review.getReviewStar(),
                review.getReviewContent(),
                formattedDate,
                review.isBlocked()

        );
    }
}
