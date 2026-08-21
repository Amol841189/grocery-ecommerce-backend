package com.app.grocery.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.grocery.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
  boolean existsByNameIgnoreCaseAndBrand_BrandIdAndSubCategory_SubCategoryId(
    String name,
    String brandId,
    String subCategoryId
  );

  List<Product> findBySubCategory_SubCategoryId(String subCategoryId);

  Optional<Product> findByProductId(String productId);
}
