package com.livo.project.faq.service;

import com.livo.project.faq.domain.Faq;
import com.livo.project.faq.repository.FaqRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaqService {
    private final FaqRepository faqRepository;
    private final EmbeddingService embeddingService;
    private final ChatService chatService;
    private VectorStore vectorStore;

    // TokenTextSplitter 재사용
    private final TokenTextSplitter textSplitter = TokenTextSplitter.builder()
            .withChunkSize(512)
            .withMinChunkSizeChars(350)
            .withMinChunkLengthToEmbed(5)
            .withMaxNumChunks(10000)
            .withKeepSeparator(true)
            .build();
    @PostConstruct
    public void initStore() {
        vectorStore = SimpleVectorStore.builder(embeddingService.getEmbeddingModel()).build();
        List<Faq> faqs = faqRepository.findAll();
        log.info("FAQ 벡터스토어 초기화 시작 - 총 {}건", faqs.size());

        int batchSize = 100;
        for (int i = 0; i < faqs.size(); i += batchSize) {
            List<Faq> batch = faqs.subList(i, Math.min(i + batchSize, faqs.size()));
            for (Faq faq : batch) {
                Document doc = new Document(faq.getAnswer(), Map.of("faqId", faq.getId()));
                List<Document> chunks = textSplitter.split(doc);
                vectorStore.add(chunks);
            }
        }
        log.info("FAQ 벡터스토어 초기화 완료");
    }

    public String refineAnswerWithLLM(String question, double threshold, String model) {
        log.info("Hybrid 방식 RAG 답변 생성 - question='{}', model={}", question, model);

        //유사 문서 검색
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(5)
                .build();

        List<Document> results = vectorStore.similaritySearch(request);
        log.info("검색 완료 - 결과 수={}", results.size());
        if (results.isEmpty()) return "results 관련 정보를 찾을 수 없습니다.";

        //threshold 필터링
        List<Document> filtered = results.stream()
                .filter(doc -> doc.getScore() != null && doc.getScore() > threshold)
                .toList();

        if (filtered.isEmpty()) return "filtered 관련 정보를 찾을 수 없습니다.";

        //가장 유사한 문서 하나 또는 상위 n개만 사용
        String topContexts = filtered.stream()
                .limit(3)
                .map(doc -> doc.getText())
                .collect(Collectors.joining("\n\n"));

        //AI에게 "보정용 프롬프트" 전달
        String systemPrompt = """
        당신은 고객 문의 답변을 보정하는 AI입니다.
        아래 제공된 FAQ 내용을 참고하여 사용자의 질문에 가장 자연스럽고 정확한 답변을 작성해주세요.
        주어진 내용이 불충분하면 "죄송합니다 😅 관련된 정보를 찾을 수 없어요."라고 답해주세요.
        
        참고 내용:
        """ + topContexts;

        ChatResponse response = chatService.openAiChat(question, systemPrompt, model);
        log.debug("response:{}", response);
        if (response == null || response.getResult() == null) {
            return "AI 모델 호출 중 오류가 발생했습니다.";
        }

        String aiAnswer = response.getResult().getOutput().getText();
        log.info("보정된 AI 답변: {}", aiAnswer);
        return aiAnswer;
    }


//    public String findSimilarAnswer(String question, double threshold) {
//        log.info("유사도 검색 요청 - query='{}'", question);
//        SearchRequest request = SearchRequest.builder()
//                .query(question)
//                .topK(10)
//                .build();
//
//        List<Document> results = vectorStore.similaritySearch(request);
//        log.info("검색 완료 - 결과 수={}", results.size());
//
//        // FAQ ID 기준으로 최대 1개 청크만 사용
//        Map<Object, Document> faqMap = results.stream()
//                .filter(doc -> doc.getScore() != null && doc.getScore() > threshold)
//                .collect(Collectors.toMap(
//                        doc -> doc.getMetadata().get("faqId"),
//                        doc -> doc,
//                        (d1, d2) -> d1
//                ));
//
//        if (faqMap.isEmpty()) return "관련된 답변을 찾을 수 없습니다.";
//
//        String answer = faqMap.values().stream()
//                .map(Document::getText)
//                .distinct()
//                .collect(Collectors.joining(" "));
//
//        log.info("최종 반환 문장:\n{}", answer.substring(0, Math.min(200, answer.length())) + "...");
//        return answer;
//    }


    public void addFaqToVectorStore(Faq faq) {
        Document doc = new Document(faq.getAnswer(), Map.of("faqId", faq.getId()));
        List<Document> chunks = textSplitter.split(doc);
        vectorStore.add(chunks);

        log.info("새 FAQ 벡터 추가 - faqId={}, 청크 수={}", faq.getId(), chunks.size());
    }
/*
    // FAQ 삭제용 메서드
    public void removeFaqFromVectorStore(Long faqId) {
        vectorStore.removeIf(doc -> faqId.equals(doc.getMetadata().get("faqId")));
        log.info("FAQ 벡터 삭제 - faqId={}", faqId);
    }

    // 전체 벡터 재초기화 (주기적 사용 가능)
    public void refreshVectorStore() {
        vectorStore = SimpleVectorStore.builder(embeddingService.getEmbeddingModel()).build();
        initStore();
        log.info("벡터스토어 전체 재초기화 완료");
    }
    */

}
