package com.skala.ch03.tool;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.skala.ch03.domain.Order;
import com.skala.ch03.domain.Ticket;
import com.skala.ch03.repository.OrderRepository;
import com.skala.ch03.repository.TicketRepository;

@Component
public class OrderTools {

    private final OrderRepository orders;
    private final TicketRepository tickets;

    public OrderTools(OrderRepository orders, TicketRepository tickets) {
        this.orders = orders;
        this.tickets = tickets;
    }

    @Tool(description = """
            주문 상태를 조회한다.
            사용자가 주문번호를 말하거나
            '내 주문', '배송 언제'처럼 주문 상태나 배송 상태를 물으면 이 도구를 사용한다.
            """)
    public OrderView getOrder(@ToolParam(description = "조회할 주문번호. 예: 12345") String orderId, ToolContext context) {
        System.out.println("[TOOL] getOrder() 호출");

        String userId = (String) context.getContext().get("userId");

        return orders.findByIdAndOwnerId(orderId, userId)
                     .map(OrderView::from)
                     .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }

    public record OrderView(String id, String item, String status, String eta) {
        static OrderView from(Order order) {
            return new OrderView(order.id(), order.item(), order.status().name(), order.eta());
        }
    }

    @Tool(description = """
            환불을 접수한다.
            즉시 환불 처리하지 않고 담당자 승인 후 처리한다.
            사용자가 특정 주문의 환불을 요청하면 이 도구를 사용한다.
           """)
    public TicketView reqeustRefund(@ToolParam(description = "환불할 주민번호. 예: 12345") String orderId,
                                    @ToolParam(description = "환불 사유") String reason,
                                    ToolContext context) {

        String userId = (String) context.getContext().get("userId");
        orders.findByIdAndOwnerId(orderId, userId).orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        Ticket ticket = tickets.create(orderId, userId, reason);

        System.out.println("[REFUND_REQEUESTED] user = " + userId + ", orderId = " + orderId + ", ticket = " + ticket.no());

        orders.findByIdAndOwnerId(orderId, userId).orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        return new TicketView(ticket.no(), "접수되었습니다. 담당자 승인 후 처리됩니다.");

    }

    public record TicketView(String no, String message) {

    }

}
