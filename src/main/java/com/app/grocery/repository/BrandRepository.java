package com.app.grocery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.grocery.entity.brand.Brand;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, String> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByBrandIdAndSubCategories_SubCategoryId(
            String brandId,
            String subCategoryId
    );
    
    // =====================================================
    // FIND BRANDS BY SUBCATEGORY
    // =====================================================

    @Query("""
        SELECT DISTINCT b
        FROM Brand b
        JOIN b.subCategories sc
        WHERE sc.subCategoryId = :subCategoryId
        ORDER BY b.name
    """)
    List<Brand> findBrandsBySubCategory(
            @Param("subCategoryId")
            String subCategoryId
    );
}