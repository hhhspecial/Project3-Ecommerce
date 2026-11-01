package com.example.ecommerce.product.dto;

import com.example.ecommerce.product.Category;

public record CategoryResponse(
        Long id,
        Long parentId,
        String name,
        String description
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getParentId(),
                category.getName(),
                category.getDescription()
        );
    }
}
