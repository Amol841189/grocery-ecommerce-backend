package com.app.grocery.entity.cart;

import com.app.grocery.entity.product.Product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "cart_items",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_cart_product",
      columnNames = { "cart_id", "product_id" }
    ),
  }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // =====================================================
  // CART
  // =====================================================

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id", nullable = false)
  private Cart cart;

  // =====================================================
  // PRODUCT
  // =====================================================

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  // =====================================================
  // QUANTITY
  // =====================================================

  @Column(nullable = false)
  private Integer quantity;
}
