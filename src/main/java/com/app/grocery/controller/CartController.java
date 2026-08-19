package com.app.grocery.controller;

import com.app.grocery.dto.AddToCartRequest;
import com.app.grocery.dto.CartResponse;
import com.app.grocery.dto.UpdateCartItemRequest;
import com.app.grocery.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

  private final CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  // =====================================================
  // CREATE NEW CART
  // =====================================================

  @PostMapping
  public ResponseEntity<CartResponse> createCart() {
    return ResponseEntity.ok(cartService.createCart());
  }

  // =====================================================
  // GET CART
  // =====================================================

  @GetMapping("/{cartId}")
  public ResponseEntity<CartResponse> getCart(@PathVariable String cartId) {
    return ResponseEntity.ok(cartService.getCart(cartId));
  }

  // =====================================================
  // ADD PRODUCT
  // =====================================================

  @PostMapping("/{cartId}/items")
  public ResponseEntity<CartResponse> addToCart(
    @PathVariable String cartId,
    @RequestBody AddToCartRequest request
  ) {
    return ResponseEntity.ok(cartService.addToCart(cartId, request));
  }

  // =====================================================
  // UPDATE QUANTITY
  // =====================================================

  @PutMapping("/{cartId}/items/{productId}")
  public ResponseEntity<CartResponse> updateQuantity(
    @PathVariable String cartId,
    @PathVariable String productId,
    @RequestBody UpdateCartItemRequest request
  ) {

    System.out.println("PUT http://localhost:8081/" + cartId + "/items/" + productId);
    System.out.println("========== UPDATE QUANTITY API ==========");
    System.out.println("cartId     : " + cartId);
    System.out.println("productId  : " + productId);
    System.out.println("quantity   : " + request.getQuantity());

    CartResponse response = cartService.updateQuantity(cartId, productId, request);

    System.out.println("========== API COMPLETED ==========");

    return ResponseEntity.ok(response);
  }

  // =====================================================
  // REMOVE PRODUCT
  // =====================================================

  @DeleteMapping("/{cartId}/items/{productId}")
  public ResponseEntity<CartResponse> removeItem(
    @PathVariable String cartId,
    @PathVariable String productId
  ) {
    return ResponseEntity.ok(cartService.removeItem(cartId, productId));
  }

  // =====================================================
  // CLEAR CART
  // =====================================================

  @DeleteMapping("/{cartId}")
  public ResponseEntity<Void> clearCart(@PathVariable String cartId) {
    cartService.clearCart(cartId);

    return ResponseEntity.noContent().build();
  }
}
