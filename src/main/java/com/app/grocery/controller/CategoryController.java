package com.app.grocery.controller;

import com.app.grocery.dto.category.request.CategoryCreateRequest;
import com.app.grocery.dto.category.response.CategoryCreateResponse;
import com.app.grocery.dto.category.response.CategoryListResponse;
import com.app.grocery.service.CategoryService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PostMapping
  public ResponseEntity<CategoryCreateResponse> addCategory(
    @RequestBody CategoryCreateRequest request
  ) {
    CategoryCreateResponse response = categoryService.addCategory(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // GET ALL CATEGORIES
  @GetMapping
  public ResponseEntity<List<CategoryListResponse>> getAllCategories() {
    List<CategoryListResponse> response = categoryService.getAllCategories();

    return ResponseEntity.ok(response);
  }
}
