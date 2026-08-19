package com.skala.ch03.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.skala.ch03.service.RetrievedChunk;

class ChunkResponseTest {

    @Test
    void limitsDiagnosticContentToOneHundredTwentyCharacters() {
        var chunk = new RetrievedChunk("source", 0.7, "가".repeat(121));

        ChunkResponse response = ChunkResponse.from(chunk);

        assertThat(response.content()).hasSize(123).endsWith("...");
    }
}
