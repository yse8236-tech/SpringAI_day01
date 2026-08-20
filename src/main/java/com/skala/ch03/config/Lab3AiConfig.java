package com.skala.ch03.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.skala.ch03.tool.OrderTools;

@Configuration
public class Lab3AiConfig {

    @Bean
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().maxMessages(20).build();
    }

    @Bean
    ChatClient assistantChatClient(ChatClient.Builder builder, VectorStore vs, ChatMemory memory, OrderTools tools) {
        return builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build(),
                                       QuestionAnswerAdvisor.builder(vs).build())
                      .defaultTools(tools)
                      .build();
    }

}
