package com.skala.ch03.repository;

import java.util.Optional;

import com.skala.ch03.domain.Order;

public interface OrderRepository {

    /** 소유자 검증까지 쿼리 안에서 끝낸다 — 권한 없는 주문은 애초에 안 보인다. */
    Optional<Order> findByIdAndOwnerId(String orderId, String ownerId);
}
