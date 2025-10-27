package com.livo.project.config;

import com.livo.project.faq.dto.ChatMessage;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@SessionAttributes("chatHistory")
public class ChatSessionInitializer {

    @ModelAttribute("chatHistory")
    public List<ChatMessage> initChatHistory() {
        List<ChatMessage> history = new ArrayList<>();
        history.add(new ChatMessage("ai","""
안녕하세요! 😊
LiVO 챗봇입니다.

궁금하신 내용을 편하게 질문해주세요!
예시)
- 환불 규정 알려줘
- 신고 처리 기간은?
- 결제 오류났을 때 확인 방법 알려줘
""".stripIndent().trim()));
        return history;
    }
}

