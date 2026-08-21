package com.app.grocery.dto.subcategory.response;

public record SubCategoryResponse(
        String subCategoryId,
        String name,
        String description,
        String categoryId,
        String categoryName
) {
}