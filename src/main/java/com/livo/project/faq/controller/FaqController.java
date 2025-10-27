package com.livo.project.faq.controller;

import com.livo.project.faq.domain.Faq;
import com.livo.project.faq.dto.ChatMessage;
import com.livo.project.faq.repository.FaqRepository;
import com.livo.project.faq.service.FaqService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("faq")
@Controller
@RequiredArgsConstructor
@SessionAttributes("chatHistory")
public class FaqController {
    private final FaqService faqService;
    private final FaqRepository faqRepository;

//    @ModelAttribute("chatHistory")
//    public List<ChatMessage> chatHistory(){
//        List<ChatMessage> history = new ArrayList<>();
//        history.add(new ChatMessage("ai", """
//        안녕하세요! 😊
//        LiVO 챗봇입니다.
//
//        궁금하신 내용을 편하게 질문해주세요!
//        예시)
//        - 환불 규정 알려줘
//        - 신고하면 언제 처리돼?
//        - 결제 중 오류났을 때 어떻게 해?
//        """));
//        return history;
//    }

    @GetMapping("/ask")
    public String showAskForm(){
        return "faq/faq_ask";
    }

    @PostMapping("/ask")
    public String ask(@ModelAttribute("chatHistory") List<ChatMessage> chatHistory,
                      @RequestParam String question, Model model){
        double threshold = 0.15;
        String answer = faqService.refineAnswerWithLLM(question, threshold, "gpt-4o-mini");
        chatHistory.add(new ChatMessage("user", question));
        chatHistory.add(new ChatMessage("ai", answer));
        model.addAttribute("chatHistory", chatHistory);
        return "faq/faq_ask";
    }
    @PostMapping("/ask-ajax")
    @ResponseBody
    public Map<String, String> askAjax(HttpSession session,
                                       @RequestParam String question){
        List<ChatMessage> chatHistory =
                (List<ChatMessage>) session.getAttribute("chatHistory");
        if (chatHistory == null) {
            chatHistory = new java.util.ArrayList<>();
        }
        String answer = faqService.refineAnswerWithLLM(question, 0.15, "gpt-4o-mini");
        chatHistory.add(new ChatMessage("user", question));
        chatHistory.add(new ChatMessage("ai", answer));
        session.setAttribute("chatHistory", chatHistory);
        return Map.of(
                "user", question,
                "ai", answer
        );
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



}
