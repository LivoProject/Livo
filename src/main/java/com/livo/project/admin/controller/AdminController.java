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
    private final LectureAdminService lectureAdminService;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;
    private final FaqAdminService faqAdminService;
    private final FaqRepository faqRepository;
    private final FaqService faqService;

    @GetMapping("/dashboard")
    public String showAdminPage(){
        return "admin/dashboard";
    }
    @GetMapping("")
    public String redirectToDashboard() {//추가한 메서드
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/faq")
    public String showFaqPage(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "9") int size,
                              Model model){
        Pageable pageable = PageRequest.of(page, size);
        Page<Faq> faqPage = faqAdminService.getFaqPage(pageable);

        model.addAttribute("faqPage",faqPage);
        model.addAttribute("faq",faqPage.getContent());
        return "admin/faqPage";
    }
    @GetMapping("/notice")
    public String showNoticePage(){
        return "admin/noticePage";
    }

    @GetMapping("/lecture")
    public String adminLectureList(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "9") int size,
                                   Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lecturePage = lectureService.getLecturePage(pageable);

        model.addAttribute("lecturePage", lecturePage);
        model.addAttribute("lectures", lecturePage.getContent());
        return "admin/lecturePage";
    }

    @GetMapping("/lecture/insert")
    public String showLectureForm(Model model){
        List<Category> parents = categoryRepository.findByParentIsNull();
        model.addAttribute("parents",parents);
        return "admin/lectureForm";
    }

    @PostMapping("/lecture/save")
    @ResponseBody
    public ResponseEntity<?> saveLecture(@RequestParam("categoryId")int categoryId, @ModelAttribute Lecture lecture){
        Lecture saved = lectureAdminService.saveLecture(lecture, categoryId);
        return ResponseEntity.ok(Map.of("lectureId", saved.getLectureId()));
    }

    @PostMapping("/lecture/uploadImage")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file){
        return fileService.saveFile(file);
    }

    @PostMapping("/lecture/delete")
    public String deleteLecture(@RequestParam("lectureId") int id){
        lectureAdminService.deleteLecture(id);
        return "redirect:/admin/lecture";
    }

    @GetMapping("/lecture/edit")
    public String showEditForm(Model model, @RequestParam("lectureId") int lectureId){
        Lecture lecture = lectureAdminService.editLecture(lectureId);
        List<Category> parents = categoryRepository.findByParentIsNull();
        model.addAttribute("lecture", lecture);
        model.addAttribute("parents", parents);
        return "admin/lectureEdit";
    }

    @PostMapping("/lecture/edit")
    public String editLecture(@RequestParam("categoryId") int categoryId, Lecture lecture){
        lectureAdminService.updateLecture(lecture, categoryId);
        return "redirect:/admin/lecture";
    }

    @GetMapping("/faq/insert")
    public String showFaqForm(){
        return "admin/faqForm";
    }

    @PostMapping("/faq/save")
    public String add(@RequestParam String question, @RequestParam String answer, Model model){
        Faq faq = new Faq();
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faqRepository.save(faq);

        faqService.addFaqToVectorStore(faq);

        model.addAttribute("message", "faq가 성공적으로 등록되었습니다.");
        return "redirect:/admin/faq";
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
