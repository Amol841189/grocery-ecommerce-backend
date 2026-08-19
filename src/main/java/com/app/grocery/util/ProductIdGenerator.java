package com.app.grocery.util;

import java.util.UUID;

public class ProductIdGenerator {

    private ProductIdGenerator() {
    }

    public static String generate() {

        return "PRD-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }
}