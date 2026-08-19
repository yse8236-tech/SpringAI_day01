package com.skala.ch03.web;

import java.util.List;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lab2")
public class Lab2RetrieveController {
    private final VectorStore vectorStore;

    public Lab2RetrieveController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @GetMapping("/retrieve")
    public List<ChunkResponse> retrieve(@RequestParam String q,
            @RequestParam(defaultValue = "4") int topK) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(q)
                .topK(topK)
                .similarityThreshold(0.5) // 유사도 0.5 미만은 버림
                .build())
                .stream()
                .map(d -> new ChunkResponse(
                        (String) d.getMetadata().get("source"),
                        d.getScore(),
                        snippet(d.getText(), 120)))
                .toList();
    }

    private static String snippet(String text, int maxLength) {
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }
}

record ChunkResponse(String source, Double score, String content) {
}
