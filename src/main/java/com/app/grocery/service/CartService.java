package com.app.grocery.service;

import com.app.grocery.dto.AddToCartRequest;
import com.app.grocery.dto.CartItemResponse;
import com.app.grocery.dto.CartResponse;
import com.app.grocery.dto.UpdateCartItemRequest;
import com.app.grocery.entity.Cart;
import com.app.grocery.entity.CartItem;
import com.app.grocery.entity.Product;
import com.app.grocery.exception.InsufficientStockException;
import com.app.grocery.exception.ResourceNotFoundException;
import com.app.grocery.exception.ValidationException;
import com.app.grocery.repository.CartItemRepository;
import com.app.grocery.repository.CartRepository;
import com.app.grocery.repository.ProductRepository;
import com.app.grocery.util.CartIdGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

  private final CartRepository cartRepository;

  private final CartItemRepository cartItemRepository;

  private final ProductRepository productRepository;

  public CartService(
    CartRepository cartRepository,
    CartItemRepository cartItemRepository,
    ProductRepository productRepository
  ) {
    this.cartRepository = cartRepository;
    this.cartItemRepository = cartItemRepository;
    this.productRepository = productRepository;
  }

  // =====================================================
  // CREATE CART
  // =====================================================

  @Transactional
  public CartResponse createCart() {
    LocalDateTime now = LocalDateTime.now();

    Cart cart = Cart
      .builder()
      .cartId(CartIdGenerator.generate())
      .createdAt(now)
      .updatedAt(now)
      .build();

    Cart savedCart = cartRepository.save(cart);

    return buildCartResponse(savedCart);
  }

  // =====================================================
  // GET CART
  // =====================================================

  @Transactional(readOnly = true)
  public CartResponse getCart(String cartId) {
    Cart cart = cartRepository
      .findById(cartId)
      .orElseThrow(() -> new ResourceNotFoundException("Cart not found: " + cartId));

    return buildCartResponse(cart);
  }

  // =====================================================
  // ADD PRODUCT TO CART
  // =====================================================

  @Transactional
  public CartResponse addToCart(String cartId, AddToCartRequest request) {
    if (request.getProductId() == null || request.getProductId().isBlank()) {
      throw new ValidationException("Product ID is required");
    }

    if (request.getQuantity() == null || request.getQuantity() <= 0) {
      throw new ValidationException(
        "Quantity must be greater than zero for product ID: "
        + request.getProductId()
      );
    }

    // ---------------------------------------------
    // CART
    // ---------------------------------------------

    Cart cart = cartRepository
      .findById(cartId)
      .orElseThrow(() -> new ResourceNotFoundException("Cart not found: " + cartId));

    // ---------------------------------------------
    // PRODUCT
    // ---------------------------------------------

    Product product = productRepository
      .findById(request.getProductId())
      .orElseThrow(() ->
        new ResourceNotFoundException("Product not found: " + request.getProductId())
      );

    // ---------------------------------------------
    // CHECK ACTIVE
    // ---------------------------------------------

    if (!Boolean.TRUE.equals(product.getActive())) {
      throw new ResourceNotFoundException("Product is not available");
    }

    // ---------------------------------------------
    // CHECK INVENTORY
    // ---------------------------------------------

    if (product.getInventory() == null) {
      throw new ResourceNotFoundException("Product inventory not available");
    }

    int availableQuantity =
      product.getInventory().getQuantity() -
      product.getInventory().getReservedQuantity();

    // ---------------------------------------------
    // EXISTING CART ITEM
    // ---------------------------------------------

    CartItem cartItem = cartItemRepository
      .findByCartAndProduct(cart, product)
      .orElse(null);

    int newQuantity;

    if (cartItem == null) {
      newQuantity = request.getQuantity();
    } else {
      newQuantity = cartItem.getQuantity() + request.getQuantity();
    }

    // ---------------------------------------------
    // STOCK CHECK
    // ---------------------------------------------

    if (newQuantity > availableQuantity) {
      throw new InsufficientStockException(
        "Only " + availableQuantity + " items available"
      );
    }

    // ---------------------------------------------
    // ADD / UPDATE
    // ---------------------------------------------

    if (cartItem == null) {
      cartItem =
        CartItem
          .builder()
          .cart(cart)
          .product(product)
          .quantity(request.getQuantity())
          .build();
    } else {
      cartItem.setQuantity(newQuantity);
    }

    cartItemRepository.save(cartItem);

    cart.setUpdatedAt(LocalDateTime.now());

    cartRepository.save(cart);

    return buildCartResponse(cart);
  }

  // =====================================================
  // UPDATE QUANTITY
  // =====================================================

  @Transactional
  public CartResponse updateQuantity(
    String cartId,
    String productId,
    UpdateCartItemRequest request
  ) {
    Cart cart = cartRepository
      .findById(cartId)
      .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

    Product product = productRepository
      .findById(productId)
      .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

    CartItem item = cartItemRepository
      .findByCartAndProduct(cart, product)
      .orElseThrow(() -> 
          new ResourceNotFoundException(
               "Product with ID: " + productId 
                + "not found in cart with ID: " + cartId
          )
      );

    Integer quantity = request.getQuantity();

    // ---------------------------------------------
    // REMOVE
    // ---------------------------------------------

    if (quantity == null || quantity <= 0) {
      cartItemRepository.delete(item);
    } else {
      int availableQuantity =
        product.getInventory().getQuantity() -
        product.getInventory().getReservedQuantity();

      if (quantity > availableQuantity) {
        throw new InsufficientStockException(
          "Only " + availableQuantity + " items available"
        );
      }

      item.setQuantity(quantity);

      cartItemRepository.save(item);
    }

    cart.setUpdatedAt(LocalDateTime.now());

    cartRepository.save(cart);

    return buildCartResponse(cart);
  }

  // =====================================================
  // REMOVE ITEM
  // =====================================================
  @Transactional
  public CartResponse removeItem(String cartId, String productId) {
    Cart cart = cartRepository
      .findById(cartId)
      .orElseThrow(() -> new ResourceNotFoundException("Cart not found: " + cartId)
      );

    Product product = productRepository
      .findById(productId)
      .orElseThrow(() ->
        new ResourceNotFoundException("Product not found: " + productId)
      );

    CartItem item = cartItemRepository
      .findByCartAndProduct(cart, product)
      .orElseThrow(() ->
        new ResourceNotFoundException("Product not found in cart: " + productId)
      );

    cartItemRepository.delete(item);

    cartItemRepository.flush();

    cart.setUpdatedAt(LocalDateTime.now());

    return buildCartResponse(cart);
  }

  // =====================================================
  // CLEAR CART
  // =====================================================

  @Transactional
  public void clearCart(String cartId) {
    Cart cart = cartRepository
      .findById(cartId)
      .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

    cart.getItems().clear();

    cart.setUpdatedAt(LocalDateTime.now());

    cartRepository.save(cart);
  }

  // =====================================================
  // BUILD RESPONSE
  // =====================================================

  private CartResponse buildCartResponse(Cart cart) {
    List<CartItemResponse> items = cart
      .getItems()
      .stream()
      .map(this::mapCartItem)
      .toList();

    BigDecimal itemTotal = items
      .stream()
      .map(CartItemResponse::getItemTotal)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    int totalItems = items
      .stream()
      .mapToInt(CartItemResponse::getQuantity)
      .sum();

    // ---------------------------------------------
    // DELIVERY
    // ---------------------------------------------

    BigDecimal deliveryFee;

    if (items.isEmpty()) {
      deliveryFee = BigDecimal.ZERO;
    } else if (itemTotal.compareTo(BigDecimal.valueOf(500)) >= 0) {
      deliveryFee = BigDecimal.ZERO;
    } else {
      deliveryFee = BigDecimal.valueOf(20);
    }

    // ---------------------------------------------
    // HANDLING
    // ---------------------------------------------

    BigDecimal handlingFee = items.isEmpty()
      ? BigDecimal.ZERO
      : BigDecimal.valueOf(2);

    // ---------------------------------------------
    // GRAND TOTAL
    // ---------------------------------------------

    BigDecimal grandTotal = itemTotal.add(deliveryFee).add(handlingFee);

    return CartResponse
      .builder()
      .cartId(cart.getCartId())
      .items(items)
      .totalItems(totalItems)
      .itemTotal(itemTotal)
      .deliveryFee(deliveryFee)
      .handlingFee(handlingFee)
      .grandTotal(grandTotal)
      .build();
  }

  // =====================================================
  // MAP ITEM
  // =====================================================

  private CartItemResponse mapCartItem(CartItem item) {
    Product product = item.getProduct();

    BigDecimal sellingPrice = product.getDiscountPrice() != null
      ? product.getDiscountPrice()
      : product.getPrice();

    BigDecimal itemTotal = sellingPrice.multiply(
      BigDecimal.valueOf(item.getQuantity())
    );

    return CartItemResponse
      .builder()
      .cartItemId(item.getId())
      .productId(product.getProductId())
      .productName(product.getName())
      .imageUrl(product.getImageUrl())
      .price(product.getPrice())
      .discountPrice(product.getDiscountPrice())
      .unit(product.getUnit())
      .quantity(item.getQuantity())
      .itemTotal(itemTotal)
      .build();
  }
}
