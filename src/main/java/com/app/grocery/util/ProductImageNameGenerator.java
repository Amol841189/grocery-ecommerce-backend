package com.app.grocery.util;

import java.util.UUID;

public class ProductImageNameGenerator {

    private ProductImageNameGenerator() {
    }

    public static String generate(
            String productName,
            String originalFileName) {

        String cleanProductName = productName
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        String extension = "";

        if (originalFileName != null &&
                originalFileName.contains(".")) {

            extension = originalFileName
                    .substring(
                            originalFileName.lastIndexOf(".")
                    )
                    .toLowerCase();
        }

        String randomId = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);

        return cleanProductName
                + "-"
                + randomId
                + extension;
    }
}