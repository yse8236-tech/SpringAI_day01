package com.skala.ch03.web;

import com.skala.ch03.service.RetrievedChunk;

/** 검색 진단 API용 응답. 생성에 쓰는 전체 청크와 달리 화면에는 앞 120자만 보여 준다. */
public record ChunkResponse(String source, Double score, String content) {

    private static final int MAX_SNIPPET_LENGTH = 120;

    static ChunkResponse from(RetrievedChunk chunk) {
        return new ChunkResponse(chunk.source(), chunk.score(), snippet(chunk.content()));
    }

    private static String snippet(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= MAX_SNIPPET_LENGTH
                ? text
                : text.substring(0, MAX_SNIPPET_LENGTH) + "...";
    }
}
