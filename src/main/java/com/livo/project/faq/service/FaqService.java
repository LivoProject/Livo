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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
                Document doc = new Document(buildRagFormat(faq.getQuestion(), faq.getAnswer()), Map.of("faqId", faq.getId()));
                List<Document> chunks = textSplitter.split(doc);
                vectorStore.add(chunks);
            }
        }
        log.info("FAQ 벡터스토어 초기화 완료");
    }
//    @Transactional
//    public void rebuildVectorStore(){
//        vectorStore = SimpleVectorStore.builder(embeddingService.getEmbeddingModel()).build();
//        List<Faq> faqs = faqRepository.findAll();
//        int batchSize = 100;
//        for (int i = 0; i < faqs.size(); i += batchSize) {
//            List<Faq> batch = faqs.subList(i, Math.min(i + batchSize, faqs.size()));
//            for (Faq faq : batch) {
//                String ragText = buildRagFormat(faq.getQuestion(), faq.getAnswer());
//                Document doc = new Document(ragText, Map.of("faqId", faq.getId()));
//                List<Document> chunks = textSplitter.split(doc);
//                vectorStore.add(chunks);
//            }
//        }
//        log.info(" 벡터스토어 전체 재구축 완료 (총 {}개 FAQ)", faqs.size());
//    }
    public String refineAnswerWithLLM(String question, double threshold, String model) {
        log.info("Hybrid 방식 RAG 답변 생성 - question='{}', model={}", question, model);

        //유사 문서 검색
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(5)
                .build();

        List<Document> results = vectorStore.similaritySearch(request);
        log.info("검색 완료 - 결과 수={}", results.size());
        if (results.isEmpty()) return "죄송합니다 😅 관련된 정보를 찾을 수 없어요.";

        //threshold 필터링
        List<Document> filtered = results.stream()
                .filter(doc -> {
                    Double score = doc.getScore();
                    return score == null || score > threshold; // null이면 통과
                })
                .toList();

        if (filtered.isEmpty()) return "죄송합니다 😅 관련된 정보를 찾을 수 없어요";

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


    public void addFaqToVectorStore(Faq faq) {
        String ragText = buildRagFormat(faq.getQuestion(), faq.getAnswer());
        Document doc = new Document(ragText, Map.of("faqId", faq.getId()));
        List<Document> chunks = textSplitter.split(doc);
        vectorStore.add(chunks);

        log.info("새 FAQ 벡터 추가 - faqId={}, 청크 수={}", faq.getId(), chunks.size());
    }

    public String buildRagFormat(String question, String answer) {
        String variations = generateQuestionVariations(question);
        String keywords = extractKeywords(answer);
        return """
            [질문들]
            - %s
            %s
        
            [답변]
            %s
        
            [핵심키워드]
            %s
            """.formatted(
                question,
                prependHyphens(variations), // 보기 좋은 정렬 처리
                answer,
                keywords
        );
    }
    private String prependHyphens(String text) {
        return Arrays.stream(text.split("\n"))
                .map(line -> "- " + line.trim())
                .collect(Collectors.joining("\n"));
    }

    private String extractKeywords(String text) {
        String prompt = """
            다음 문장에서 핵심 키워드만 5~10개 추출해서 콤마로 구분해줘.
            불필요한 조사/형용사/문장부호 제거.
            명사/주제어/서비스 용어 중심으로.
            문장:
            """ + text;
        String systemMessage = "너는 텍스트에서 핵심 키워드를 뽑아주는 도우미야. 설명 없이 키워드만 콤마로 구분해서 출력해.";
        ChatResponse res = chatService.openAiChat(prompt, systemMessage, "gpt-4o-mini");
        if (res == null || res.getResult() == null) return "";
        return res.getResult().getOutput().getText().trim();
    }

    private String generateQuestionVariations(String question) {
        String systemMessage = """
        너는 FAQ 질문을 다양한 표현으로 재작성해주는 도움 AI야.
        같은 의미를 가지지만 말투와 표현이 다른 질문을 5개 만들어줘.
        설명 없이 질문만 줄바꿈으로 출력해.
        어투는 자연스럽고 사용자 질문 스타일로.
    """;

        ChatResponse res = chatService.openAiChat(question, systemMessage, "gpt-4o-mini");

        if (res == null || res.getResult() == null) return question;

        return res.getResult().getOutput().getText().trim();
    }


}
