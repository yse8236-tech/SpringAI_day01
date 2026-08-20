package com.skala.ch03.domain;

public record Ticket(String no, String orderId, String userId, String reason, Status status) {
    public enum Status {
        PENDING,
        APPROVED,
    }
}
