package com.app.grocery.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

  @Id
  @Column(name = "brand_id", nullable = false, unique = true, length = 40)
  private String brandId;

  // =====================================================
  // BRAND NAME
  // =====================================================

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  // =====================================================
  // DESCRIPTION
  // =====================================================

  @Column(length = 500)
  private String description;

  // =====================================================
  // BRAND → PRODUCTS
  // =====================================================

  @OneToMany(mappedBy = "brand")
  @Builder.Default
  private List<Product> products = new ArrayList<>();

  // =====================================================
  // BRAND → SUBCATEGORIES
  // =====================================================

  @ManyToMany
  @JoinTable(
    name = "brand_subcategories",
    joinColumns = @JoinColumn(name = "brand_id"),
    inverseJoinColumns = @JoinColumn(name = "sub_category_id")
  )
  @Builder.Default
  private List<SubCategory> subCategories = new ArrayList<>();

  // =====================================================
  // AUDIT
  // =====================================================

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;
}
