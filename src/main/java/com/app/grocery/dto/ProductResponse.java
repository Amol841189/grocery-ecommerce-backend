package com.app.grocery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String productId;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private String unit;

    private String imageUrl;

    // Category
    private String categoryId;
    private String categoryName;

    // SubCategory
    private String subCategoryId;
    private String subCategoryName;

    // Brand
    private String brandId;
    private String brandName;

    // Inventory
    private Integer quantity;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}