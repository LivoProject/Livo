package com.livo.project.lecture.service;

public interface LectureLikeService {

    //좋아요 토글
    boolean toggleLike(int lectureId, String email);

    //이 강좌 좋아요 눌렀는지 확인
    boolean isLiked(int lectureId, String email);
}
