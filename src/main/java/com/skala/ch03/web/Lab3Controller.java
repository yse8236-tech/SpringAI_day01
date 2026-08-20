package com.skala.ch03.web;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skala.ch03.domain.Ticket;
import com.skala.ch03.repository.TicketRepository;
import com.skala.ch03.tool.OrderTools;



@RestController
@RequestMapping("/lab3")
public class Lab3Controller {

    private final ChatClient chatClient;
    private final OrderTools orderTools;
    private final TicketRepository tickets;

    public Lab3Controller(ChatClient.Builder builder, OrderTools orderTools, TicketRepository tickets) {
        this.orderTools = orderTools;
        this.tickets = tickets;

        this.chatClient = builder.defaultSystem("""
                                    당신은 쇼핑몰 상담 도우미입니다.
                                    주문 상태나 배송 상태 질문에는 주문 조회 도구를 사용하세요.
                                    사용자가 제공하지 않은 정보는 추측하지 마세요.
                                    """)
                                 .build();
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
    System.out.println("request = " + request);
    System.out.println("userId = " + request.userId());
    System.out.println("message = " + request.message());

        return chatClient.prompt()
                         .user(request.message())
                         .tools(orderTools)
                         .toolContext(Map.of("userId", request.userId)) // userId를 Tool parameter로 넘기지 않음
                         .call()
                         .content();
    }

    public record ChatRequest(String userId, String message) {

    }

    @GetMapping("/admin/tickets/pending")
    public List<Ticket> pending() {
        return tickets.findPending();
    }

}
