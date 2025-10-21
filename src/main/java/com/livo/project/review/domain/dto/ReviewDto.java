package com.livo.project.review.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDto {
    private int reviewUId;
    private String userName;
    private String userEmail;
    private int reviewStar;
    private String reviewContent;
    private String createdAt;
}
