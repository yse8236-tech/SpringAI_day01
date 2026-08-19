package com.skala.ch03.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

class Lab2QuestionAnswerServiceTest {

    @Test
    void doesNotCallModelWhenNoEvidenceWasRetrieved() {
        Lab2RetrievalService retrievalService = mock(Lab2RetrievalService.class);
        ChatClient chatClient = mock(ChatClient.class);
        when(retrievalService.retrieve("우주 배송도 되나요?")).thenReturn(List.of());
        var service = new Lab2QuestionAnswerService(retrievalService, chatClient);

        AnswerDto answer = service.ask("우주 배송도 되나요?");

        assertThat(answer).isEqualTo(AnswerDto.unknown());
        verifyNoInteractions(chatClient);
    }

    @Test
    void rejectsSourceThatWasNotRetrieved() {
        var chunks = List.of(new RetrievedChunk("membership", 0.8, "골드 등급 적립률은 3%입니다."));
        var generated = new AnswerDto("골드 등급은 3% 적립됩니다.", List.of("invented-policy"), true);

        AnswerDto answer = Lab2QuestionAnswerService.validateGrounding(generated, chunks);

        assertThat(answer).isEqualTo(AnswerDto.unknown());
    }

    @Test
    void canonicalizesVerifiedMarkdownSource() {
        var chunks = List.of(new RetrievedChunk("membership", 0.8, "골드 등급 적립률은 3%입니다."));
        var generated = new AnswerDto("골드 등급은 3% 적립됩니다.", List.of("membership.md"), true);

        AnswerDto answer = Lab2QuestionAnswerService.validateGrounding(generated, chunks);

        assertThat(answer.sources()).containsExactly("membership");
        assertThat(answer.grounded()).isTrue();
    }

    @Test
    void normalizesUngroundedModelResponseToRequiredPhrase() {
        var chunks = List.of(new RetrievedChunk("shipping-policy", 0.6, "평균 배송 기간은 2~3일입니다."));
        var generated = new AnswerDto("문서에 관련 정보가 없습니다.", List.of("shipping-policy"), false);

        AnswerDto answer = Lab2QuestionAnswerService.validateGrounding(generated, chunks);

        assertThat(answer).isEqualTo(AnswerDto.unknown());
    }
}
