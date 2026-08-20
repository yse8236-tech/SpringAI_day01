package com.skala.ch03.repository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.skala.ch03.domain.Ticket;

@Repository
public class TicketRepository {

    private final ConcurrentHashMap<String, Ticket> store = new ConcurrentHashMap<>();

    public Ticket create(String orderId, String userId, String reason) {

        String no = UUID.randomUUID().toString();

        Ticket ticket = new Ticket(no, orderId, userId, reason, Ticket.Status.PENDING);

        store.put(no, ticket);

        return ticket;

    }

    public List<Ticket> findPending() {
        return store.values().stream().filter(ticket -> ticket.status() == Ticket.Status.PENDING).toList();
    }

    public Ticket approve(String no) {
        Ticket ticket = store.get(no);

        if (ticket == null) {
            throw new IllegalArgumentException("티켓을 찾을 수 없습니다.");
        }

        Ticket approvedTicket = new Ticket(ticket.no(), ticket.orderId(), ticket.userId(), ticket.reason(), Ticket.Status.APPROVED);

        store.put(no, approvedTicket);

        return approvedTicket;
    }

}
