package com.skala.ch03.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.skala.ch03.service.AnswerDto;
import com.skala.ch03.service.Lab2IngestService;
import com.skala.ch03.service.Lab2IngestService.IngestResult;
import com.skala.ch03.service.Lab2QuestionAnswerService;
import com.skala.ch03.service.Lab2RetrievalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/lab2")
@Tag(name = "Day2 실습 · 사내 문서 Q&A")
public class Lab2Controller {

    private final Lab2IngestService ingestService;
    private final Lab2RetrievalService retrievalService;
    private final Lab2QuestionAnswerService questionAnswerService;

    public Lab2Controller(
            Lab2IngestService ingestService,
            Lab2RetrievalService retrievalService,
            Lab2QuestionAnswerService questionAnswerService) {
        this.ingestService = ingestService;
        this.retrievalService = retrievalService;
        this.questionAnswerService = questionAnswerService;
    }

    @PostMapping("/ingest")
    @Operation(summary = "정책 문서 다시 색인", description = "같은 source의 기존 청크를 지운 뒤 세 문서를 저장합니다.")
    public List<IngestResult> ingest() {
        return ingestService.ingestDefaultDocuments();
    }

    @GetMapping("/retrieve")
    @Operation(summary = "검색 결과와 유사도 확인", description = "답변 생성 전에 검색 품질을 점수와 함께 확인합니다.")
    public List<ChunkResponse> retrieve(
            @RequestParam String q,
            @RequestParam(required = false) Integer topK) {
        try {
            var chunks = topK == null
                    ? retrievalService.retrieve(q)
                    : retrievalService.retrieve(q, topK);
            return chunks.stream().map(ChunkResponse::from).toList();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping("/ask")
    @Operation(summary = "정책 문서에 근거해 답변", description = "답변, 사용한 출처, 근거 사용 여부를 구조화해 반환합니다.")
    public AnswerDto ask(@RequestBody AskRequest request) {
        if (request == null || request.question() == null || request.question().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "질문은 비어 있을 수 없습니다.");
        }
        return questionAnswerService.ask(request.question());
    }

    public record AskRequest(String question) {
    }
}
