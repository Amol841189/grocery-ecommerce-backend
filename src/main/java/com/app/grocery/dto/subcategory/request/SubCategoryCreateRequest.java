package com.app.grocery.dto.subcategory.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubCategoryCreateRequest {

    private String name;

    private String description;

    private String categoryId;
}