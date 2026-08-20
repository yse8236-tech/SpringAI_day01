package com.skala.ch03.tool;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.skala.ch03.domain.Order;
import com.skala.ch03.domain.Ticket;
import com.skala.ch03.repository.OrderRepository;
import com.skala.ch03.repository.TicketRepository;

import io.micrometer.core.instrument.MeterRegistry;

@Component
public class OrderTools {

    private final OrderRepository orders;
    private final TicketRepository tickets;
    private final MeterRegistry registry;

    public OrderTools(OrderRepository orders, TicketRepository tickets, MeterRegistry registry) {
        this.orders = orders;
        this.tickets = tickets;
        this.registry = registry;
    }

    @Tool(description = """
            주문 상태를 조회한다.
            사용자가 주문번호를 말하거나
            '내 주문', '배송 언제'처럼 주문 상태나 배송 상태를 물으면 이 도구를 사용한다.
            """)
    public OrderView getOrder(@ToolParam(description = "조회할 주문번호. 예: 12345") String orderId, ToolContext context) {

        System.out.println("[TOOL] getOrder() 호출");

        String userId = (String) context.getContext().get("userId");

        try {

            OrderView result = orders.findByIdAndOwnerId(orderId, userId)
                                     .map(OrderView::from)
                                     .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

            registry.counter("ai.tool.calls", "tool", "getOrder", "result", "ok").increment();

            return result;

        } catch (RuntimeException e) {

            registry.counter("ai.tool.calls", "tool", "getOrder", "result", "fail").increment();

            throw e;

        }

    }

    public record OrderView(String id, String item, String status, String eta) {

        static OrderView from(Order order) {

            return new OrderView(order.id(), order.item(), order.status().name(), order.eta());

        }

    }

    @Tool(description = """
            환불을 접수한다.
            사용자가 '환불해줘', '환불로 접수해줘', '반품 접수해줘'처럼
            이미 대화 중인 주문의 환불 또는 반품 접수를 요청하면 이 도구를 사용한다.
            이전 대화에서 주문번호와 환불 사유를 알 수 있으면 그 정보를 사용한다.
            즉시 환불 처리하지 않고 PENDING 상태로 접수하며 담당자 승인 후 처리한다.
           """)
    public TicketView reqeustRefund(@ToolParam(description = "환불할 주민번호. 이전 대화의 주문번호를 사용할 수 있음.  예: 12345") String orderId,
                                    @ToolParam(description = "환불 사유. 예: 단순 변심") String reason,
                                    ToolContext context) {

        System.out.println("[TOOL] requestRefund() 호출");
        String userId = (String) context.getContext().get("userId");

        try {

            // 본인 주문인지 권한 확인
            orders.findByIdAndOwnerId(orderId, userId).orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

            // PENDING 티켓 생성
            Ticket ticket = tickets.create(orderId, userId, reason);

            // 도구 호출 성공 지표
            registry.counter("ai.tool.calls", "tool", "requestRefund", "result", "ok").increment();

            // 임시 감사 로그
            System.out.println("[REFUND_REQUESTED]"
                                + "user = " + userId + ", orderId = " + orderId + ", reason = " + reason
                                + ", ticket" + ticket + ", status = " + ticket.status());

            // 접수 결과 반환
            return new TicketView(ticket.no(), "접수되었습니다. 담당자 승인 후 처리됩니다.");

        } catch (RuntimeException e) {

            // 도구 호출 실패 지표
            registry.counter("ai.tool.calls", "tool", "requestRefund", "result", "fail").increment();

            throw e;

        }

    }

    public record TicketView(String no, String message) {

    }

}
