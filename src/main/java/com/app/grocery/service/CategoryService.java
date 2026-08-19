package com.app.grocery.service;

import com.app.grocery.dto.CategoryCreateRequest;
import com.app.grocery.dto.CategoryCreateResponse;
import com.app.grocery.dto.CategoryListResponse;
import com.app.grocery.entity.Category;
import com.app.grocery.repository.CategoryRepository;
import com.app.grocery.util.CategoryIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryCreateResponse addCategory(CategoryCreateRequest request) {

        // Check duplicate category name
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException("Category already exists");
        }

        // Current time
        LocalDateTime now = LocalDateTime.now();

        // Create Category entity
        Category category = Category.builder()
                .categoryId(CategoryIdGenerator.generate())
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Save category
        Category savedCategory = categoryRepository.save(category);

        // Return response DTO
        return CategoryCreateResponse.builder()
                .categoryId(savedCategory.getCategoryId())
                .name(savedCategory.getName())
                .description(savedCategory.getDescription())
                .createdAt(savedCategory.getCreatedAt())
                .updatedAt(savedCategory.getUpdatedAt())
                .build();
    }

    public List<CategoryListResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(category ->
                        new CategoryListResponse(
                                category.getCategoryId(),
                                category.getName()
                        )
                )
                .toList();
    }
}