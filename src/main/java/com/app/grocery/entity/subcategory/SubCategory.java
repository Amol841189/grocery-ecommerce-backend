package com.app.grocery.entity.subcategory;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.app.grocery.entity.brand.Brand;
import com.app.grocery.entity.category.Category;
import com.app.grocery.entity.product.Product;


@Entity
@Table(
        name = "sub_categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "category_id",
                                "name"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubCategory {

    @Id
    @Column(name = "sub_category_id")
    private String subCategoryId;


    // =====================================================
    // NAME
    // =====================================================

    @Column(
            nullable = false,
            length = 100
    )
    private String name;


    // =====================================================
    // DESCRIPTION
    // =====================================================

    @Column(length = 500)
    private String description;


    // =====================================================
    // SUBCATEGORY → CATEGORY
    // =====================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;


    // =====================================================
    // SUBCATEGORY → PRODUCTS
    // =====================================================

    @OneToMany(
            mappedBy = "subCategory"
    )
    private List<Product> products =
            new ArrayList<>();


    // =====================================================
    // SUBCATEGORY → BRANDS
    // =====================================================

    @ManyToMany(mappedBy = "subCategories")
    private List<Brand> brands =
            new ArrayList<>();
}