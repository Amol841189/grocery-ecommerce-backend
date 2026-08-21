package com.app.grocery.dto.brand.request;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class BrandCreateRequest {

  private String name;

  private String description;
}
