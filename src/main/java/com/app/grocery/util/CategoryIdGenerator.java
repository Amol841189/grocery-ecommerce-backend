package com.app.grocery.util;

import java.util.UUID;

public class CategoryIdGenerator {

    private CategoryIdGenerator() {
    }

    public static String generate() {

        return "CAT-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }
}