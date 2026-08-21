package com.app.grocery.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {

    private String productId;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private String unit;

    private String imageUrl;

    private String brandName;

    private String categoryName;

    private String subCategoryName;

    private Integer quantity;

    private Boolean active;

    private Boolean inStock;
}