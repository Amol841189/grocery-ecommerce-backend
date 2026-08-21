package com.app.grocery.controller;

import com.app.grocery.dto.cart.request.AddToCartRequest;
import com.app.grocery.dto.cart.request.CreateCartRequest;
import com.app.grocery.dto.cart.request.UpdateCartItemRequest;
import com.app.grocery.dto.cart.response.CartResponse;
import com.app.grocery.service.CartService;

import org.springframework.http.HttpStatus;
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
  public ResponseEntity<CartResponse> createCart(@RequestBody CreateCartRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(cartService.createCart(request.userId()));
  }

  // =====================================================
  // GET CART
  // =====================================================

  @GetMapping("/cid/{cartId}")
  public ResponseEntity<CartResponse> getCartByCartId(@PathVariable String cartId) {
    return ResponseEntity.ok(cartService.getCartByCartId(cartId));
  }

  @GetMapping("/uid/{userId}")
  public ResponseEntity<CartResponse> getCartByUserId(@PathVariable String userId) {
    return ResponseEntity.ok(cartService.getCartByCartId(userId));
  }

  // =====================================================
  // ADD PRODUCT
  // =====================================================

  @PostMapping("/items")
  public ResponseEntity<CartResponse> addToCart(
      @RequestParam String userId,
      @RequestBody AddToCartRequest request
  ) {

      return ResponseEntity.ok(
          cartService.addToCart(userId, request)
      );
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
