package com.livo.project.admin.controller;

import com.livo.project.admin.service.FaqAdminService;
import com.livo.project.admin.service.FileService;
import com.livo.project.admin.service.LectureAdminService;
import com.livo.project.faq.domain.Faq;
import com.livo.project.faq.repository.FaqRepository;
import com.livo.project.faq.service.FaqService;
import com.livo.project.lecture.domain.Category;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.CategoryRepository;
import com.livo.project.lecture.service.LectureService;
import com.livo.project.notice.domain.entity.Notice;
import com.livo.project.notice.service.NoticeService;
import com.livo.project.report.domain.Report;
import com.livo.project.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("admin")
@Controller
public class AdminController {
    public final LectureService lectureService;
    private final FaqAdminService faqAdminService;
    private final FaqRepository faqRepository;
    private final FaqService faqService;
    private final LectureAdminService lectureAdminService;
    private final ReportService reportService;
    private final NoticeService noticeService;

    @GetMapping("/dashboard")
    public String showAdminPage(Model model){
        List<Lecture> recentLectures = lectureAdminService.getRecentLectures();
        model.addAttribute("recentLectures",recentLectures);
        List<Faq> recentFaqs = faqAdminService.getFaqTop5();
        model.addAttribute("recentFaqs",recentFaqs);
        List<Report> reports = reportService.getNotApprovedReport();
        model.addAttribute("reports",reports);
        List<Notice> notices = noticeService.getRecentNotices();
        model.addAttribute("notices",notices);
        return "admin/dashboard";
    }
    @GetMapping("")
    public String redirectToDashboard() {//추가한 메서드
        return "redirect:/admin/dashboard";
    }

//    @GetMapping("/notice")
//    public String showNoticePage(){
//        return "admin/noticePage";
//    }

    @GetMapping("/lecture")
    public String showLecturePage(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "9") int size,
                                   Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lecturePage = lectureService.getLecturePage(pageable);

        model.addAttribute("lecturePage", lecturePage);
        model.addAttribute("lectures", lecturePage.getContent());
        return "admin/lecturePage";
    }


    @GetMapping("/report")
    public String showReportPage(){
        return "admin/reportPage";
    }

    @GetMapping("/chart")
    public String showChartPage(){
        return "admin/chartPage";
    }

    @GetMapping("/play")
    public String showViewPage(){
        return "mypage/lecture-play";
    }
}
