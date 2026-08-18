package com.skala.ch03.domain;

public record Order(String id, String ownerId, String item, OrderStatus status, String eta) {
}
