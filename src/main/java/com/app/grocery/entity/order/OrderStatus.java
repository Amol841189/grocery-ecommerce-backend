package com.app.grocery.entity.order;

public enum OrderStatus {

    PAYMENT_PENDING,
    PAYMENT_FAILED,
    CONFIRMED,
    PROCESSING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}