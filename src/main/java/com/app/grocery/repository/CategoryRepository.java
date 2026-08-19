package com.app.grocery.repository;

import com.app.grocery.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {

    boolean existsByNameIgnoreCase(String name);
}