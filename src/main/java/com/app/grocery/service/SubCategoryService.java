package com.app.grocery.service;

import com.app.grocery.dto.SubCategoryCreateRequest;
import com.app.grocery.dto.SubCategoryListResponse;
import com.app.grocery.dto.SubCategoryResponse;
import com.app.grocery.entity.Category;
import com.app.grocery.entity.SubCategory;
import com.app.grocery.util.SubCategoryIdGenerator;
import com.app.grocery.repository.CategoryRepository;
import com.app.grocery.repository.SubCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    public SubCategoryService(
            SubCategoryRepository subCategoryRepository,
            CategoryRepository categoryRepository
    ) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    // =====================================================
    // CREATE SUBCATEGORY
    // =====================================================

        @Transactional
        public SubCategoryResponse addSubCategory(
                        SubCategoryCreateRequest request
                ) {

                // ==========================================
                // 1. FIND CATEGORY
                // ==========================================

                Category category =
                        categoryRepository
                                .findById(request.getCategoryId())
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Category not found: "
                                                        + request.getCategoryId()
                                        )
                                );


                // ==========================================
                // 2. CHECK DUPLICATE
                // ==========================================

                boolean exists =
                        subCategoryRepository
                                .existsByNameIgnoreCaseAndCategory_CategoryId(
                                        request.getName(),
                                        request.getCategoryId()
                                );

                if (exists) {

                        throw new RuntimeException(
                                "SubCategory already exists in this category"
                        );
                }


                // ==========================================
                // 3. CREATE SUBCATEGORY
                // ==========================================

                SubCategory subCategory =
                        new SubCategory();

                // Generate SUB-XXXXXXXXXXXX ID
                subCategory.setSubCategoryId(
                        SubCategoryIdGenerator.generate()
                );

                subCategory.setName(
                        request.getName()
                );

                subCategory.setDescription(
                        request.getDescription()
                );

                subCategory.setCategory(
                        category
                );


                // ==========================================
                // 4. SAVE
                // ==========================================

                SubCategory saved =
                        subCategoryRepository.save(
                                subCategory
                        );


                // ==========================================
                // 5. RESPONSE
                // ==========================================

                return mapToResponse(saved);
        }
    // =====================================================
    // GET ALL SUBCATEGORIES
    // =====================================================

    @Transactional(readOnly = true)
    public List<SubCategoryListResponse> getAllSubCategories() {

        return subCategoryRepository
                .findAll()
                .stream()
                .map(subCategory ->
                        new SubCategoryListResponse(
                                subCategory.getSubCategoryId(),
                                subCategory.getName()
                        )
                )
                .toList();
    }


    // =====================================================
    // GET SUBCATEGORY BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public SubCategoryResponse getSubCategoryById(
            String subCategoryId
    ) {

        SubCategory subCategory =
                subCategoryRepository
                        .findById(subCategoryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "SubCategory not found: "
                                                + subCategoryId
                                )
                        );

        return mapToResponse(subCategory);
    }


    // =====================================================
    // GET SUBCATEGORIES BY CATEGORY
    // =====================================================

    @Transactional(readOnly = true)
    public List<SubCategoryListResponse> getSubCategoriesByCategory(
                                String categoryId ) {

        // Make sure category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException(
                    "Category not found: " + categoryId
            );
        }

        return subCategoryRepository
                .findByCategory_CategoryId(categoryId)
                .stream()
                .map(subCategory ->
                        new SubCategoryListResponse(
                                subCategory.getSubCategoryId(),
                                subCategory.getName()
                        )
                )
                .toList();
    }


    // =====================================================
    // UPDATE SUBCATEGORY
    // =====================================================

    @Transactional
    public SubCategoryResponse updateSubCategory(
            String subCategoryId,
            SubCategoryCreateRequest request
    ) {

        SubCategory subCategory =
                subCategoryRepository
                        .findById(subCategoryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "SubCategory not found: "
                                                + subCategoryId
                                )
                        );

        Category category =
                categoryRepository
                        .findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found: "
                                                + request.getCategoryId()
                                )
                        );

        boolean exists =
                subCategoryRepository
                        .existsByNameIgnoreCaseAndCategory_CategoryId(
                                request.getName(),
                                request.getCategoryId()
                        );

        if (exists &&
                !subCategory.getName()
                        .equalsIgnoreCase(request.getName())) {

            throw new RuntimeException(
                    "SubCategory already exists in this category"
            );
        }

        subCategory.setName(request.getName());
        subCategory.setDescription(request.getDescription());
        subCategory.setCategory(category);

        SubCategory updated =
                subCategoryRepository.save(subCategory);

        return mapToResponse(updated);
    }


    // =====================================================
    // DELETE SUBCATEGORY
    // =====================================================

    @Transactional
    public void deleteSubCategory(
            String subCategoryId
    ) {

        if (!subCategoryRepository.existsById(subCategoryId)) {

            throw new RuntimeException(
                    "SubCategory not found: "
                            + subCategoryId
            );
        }

        subCategoryRepository.deleteById(subCategoryId);
    }


    // =====================================================
    // RESPONSE MAPPER
    // =====================================================

    private SubCategoryResponse mapToResponse(
            SubCategory subCategory
    ) {

        Category category =
                subCategory.getCategory();

        return new SubCategoryResponse(

                subCategory.getSubCategoryId(),

                subCategory.getName(),

                subCategory.getDescription(),

                category.getCategoryId(),

                category.getName()
        );
    }
}