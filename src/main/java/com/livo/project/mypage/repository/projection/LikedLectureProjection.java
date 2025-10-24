package com.livo.project.mypage.repository.projection;

public interface LikedLectureProjection {
    Integer getLectureId();
    String getTitle();
    String getTutorName();
    String getPrice();
    String getThumbnailUrl();
    Double getProgressPercent();
}