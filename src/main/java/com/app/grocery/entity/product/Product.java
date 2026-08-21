package com.app.grocery.entity.product;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.app.grocery.entity.brand.Brand;
import com.app.grocery.entity.inventory.Inventory;
import com.app.grocery.entity.subcategory.SubCategory;

@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_name_brand_subcategory",
                        columnNames = {
                                "name",
                                "brand_id",
                                "sub_category_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @Column(
            name = "product_id",
            nullable = false,
            unique = true,
            length = 40
    )
    private String productId;


    // =====================================================
    // PRODUCT INFORMATION
    // =====================================================

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 2000)
    private String description;


    // =====================================================
    // PRICE
    // =====================================================

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountPrice;


    // =====================================================
    // UNIT
    // =====================================================

    @Column(nullable = false, length = 50)
    private String unit;


    // =====================================================
    // SUBCATEGORY
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "sub_category_id",
        nullable = false
    )
    private SubCategory subCategory;


    // =====================================================
    // BRAND
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "brand_id",
        nullable = false
    )
    private Brand brand;


    // =====================================================
    // INVENTORY
    // =====================================================

    @OneToOne(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Inventory inventory;


    // =====================================================
    // STATUS
    // =====================================================

    @Column(nullable = false)
    private Boolean active;


    // =====================================================
    // IMAGE
    // =====================================================

    @Column(name = "image_url")
    private String imageUrl;


    // =====================================================
    // AUDIT
    // =====================================================

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}