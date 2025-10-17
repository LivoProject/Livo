package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.Category;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.LectureRepository;
import com.livo.project.main.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Lecture saveLecture(Lecture lecture, @RequestParam("categoryId") int categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        lecture.setCategory(category);
        return lectureRepository.save(lecture);

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
