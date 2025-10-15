package com.livo.project.main.service;

import com.livo.project.main.domain.Category;
import com.livo.project.main.domain.Lecture;
import com.livo.project.main.dto.MainDTO;
import com.livo.project.main.repository.CategoryRepository;
import com.livo.project.main.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {
    private final CategoryRepository categoryRepository;
    private final LectureRepository lectureRepository;

    public MainDTO getMainPageData() {
        List<Category> categories = categoryRepository.findByCategoryLevelOrderByCategoryOrderAsc(1);
        List<Lecture> lectures = lectureRepository.findTop5ByOrderByLectureIdDesc();
        return new MainDTO(categories, lectures);
    }
}
