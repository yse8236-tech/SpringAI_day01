package com.skala.ch03.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.stereotype.Component;

@Component
public class AuditAdvisor implements CallAdvisor {

    private static final Logger log = LoggerFactory.getLogger(AuditAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        log.info("[AUDIT] AI 요청 시작");

        try {
            ChatClientResponse response = chain.nextCall(request);
            log.info("[AUDIT] AI 요청 성공");
            return response;
        } catch (RuntimeException e) {
            log.warn("[AUDIT] AI 요청 실패: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String getName() {
        return "auditAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
