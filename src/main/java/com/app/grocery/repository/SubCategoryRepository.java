package com.app.grocery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.grocery.entity.subcategory.SubCategory;

import java.util.List;

public interface SubCategoryRepository
        extends JpaRepository<SubCategory, String> {

    boolean existsByNameIgnoreCaseAndCategory_CategoryId(
            String name,
            String categoryId
    );

    List<SubCategory> findByCategory_CategoryId(
            String categoryId
    );
    
}