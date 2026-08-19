package com.app.grocery.dto;

public record SubCategoryResponse(
        String subCategoryId,
        String name,
        String description,
        String categoryId,
        String categoryName
) {
}