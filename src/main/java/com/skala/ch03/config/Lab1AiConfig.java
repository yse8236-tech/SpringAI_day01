package com.skala.ch03.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Day 1 실습 — 요약 전용 ChatClient 빈.
 *
 * <p>온도와 최대 토큰을 빈에서 못 박아 둔다. 호출부마다 정하게 두면 누군가는
 * 기본값 0.7 로 부르고, 그날부터 요약이 매번 달라진다.
 */
@Configuration
class Lab1AiConfig {

    /** 요약 전용 — 같은 입력이면 같은 출력이어야 하는 일. */
    @Bean
    ChatClient summaryChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        너는 이커머스 주문 상담 도우미다.
                        주어진 주문 정보만 사용해 한국어 한 문장으로 요약한다.
                        추측하지 않는다. 정보가 부족하면 "정보가 부족합니다"라고 답한다.
                        """)
                .defaultOptions(ChatOptions.builder()
                        .temperature(0.0)   // 요약은 매번 같아야 한다
                        .maxTokens(120)     // 비용 상한 — 길게 쓸 이유가 없다
                        .build())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(VectorStore.class)
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
