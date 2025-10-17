package com.livo.project.faq.controller;

import com.livo.project.faq.domain.Faq;
import com.livo.project.faq.dto.ChatMessage;
import com.livo.project.faq.repository.FaqRepository;
import com.livo.project.faq.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("faq")
@Controller
@RequiredArgsConstructor
@SessionAttributes("chatHistory")
public class FaqController {
    private final FaqService faqService;
    private final FaqRepository faqRepository;

    @GetMapping("/ask")
    public String showAskForm(){
        return "faq/faq_ask";
    }

    @PostMapping("/ask")
    public String ask(@ModelAttribute("chatHistory") List<ChatMessage> chatHistory, @RequestParam String question, Model model){
        double threshold = 0.3;
        String answer = faqService.refineAnswerWithLLM(question, threshold, "gpt-4o-mini");
        chatHistory.add(new ChatMessage("user", question));
        chatHistory.add(new ChatMessage("ai", answer));
        model.addAttribute("chatHistory", chatHistory);
        return "faq/faq_ask";
    }

    @GetMapping("/reset")
    public String reset(SessionStatus sessionStatus){
        sessionStatus.setComplete();// 세션 속성 초기화
        return "redirect:/faq/ask";
    }
    @GetMapping("/add")
    public String showAddForm(){
        return "faq/faq_add";
    }
    @PostMapping("/add")
    public String add(@RequestParam String question, @RequestParam String answer, Model model){
        Faq faq = new Faq();
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faqRepository.save(faq);

        faqService.addFaqToVectorStore(faq);

        model.addAttribute("message", "faq가 성공적으로 등록되었습니다.");
        return "faq/faq_add";
    }


    @ModelAttribute("chatHistory")
    public List<ChatMessage> chatHistory(){
        return new ArrayList<>();
    }
}
