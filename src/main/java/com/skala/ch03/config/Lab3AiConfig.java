package com.skala.ch03.config;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.skala.ch03.advisor.AuditAdvisor;
import com.skala.ch03.advisor.TokenMeterAdvisor;
import com.skala.ch03.tool.OrderTools;

@Configuration
public class Lab3AiConfig {

    @Bean
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().maxMessages(20).build();
    }

    @Bean
    ChatClient assistantChatClient(ChatClient.Builder builder,
                                   VectorStore vs,
                                   ChatMemory memory,
                                   OrderTools tools,
                                   AuditAdvisor audit,
                                   TokenMeterAdvisor tokenMeter) {
        return builder.defaultAdvisors(audit,
                                       SafeGuardAdvisor.builder().sensitiveWords(List.of("주민등록번호", "카드번호")).build(),
                                       MessageChatMemoryAdvisor.builder(memory).build(),
                                       QuestionAnswerAdvisor.builder(vs).build(),
                                       tokenMeter)
                      .defaultTools(tools)
                      .build();
    }

}
