package com.app.grocery.repository;

import com.app.grocery.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

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