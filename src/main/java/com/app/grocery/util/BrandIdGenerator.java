package com.app.grocery.util;

import java.util.UUID;

public class BrandIdGenerator {

    private BrandIdGenerator() {
    }

    public static String generate() {

        return "BRD-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }
}