package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.Category;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.LectureRepository;
import com.livo.project.lecture.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;
    private final CategoryRepository categoryRepository;

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
        return lectureRepository.findByCategory_CategoryId(categoryId);
    }

    @Override
    public List<Lecture> findAllByMainCategory(int mainCategoryId) {
        return lectureRepository.findAllByMainCategory(mainCategoryId);
    }

    @Override
    public Page<Lecture> getLecturePage(Pageable pageable) {
        return lectureRepository.findAll(pageable);
    }

    @Override
    public Page<Lecture> searchLecturePage(String keyword, Pageable pageable) {
        return lectureRepository.findByTitleContaining(keyword, pageable);
    }
}
