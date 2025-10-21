package com.livo.project.faqPage.controller;

import com.livo.project.faq.domain.Faq;
import com.livo.project.faqPage.service.FaqPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("faq-page")
public class FaqPageController {

    private final FaqPageService faqPageService;

    @GetMapping("/list")
    public String listPage(Model model){
        List<Faq> faqs = faqPageService.findAllFaqPages();
        model.addAttribute("faqs", faqs);
        return "faq-page/list";
    }

}