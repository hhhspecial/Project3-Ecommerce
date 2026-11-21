package com.example.ecommerce.product.dto;

import java.time.LocalDateTime;

import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.ProductStatus;

public record SellerProductResponse(
        Long id,
        String name,
        String summary,
        String description,
        String coverUrl,
        ProductStatus status,
        Long categoryId,
        String categoryName,
        LocalDateTime createdAt) {

    public static SellerProductResponse from(Product p) {
        return new SellerProductResponse(
                p.getId(),
                p.getName(),
                p.getSummary(),
                p.getDescription(),
                p.getCoverUrl(),
                p.getStatus(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getCreatedAt());
    }
}
