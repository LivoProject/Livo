package com.livo.project.admin.controller;

import com.livo.project.admin.service.FaqAdminService;
import com.livo.project.faq.domain.Faq;
import com.livo.project.faq.repository.FaqRepository;
import com.livo.project.faq.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/faq")
public class AdminFaqController {
    private final FaqAdminService faqAdminService;
    private final FaqRepository faqRepository;
    private final FaqService faqService;

    @GetMapping("")
    public String showFaqPage(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "9") int size,
                              Model model){
        Pageable pageable = PageRequest.of(page, size);
        Page<Faq> faqPage = faqAdminService.getFaqPage(pageable);

        model.addAttribute("faqPage",faqPage);
        model.addAttribute("faq",faqPage.getContent());
        return "admin/faqPage";
    }

    @GetMapping("/insert")
    public String showFaqForm(){
        return "admin/faqForm";
    }

    @PostMapping("/save")
    public String add(@RequestParam String question, @RequestParam String answer, Model model){
        Faq faq = new Faq();
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faqRepository.save(faq);

        faqService.addFaqToVectorStore(faq);

        model.addAttribute("message", "faq가 성공적으로 등록되었습니다.");
        return "redirect:/admin/faq";
    }

    @PostMapping("/delete/{faqId}")
    @ResponseBody
    public ResponseEntity<?> deleteFaq(@PathVariable("faqId") long faqId) {
        try {
            faqAdminService.deleteFaq(faqId);
            return ResponseEntity.ok("삭제 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 실패: " + e.getMessage());
        }
    }

    @GetMapping("/edit")
    public String showEditForm(Model model, @RequestParam("id") int id){
        Faq faq = faqAdminService.editFaq(id);
        model.addAttribute("faq",faq);
        return "admin/faqEdit";
    }

    @PostMapping("/edit")
    public String editFaq(@RequestParam("id") int id,
                                     @RequestParam String question,
                                     @RequestParam String answer){
        faqAdminService.updateFaq(id, question, answer);
        return "redirect:/admin/faq";
    }
}
