package com.pablospring.miapi.presentation.dto.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String createdFor;
    private String imagenUrl;
}
