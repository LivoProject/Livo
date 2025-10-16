package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.LectureRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;

    // 생성자 주입
    public LectureServiceImpl(@Qualifier("lectureLectureRepository")LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    @Override
    public List<Lecture> findAll() {
        return lectureRepository.findAll();
    }

    @Override
    public Optional<Lecture> findById(int lectureId) {
        return lectureRepository.findById(lectureId);
    }

    @Override
    public List<Lecture> findByCategoryId(int categoryId) {
        return lectureRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Lecture> findByTitleContaining(String keyword) {
        return lectureRepository.findByTitleContaining(keyword);
    }

    // ✅ 페이징용: 페이징은 나중에 다시 정리!!
    @Override
    public Page<Lecture> getLecturePage(Pageable pageable) {
        return lectureRepository.findAll(pageable);
    }

    // ✅ 검색 + 페이징 조합: 이것도 나중에 다시 정리!!
    @Override
    public Page<Lecture> searchLecturePage(String keyword, Pageable pageable) {
        return lectureRepository.findByTitleContaining(keyword, pageable);
    }
}
