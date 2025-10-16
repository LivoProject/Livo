package com.livo.project.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("admin")
@Controller
public class adminController {
    @GetMapping("/dashboard")
    public String showAdminPage(){
        return "admin/dashboard";
    }
    @GetMapping("/faqPage")
    public String showFaqPage(){
        return "admin/faqPage";
    }
    @GetMapping("/noticePage")
    public String showNoticePage(){
        return "admin/noticePage";
    }

    @GetMapping("/lecturePage")
    public String showLecturePage(){
        return "admin/lecturePage";
    }

    @GetMapping("/lectureForm")
    public String showLectureForm(){
        return "admin/lectureForm";
    }

    @GetMapping("/faqForm")
    public String showFaqForm(){
        return "admin/faqForm";
    }

    @GetMapping("/reportPage")
    public String showReportPage(){
        return "admin/reportPage";
    }

    @GetMapping("/chartPage")
    public String showChartPage(){
        return "admin/chartPage";
    }
}
