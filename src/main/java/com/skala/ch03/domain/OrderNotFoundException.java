package com.skala.ch03.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 없는 주문이거나 남의 주문이거나, 웹 계층 입장에서는 둘 다 404 로 보인다. */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String orderId) {
        super("주문을 찾을 수 없습니다: " + orderId);
    }
}
