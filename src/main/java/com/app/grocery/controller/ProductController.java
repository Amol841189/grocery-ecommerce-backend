package com.app.grocery.controller;

import com.app.grocery.dto.product.request.ProductCreateRequest;
import com.app.grocery.dto.product.response.ProductDeleteResponse;
import com.app.grocery.dto.product.response.ProductListResponse;
import com.app.grocery.dto.product.response.ProductResponse;
import com.app.grocery.service.ProductService;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

        private final ProductService productService;

        public ProductController(ProductService productService) {
                this.productService = productService;
        }

        // =====================================================
        // CREATE PRODUCT
        // =====================================================

        @PostMapping(consumes = "multipart/form-data")
        public ResponseEntity<ProductResponse> createProduct(@ModelAttribute ProductCreateRequest request)
                        throws IOException {

                return ResponseEntity.ok(productService.addProduct(request));
        }

        // =====================================================
        // GET ALL PRODUCTS
        // =====================================================

        @GetMapping
        public ResponseEntity<List<ProductListResponse>> getAllProducts() {

                List<ProductListResponse> products = productService.getAllProductsForUser();

                return ResponseEntity.ok(products);
        }

        // =====================================================
        // GET PRODUCT BY ID
        // =====================================================

        @GetMapping("/{productId}")
        public ResponseEntity<ProductResponse> getProductById(@PathVariable String productId) {

                ProductResponse response = productService.getProductById(productId);

                return ResponseEntity.ok(response);
        }

        // =====================================================
        // GET PRODUCT BY SUB-CATEGORY
        // =====================================================

        @GetMapping("/subcategory/{subCategoryId}")
        public ResponseEntity<List<ProductListResponse>> getProductsBySubCategory(@PathVariable String subCategoryId) {

                List<ProductListResponse> products = productService.getProductsBySubCategory(subCategoryId);

                return ResponseEntity.ok(products);
        }

        // =====================================================
        // DELETE PRODUCT
        // =====================================================

        @DeleteMapping("/{productId}")
        public ResponseEntity<ProductDeleteResponse> deleteProduct(@PathVariable String productId) {

                ProductDeleteResponse response = productService.deleteProduct(productId);

                return ResponseEntity.ok(response);
        }
}