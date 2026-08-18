package com.skala.ch03.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.skala.ch03.domain.Order;
import com.skala.ch03.domain.OrderNotFoundException;
import com.skala.ch03.repository.OrderRepository;

/**
 * Day 1 실습 — 주문을 못 찾으면 모델을 부르지 않는다.
 *
 * <p>서비스는 무엇을 하는가만 읽혀야 한다. 소유자 검증은 리포지토리 쿼리 안에서
 * 끝내고, 주문이 없으면 모델 호출 전에 {@link OrderNotFoundException} 으로 끊는다.
 * AI 가 죽어도 화면은 산다 — 요약에 실패하면 원본 주문 정보로 대체한다.
 */
@Service
public class OrderSummaryService {

    private static final Logger log = LoggerFactory.getLogger(OrderSummaryService.class);

    private final OrderRepository orders;
    private final ChatClient summaryChat;

    public OrderSummaryService(OrderRepository orders, ChatClient summaryChatClient) {
        this.orders = orders;
        this.summaryChat = summaryChatClient;
    }

    public SummaryResponse summarize(String orderId, String userId) {
        Order order = orders.findByIdAndOwnerId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        String summary;
        try {
            summary = summaryChat.prompt()
                    .user(u -> u.text("주문번호 {id} · 상품 {item} · 상태 {status} · 도착예정 {eta}"
                                    + "\n위 정보를 한 문장으로 요약해 줘.")
                            .param("id", order.id())
                            .param("item", order.item())
                            .param("status", order.status().label())
                            .param("eta", order.eta()))
                    .call().content();
        } catch (RuntimeException e) {
            log.warn("AI 요약 실패, 주문 정보로 대체한다: orderId={}", orderId, e);
            summary = order.item() + " · " + order.status().label();
        }

        return new SummaryResponse(order.id(), summary);
    }
}
