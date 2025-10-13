package com.livo.project.faq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final OpenAiApi openAiApi;

    public ChatResponse openAiChat(String userInput, String systemMessage, String model){
        log.debug("OpenAI 챗 호출 시작 - 모델:{}",model);

        try {
            List<Message> messages =  Arrays.asList(
                    new SystemMessage(systemMessage),
                    new UserMessage(userInput)
            );

            OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                    .model(model)
                    .build();

            Prompt prompt = new Prompt(messages, chatOptions);

            OpenAiChatModel chatModel = OpenAiChatModel.builder()
                    .openAiApi(openAiApi)
                    .build();

            return chatModel.call(prompt);
        } catch (Exception e) {
            log.error("OpenAI 챗 호출 중 오류 발생 {} : ", e.getMessage(), e);
            return null;
        }

    }
}
