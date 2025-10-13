package com.livo.project.faq.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmbeddingService {
    private final OpenAiApi openAiApi;
    private OpenAiEmbeddingModel embeddingModel;

    @Value("${spring.ai.openai.embedding.options.model}")
    private String embeddingModelName;

    public OpenAiEmbeddingModel getEmbeddingModel() {
        if (embeddingModel == null) {
            OpenAiEmbeddingOptions options = OpenAiEmbeddingOptions.builder()
                    .model(embeddingModelName)
                    .build();

            embeddingModel = new OpenAiEmbeddingModel(
                    openAiApi,
                    MetadataMode.EMBED,
                    options,
                    RetryUtils.DEFAULT_RETRY_TEMPLATE
            );
        }
        return embeddingModel;
    }
}
