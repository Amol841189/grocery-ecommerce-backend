package com.app.grocery.dto.product.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreateRequest {

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private String unit;

    private String subCategoryId;

    private String brandId;

    private Integer quantity;

    // Product image
    private MultipartFile image;
}