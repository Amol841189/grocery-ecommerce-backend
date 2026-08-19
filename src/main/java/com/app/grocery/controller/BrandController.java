package com.app.grocery.controller;

import com.app.grocery.dto.BrandCreateRequest;
import com.app.grocery.dto.BrandListResponse;
import com.app.grocery.dto.BrandResponse;
import com.app.grocery.service.BrandService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

  private final BrandService brandService;

  // =====================================================
  // CREATE BRAND
  // =====================================================

  @PostMapping
  public ResponseEntity<BrandResponse> addBrand(
    @RequestBody BrandCreateRequest request
  ) {
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(brandService.addBrand(request));
  }

  // =====================================================
  // GET ALL BRANDS
  // =====================================================

  @GetMapping
  public ResponseEntity<List<BrandListResponse>> getAllBrands() {
    return ResponseEntity.ok(brandService.getAllBrands());
  }

  // =====================================================
  // GET BRANDS BY SUBCATEGORY
  // =====================================================

  @GetMapping("/subcategory/{subCategoryId}")
  public ResponseEntity<List<BrandListResponse>> getBrandsBySubCategory(
    @PathVariable String subCategoryId
  ) {
    return ResponseEntity.ok(
      brandService.getBrandsBySubCategory(subCategoryId)
    );
  }

  // =====================================================
  // ASSIGN BRAND TO SUBCATEGORY
  // =====================================================

  @PostMapping("/{brandId}/subcategory/{subCategoryId}")
  public ResponseEntity<BrandListResponse> assignBrandToSubCategory(
    @PathVariable String brandId,
    @PathVariable String subCategoryId
  ) {
    return ResponseEntity.ok(
      brandService.assignBrandToSubCategory(brandId, subCategoryId)
    );
  }
}
