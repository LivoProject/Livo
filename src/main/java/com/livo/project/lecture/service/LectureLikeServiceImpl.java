package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.LectureLike;
import com.livo.project.lecture.repository.LectureLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LectureLikeServiceImpl implements LectureLikeService {

    private final LectureLikeRepository lectureLikeRepository;

    @Override
    public boolean toggleLike(int lectureId, String email) {
        boolean alreadyLiked = lectureLikeRepository.existsByLectureIdAndEmail(lectureId, email);

        if(alreadyLiked) {
            // 이미 눌렀으면 좋아요 취소
            lectureLikeRepository.deleteByLectureIdAndEmail(lectureId, email);
            return false;
        } else {
            // 안 눌렀으면 좋아요 추가
            LectureLike newLike = new LectureLike();
            newLike.setLectureId(lectureId);
            newLike.setEmail(email);
            lectureLikeRepository.save(newLike);
            return true;
        }
    }

    @Override
    public boolean isLiked(int lectureId, String email) {
        return lectureLikeRepository.existsByLectureIdAndEmail(lectureId, email);
    }
}
