package com.app.grocery.util;

import java.util.UUID;

public final class CartIdGenerator {

    private CartIdGenerator() {
    }

    public static String generate() {

        return "CART-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }
}