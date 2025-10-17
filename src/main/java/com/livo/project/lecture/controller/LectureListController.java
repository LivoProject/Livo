package com.livo.project.lecture.controller;

import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/lecture")
public class LectureListController {

    private final LectureService lectureService;

    // 전체 강좌 리스트 (페이징 포함)
    @GetMapping("/list")
    public String list(
            @RequestParam(defaultValue = "0") int page,   // 페이지 번호 (0부터 시작)
            @RequestParam(defaultValue = "10") int size,   // 한 페이지당 9개씩
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lecturePage = lectureService.getLecturePage(pageable);

        model.addAttribute("lecturePage", lecturePage);
        model.addAttribute("lectures", lecturePage.getContent());
        return "lecture/list";
    }

    // 키워드 검색 (페이징 포함)
    @GetMapping("/search")
    public String search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lecturePage;

        // 키워드가 있으면 검색, 없으면 전체 목록
        if (keyword != null && !keyword.trim().isEmpty()) {
            lecturePage = lectureService.searchLecturePage(keyword, pageable);
        } else {
            lecturePage = lectureService.getLecturePage(pageable);
        }

        model.addAttribute("lecturePage", lecturePage);
        model.addAttribute("lectures", lecturePage.getContent());
        model.addAttribute("keyword", keyword);
        return "lecture/list";
    }

    // 통합 검색 (주제 / 세부분류 / 키워드)
    @GetMapping("/filter")
    public String filter(
            @RequestParam(value = "mainCategory", required = false) Integer mainCategory,
            @RequestParam(value = "subCategory", required = false) Integer subCategory,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<Lecture> results;

        if (subCategory != null) {
            results = lectureService.findByCategoryId(subCategory);
        } else if (mainCategory != null) {
            results = lectureService.findByCategoryId(mainCategory);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            results = lectureService.findByTitleContaining(keyword);
        } else {
            results = lectureService.findAll();
        }

        model.addAttribute("lectures", results);
        return "lecture/list";
    }

    // 카테고리별 검색
    @GetMapping("/category/{categoryId}")
    public String listByCategory(@PathVariable("categoryId") int categoryId, Model model) {
        List<Lecture> lectures = lectureService.findByCategoryId(categoryId);
        model.addAttribute("lectures", lectures);
        return "lecture/list";
    }
}
