package com.skala.ch03.advisor;

import java.util.concurrent.TimeUnit;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;

@Component
public class TokenMeterAdvisor implements CallAdvisor {

    private final MeterRegistry registry;

    public TokenMeterAdvisor(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {

        long started = System.nanoTime();

        ChatClientResponse response = chain.nextCall(request);

        long elapsed = System.nanoTime() - started;

        Usage usage = response.chatResponse().getMetadata().getUsage();

        registry.counter("ai.tokens", "type", "prompt", "feature", "chat").increment(usage.getPromptTokens());

        registry.counter("ai.tokens", "type", "completion", "feature", "chat").increment(usage.getCompletionTokens());

        registry.timer("ai.latency", "phase", "model").record(elapsed, TimeUnit.NANOSECONDS);

        return response;

    }

    @Override
    public String getName() {
        return "tokenMeterAdvisor";
    }

    @Override
    public int getOrder() {
        return 900;
    }

}
