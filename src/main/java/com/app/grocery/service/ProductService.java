package com.app.grocery.service;

import com.app.grocery.dto.product.request.ProductCreateRequest;
import com.app.grocery.dto.product.response.ProductDeleteResponse;
import com.app.grocery.dto.product.response.ProductListResponse;
import com.app.grocery.dto.product.response.ProductResponse;
import com.app.grocery.entity.*;
import com.app.grocery.exception.ProductAlreadyExistsException;
import com.app.grocery.repository.*;
import com.app.grocery.util.ProductIdGenerator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {

        private final ProductRepository productRepository;
        private final BrandRepository brandRepository;
        private final ProductImageService productImageService;
        private final SubCategoryRepository subCategoryRepository;

        public ProductService(
                        ProductRepository productRepository,
                        BrandRepository brandRepository,
                        ProductImageService productImageService,
                        SubCategoryRepository subCategoryRepository) {
                this.productRepository = productRepository;
                this.brandRepository = brandRepository;
                this.productImageService = productImageService;
                this.subCategoryRepository = subCategoryRepository;
        }

        // =====================================================
        // ADD PRODUCT
        // =====================================================
        @Transactional
        public ProductResponse addProduct(ProductCreateRequest request)
                        throws IOException {
                // =====================================================
                // 1. FIND SUBCATEGORY
                // =====================================================

                SubCategory subCategory = subCategoryRepository
                                .findById(request.getSubCategoryId())
                                .orElseThrow(() -> new RuntimeException(
                                                "SubCategory not found: " + request.getSubCategoryId()));

                // =====================================================
                // 2. GET CATEGORY
                //
                // Category is obtained from SubCategory
                // =====================================================

                Category category = subCategory.getCategory();

                // =====================================================
                // 3. FIND BRAND
                //
                // Brand is optional
                // =====================================================

                Brand brand = null;

                if (request.getBrandId() != null && !request.getBrandId().isBlank()) {
                        // -------------------------------------------------
                        // Find brand
                        // -------------------------------------------------

                        brand = brandRepository
                                        .findById(request.getBrandId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Brand not found: " + request.getBrandId()));

                        // -------------------------------------------------
                        // IMPORTANT
                        //
                        // Check that brand belongs to selected
                        // subcategory.
                        // -------------------------------------------------

                        boolean brandAssigned = brandRepository.existsByBrandIdAndSubCategories_SubCategoryId(
                                        request.getBrandId(),
                                        request.getSubCategoryId());

                        if (!brandAssigned) {
                                throw new RuntimeException(
                                                "Brand '" +
                                                                brand.getName() +
                                                                "' is not assigned to subcategory '" +
                                                                subCategory.getName() +
                                                                "'");
                        }
                }

                // =====================================================
                // 4. CHECK DUPLICATE PRODUCT
                // =====================================================

                if (productRepository.existsByNameIgnoreCaseAndBrand_BrandIdAndSubCategory_SubCategoryId(
                                request.getName(),
                                request.getBrandId(),
                                request.getSubCategoryId())) {
                        throw new ProductAlreadyExistsException(
                                        "Product already exists with same name, brand and subcategory");
                }

                // =====================================================
                // 5. CURRENT TIME
                // =====================================================

                LocalDateTime now = LocalDateTime.now();

                // =====================================================
                // 6. CREATE PRODUCT
                // =====================================================

                Product product = Product
                                .builder()
                                .productId(ProductIdGenerator.generate())
                                .name(request.getName())
                                .description(request.getDescription())
                                .price(request.getPrice())
                                .discountPrice(request.getDiscountPrice())
                                .unit(request.getUnit())
                                .subCategory(subCategory)
                                .brand(brand)
                                .active(true)
                                .createdAt(now)
                                .updatedAt(now)
                                .build();

                // =====================================================
                // 7. SAVE PRODUCT IMAGE
                // =====================================================

                MultipartFile image = request.getImage();

                if (image != null && !image.isEmpty()) {
                        String imageUrl = productImageService.saveProductImage(
                                        image,
                                        category.getName(),
                                        subCategory.getName(),
                                        brand != null ? brand.getName() : "no-brand",
                                        product.getName());

                        product.setImageUrl(imageUrl);
                }

                // =====================================================
                // 8. CREATE INVENTORY
                // =====================================================

                Inventory inventory = Inventory
                                .builder()
                                .quantity(request.getQuantity())
                                .reservedQuantity(0)
                                .product(product)
                                .build();

                // =====================================================
                // 9. CONNECT INVENTORY
                // =====================================================

                product.setInventory(inventory);

                // =====================================================
                // 10. SAVE PRODUCT
                // =====================================================

                Product savedProduct = productRepository.save(product);

                // =====================================================
                // 11. RETURN RESPONSE
                // =====================================================

                return ProductResponse
                                .builder()
                                .productId(savedProduct.getProductId())
                                .name(savedProduct.getName())
                                .description(savedProduct.getDescription())
                                .price(savedProduct.getPrice())
                                .discountPrice(savedProduct.getDiscountPrice())
                                .unit(savedProduct.getUnit())
                                // ---------------------------------------------
                                // IMAGE
                                // ---------------------------------------------

                                .imageUrl(savedProduct.getImageUrl())
                                // ---------------------------------------------
                                // SUBCATEGORY
                                // ---------------------------------------------

                                .subCategoryId(savedProduct.getSubCategory().getSubCategoryId())
                                .subCategoryName(savedProduct.getSubCategory().getName())
                                // ---------------------------------------------
                                // CATEGORY
                                // ---------------------------------------------

                                .categoryId(savedProduct.getSubCategory().getCategory().getCategoryId())
                                .categoryName(savedProduct.getSubCategory().getCategory().getName())
                                // ---------------------------------------------
                                // BRAND
                                // ---------------------------------------------

                                .brandId(
                                                savedProduct.getBrand() != null
                                                                ? savedProduct.getBrand().getBrandId()
                                                                : null)
                                .brandName(
                                                savedProduct.getBrand() != null
                                                                ? savedProduct.getBrand().getName()
                                                                : null)
                                // ---------------------------------------------
                                // INVENTORY
                                // ---------------------------------------------

                                .quantity(savedProduct.getInventory().getQuantity())
                                // ---------------------------------------------
                                // STATUS
                                // ---------------------------------------------

                                .active(savedProduct.getActive())
                                .createdAt(savedProduct.getCreatedAt())
                                .updatedAt(savedProduct.getUpdatedAt())
                                .build();
        }

        // =====================================================
        // GET ALL PRODUCTS FOR FRONTEND
        // =====================================================
        @Transactional(readOnly = true)
        public List<ProductListResponse> getAllProductsForUser() {
                return productRepository
                                .findAll()
                                .stream()
                                .map(product -> {
                                        // =========================================
                                        // QUANTITY
                                        // =========================================

                                        Integer quantity = product.getInventory() != null
                                                        ? product.getInventory().getQuantity()
                                                                        - product.getInventory().getReservedQuantity()
                                                        : 0;

                                        // =========================================
                                        // IMAGE URL
                                        // =========================================

                                        String imageUrl = product.getImageUrl();

                                        // =========================================
                                        // BRAND
                                        // =========================================

                                        String brandName = product.getBrand() != null
                                                        ? product.getBrand().getName()
                                                        : null;

                                        // =========================================
                                        // SUBCATEGORY
                                        // =========================================

                                        String subCategoryName = product.getSubCategory() != null
                                                        ? product.getSubCategory().getName()
                                                        : null;

                                        // =========================================
                                        // CATEGORY
                                        // =========================================

                                        String categoryName = null;

                                        if (product.getSubCategory() != null &&
                                                        product.getSubCategory().getCategory() != null) {
                                                categoryName = product.getSubCategory().getCategory().getName();
                                        }

                                        // =========================================
                                        // STOCK
                                        // =========================================

                                        boolean inStock = Boolean.TRUE.equals(product.getActive()) && quantity > 0;

                                        // =========================================
                                        // RESPONSE
                                        // =========================================

                                        return ProductListResponse
                                                        .builder()
                                                        .productId(product.getProductId())
                                                        .name(product.getName())
                                                        .description(product.getDescription())
                                                        .price(product.getPrice())
                                                        .discountPrice(product.getDiscountPrice())
                                                        .unit(product.getUnit())
                                                        .imageUrl(imageUrl)
                                                        .brandName(brandName)
                                                        .categoryName(categoryName)
                                                        .subCategoryName(subCategoryName)
                                                        .quantity(quantity)
                                                        .active(product.getActive())
                                                        .inStock(inStock)
                                                        .build();
                                })
                                .toList();
        }

        // GEt Product By ID
        @Transactional(readOnly = true)
        public ProductResponse getProductById(String productId) {
                Product product = productRepository
                                .findById(productId)
                                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                Integer quantity = product.getInventory() != null
                                ? product.getInventory().getQuantity()
                                : 0;

                return ProductResponse
                                .builder()
                                .productId(product.getProductId())
                                .name(product.getName())
                                .description(product.getDescription())
                                .price(product.getPrice())
                                .discountPrice(product.getDiscountPrice())
                                .unit(product.getUnit())
                                .imageUrl(product.getImageUrl())
                                // =========================================
                                // SUBCATEGORY
                                // =========================================

                                .subCategoryId(
                                                product.getSubCategory() != null
                                                                ? product.getSubCategory().getSubCategoryId()
                                                                : null)
                                .subCategoryName(
                                                product.getSubCategory() != null
                                                                ? product.getSubCategory().getName()
                                                                : null)
                                // =========================================
                                // CATEGORY
                                // =========================================

                                .categoryId(
                                                product.getSubCategory() != null &&
                                                                product.getSubCategory().getCategory() != null
                                                                                ? product.getSubCategory().getCategory()
                                                                                                .getCategoryId()
                                                                                : null)
                                .categoryName(
                                                product.getSubCategory() != null &&
                                                                product.getSubCategory().getCategory() != null
                                                                                ? product.getSubCategory().getCategory()
                                                                                                .getName()
                                                                                : null)
                                // =========================================
                                // BRAND
                                // =========================================

                                .brandId(
                                                product.getBrand() != null ? product.getBrand().getBrandId() : null)
                                .brandName(
                                                product.getBrand() != null ? product.getBrand().getName() : null)
                                // =========================================
                                // INVENTORY
                                // =========================================

                                .quantity(quantity)
                                // =========================================
                                // STATUS
                                // =========================================

                                .active(product.getActive())
                                .createdAt(product.getCreatedAt())
                                .updatedAt(product.getUpdatedAt())
                                .build();
        }

        // Get Product By Subcategory
        @Transactional(readOnly = true)
        public List<ProductListResponse> getProductsBySubCategory(
                        String subCategoryId) {
                List<Product> products = productRepository.findBySubCategory_SubCategoryId(
                                subCategoryId);

                return products.stream().map(this::mapToProductListResponse).toList();
        }

        @Transactional
        public ProductDeleteResponse deleteProduct(String productId) {
                Product product = productRepository
                                .findByProductId(productId)
                                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                // Capture deletion time
                LocalDateTime deletedAt = LocalDateTime.now();

                productRepository.delete(product);

                return ProductDeleteResponse
                                .builder()
                                .productId(productId)
                                .message("Product deleted successfully")
                                .deletedAt(deletedAt)
                                .build();
        }

        // =====================================================
        // COMMON PRODUCT MAPPER
        // =====================================================
        private ProductListResponse mapToProductListResponse(Product product) {
                return ProductListResponse
                                .builder()
                                .productId(product.getProductId())
                                .name(product.getName())
                                .description(product.getDescription())
                                .price(product.getPrice())
                                .discountPrice(product.getDiscountPrice())
                                .unit(product.getUnit())
                                .imageUrl(product.getImageUrl())
                                .brandName(
                                                product.getBrand() != null ? product.getBrand().getName() : null)
                                .categoryName(
                                                product.getSubCategory() != null &&
                                                                product.getSubCategory().getCategory() != null
                                                                                ? product.getSubCategory().getCategory()
                                                                                                .getName()
                                                                                : null)
                                .subCategoryName(
                                                product.getSubCategory() != null
                                                                ? product.getSubCategory().getName()
                                                                : null)
                                .quantity(
                                                product.getInventory() != null
                                                                ? product.getInventory().getQuantity()
                                                                : 0)
                                .active(product.getActive())
                                .inStock(
                                                product.getInventory() != null &&
                                                                product.getInventory().getQuantity() != null &&
                                                                product.getInventory().getQuantity() > 0)
                                .build();
        }
}
