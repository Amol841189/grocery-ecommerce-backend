package com.app.grocery.service;

import com.app.grocery.dto.brand.request.BrandCreateRequest;
import com.app.grocery.dto.brand.response.BrandListResponse;
import com.app.grocery.dto.brand.response.BrandResponse;
import com.app.grocery.entity.brand.Brand;
import com.app.grocery.entity.subcategory.SubCategory;
import com.app.grocery.repository.BrandRepository;
import com.app.grocery.repository.SubCategoryRepository;
import com.app.grocery.util.BrandIdGenerator;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BrandService {

  private final BrandRepository brandRepository;
  private final SubCategoryRepository subCategoryRepository;

  public BrandService(
    BrandRepository brandRepository,
    SubCategoryRepository subCategoryRepository
  ) {
    this.brandRepository = brandRepository;
    this.subCategoryRepository = subCategoryRepository;
  }

  // =====================================================
  // ADD BRAND
  // =====================================================

  public BrandResponse addBrand(BrandCreateRequest request) {
    if (brandRepository.existsByNameIgnoreCase(request.getName())) {
      throw new RuntimeException("Brand already exists");
    }

    LocalDateTime now = LocalDateTime.now();

    Brand brand = Brand
      .builder()
      .brandId(BrandIdGenerator.generate())
      .name(request.getName())
      .description(request.getDescription())
      .createdAt(now)
      .updatedAt(now)
      .build();

    Brand savedBrand = brandRepository.save(brand);

    return BrandResponse
      .builder()
      .brandId(savedBrand.getBrandId())
      .name(savedBrand.getName())
      .description(savedBrand.getDescription())
      .createdAt(savedBrand.getCreatedAt())
      .updatedAt(savedBrand.getUpdatedAt())
      .build();
  }

  // =====================================================
  // GET ALL BRANDS
  // =====================================================

  @Transactional(readOnly = true)
  public List<BrandListResponse> getAllBrands() {
    return brandRepository
      .findAll()
      .stream()
      .map(brand -> new BrandListResponse(brand.getBrandId(), brand.getName()))
      .toList();
  }

  // =====================================================
  // GET BRANDS BY SUBCATEGORY
  // =====================================================

  @Transactional(readOnly = true)
  public List<BrandListResponse> getBrandsBySubCategory(String subCategoryId) {
    // -----------------------------------------------
    // CHECK SUBCATEGORY EXISTS
    // -----------------------------------------------

    if (!subCategoryRepository.existsById(subCategoryId)) {
      throw new RuntimeException("Subcategory not found: " + subCategoryId);
    }

    // -----------------------------------------------
    // GET BRANDS
    // -----------------------------------------------

    return brandRepository
      .findBrandsBySubCategory(subCategoryId)
      .stream()
      .map(brand -> new BrandListResponse(brand.getBrandId(), brand.getName()))
      .toList();
  }

  // =====================================================
  // ASSIGN BRAND TO SUBCATEGORY
  // =====================================================

  @Transactional
  public BrandListResponse assignBrandToSubCategory(
    String brandId,
    String subCategoryId
  ) {
    // -----------------------------------------------
    // FIND BRAND
    // -----------------------------------------------

    Brand brand = brandRepository
      .findById(brandId)
      .orElseThrow(() -> new RuntimeException("Brand not found: " + brandId));

    // -----------------------------------------------
    // FIND SUBCATEGORY
    // -----------------------------------------------

    SubCategory subCategory = subCategoryRepository
      .findById(subCategoryId)
      .orElseThrow(() ->
        new RuntimeException("Subcategory not found: " + subCategoryId)
      );

    // -----------------------------------------------
    // ASSIGN
    // -----------------------------------------------

    if (!brand.getSubCategories().contains(subCategory)) {
      brand.getSubCategories().add(subCategory);
    }

    // -----------------------------------------------
    // UPDATE TIME
    // -----------------------------------------------

    brand.setUpdatedAt(LocalDateTime.now());

    // -----------------------------------------------
    // SAVE
    // -----------------------------------------------

    Brand savedBrand = brandRepository.save(brand);

    return new BrandListResponse(savedBrand.getBrandId(), savedBrand.getName());
  }
}
