package com.app.grocery.controller;

import com.app.grocery.dto.SubCategoryCreateRequest;
import com.app.grocery.dto.SubCategoryListResponse;
import com.app.grocery.dto.SubCategoryResponse;
import com.app.grocery.service.SubCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// POST   /api/v1/subcategories
// GET    /api/v1/subcategories
// GET    /api/v1/subcategories/{id}
// GET    /api/v1/subcategories/category/{categoryId}
// PUT    /api/v1/subcategories/{id}
// DELETE /api/v1/subcategories/{id}

@RestController
@RequestMapping("/api/v1/subcategories")
@RequiredArgsConstructor
public class SubCategoryController {

  private final SubCategoryService subCategoryService;

  // =====================================================
  // CREATE SUBCATEGORY
  // =====================================================

  @PostMapping
  public ResponseEntity<SubCategoryResponse> createSubCategory(
    @RequestBody SubCategoryCreateRequest request
  ) {
    SubCategoryResponse response = subCategoryService.addSubCategory(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // =====================================================
  // GET ALL SUBCATEGORIES
  // =====================================================

  @GetMapping
  public ResponseEntity<List<SubCategoryListResponse>> getAllSubCategories() {
    return ResponseEntity.ok(subCategoryService.getAllSubCategories());
  }

  // =====================================================
  // GET SUBCATEGORY BY ID
  // =====================================================

  @GetMapping("/{subCategoryId}")
  public ResponseEntity<SubCategoryResponse> getSubCategoryById(
    @PathVariable String subCategoryId
  ) {
    return ResponseEntity.ok(
      subCategoryService.getSubCategoryById(subCategoryId)
    );
  }

  // =====================================================
  // GET SUBCATEGORIES BY CATEGORY
  // =====================================================

  @GetMapping("/category/{categoryId}")
  public ResponseEntity<List<SubCategoryListResponse>> getSubCategoriesByCategory(
    @PathVariable String categoryId
  ) {
    return ResponseEntity.ok(
      subCategoryService.getSubCategoriesByCategory(categoryId)
    );
  }

  // =====================================================
  // UPDATE SUBCATEGORY
  // =====================================================

  @PutMapping("/{subCategoryId}")
  public ResponseEntity<SubCategoryResponse> updateSubCategory(
    @PathVariable String subCategoryId,
    @RequestBody SubCategoryCreateRequest request
  ) {
    return ResponseEntity.ok(
      subCategoryService.updateSubCategory(subCategoryId, request)
    );
  }

  // =====================================================
  // DELETE SUBCATEGORY
  // =====================================================

  @DeleteMapping("/{subCategoryId}")
  public ResponseEntity<Void> deleteSubCategory(
    @PathVariable String subCategoryId
  ) {
    subCategoryService.deleteSubCategory(subCategoryId);

    return ResponseEntity.noContent().build();
  }
}
