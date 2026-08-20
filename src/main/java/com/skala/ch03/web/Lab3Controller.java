package com.skala.ch03.web;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skala.ch03.domain.Ticket;
import com.skala.ch03.repository.TicketRepository;



@RestController
@RequestMapping("/lab3")
public class Lab3Controller {

    private final ChatClient chatClient;
    private final TicketRepository tickets;

    public Lab3Controller(ChatClient assistantChatClient, TicketRepository tickets) {
        this.chatClient = assistantChatClient;
        this.tickets = tickets;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        System.out.println("request = " + request);
        System.out.println("userId = " + request.userId());
        System.out.println("message = " + request.message());

        String converationId = request.userId() + " : " + request.sessionId();

        return chatClient.prompt()
                         .user(request.message())
                         .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, converationId))
                         .toolContext(Map.of("userId", request.userId)) // userId를 Tool parameter로 넘기지 않음
                         .call()
                         .content();
    }

    public record ChatRequest(String userId, String sessionId, String message) {

    }

    @GetMapping("/admin/tickets/pending")
    public List<Ticket> pending() {
        return tickets.findPending();
    }

}
