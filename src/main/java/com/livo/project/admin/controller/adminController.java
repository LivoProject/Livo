package com.livo.project.admin.controller;

import com.livo.project.lecture.service.LectureService;
import com.livo.project.main.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("admin")
@Controller
public class adminController {
    private final LectureService lectureService;
    private final CategoryRepository categoryRepository;
    @GetMapping("/dashboard")
    public String showAdminPage(){
        return "admin/dashboard";
    }
    @GetMapping("/faq")
    public String showFaqPage(){
        return "admin/faqPage";
    }
    @GetMapping("/notice")
    public String showNoticePage(){
        return "admin/noticePage";
    }

    @GetMapping("/lecture")
    public String showLecturePage(){
        return "admin/lecturePage";
    }

    @GetMapping("/lecture/insert")
    public String showLectureForm(){
        return "admin/lectureForm";
    }

//    @PostMapping("/lecture/save")
//    public String saveLecture(@RequestParam("categoryId")int categoryId, Lecture lecture){
//        Optional<Category> category = categoryRepository.findById(categoryId);
//        lectureService.saveLecture(lecture);
//        return "redirect:admin/lectureForm";
//    }


    @GetMapping("/faq/insert")
    public String showFaqForm(){
        return "admin/faqForm";
    }

    @GetMapping("/report")
    public String showReportPage(){
        return "admin/reportPage";
    }

    @GetMapping("/chart")
    public String showChartPage(){
        return "admin/chartPage";
    }
}
