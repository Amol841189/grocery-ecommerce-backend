package com.app.grocery.util;

import java.util.UUID;

public final class SubCategoryIdGenerator {

    private SubCategoryIdGenerator() {
        // Prevent object creation
    }

    public static String generate() {

        return "SUB-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }
}