package com.example.ecommerce.product.dto;

import java.math.BigDecimal;

import com.example.ecommerce.product.Category;
import com.example.ecommerce.product.Product;

public record ProductResponse(
        Long id,
        String name,
        String summary,
        String coverUrl,
        BigDecimal minPrice,
        BigDecimal avgRating,
        int reviewCount,
        Long shopId,
        String shopName,
        Long categoryId,
        String categoryName) {

    public static ProductResponse from(Product product, BigDecimal minPrice) {
        Category category = product.getCategory();
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSummary(),
                product.getCoverUrl(),
                minPrice,
                product.getAvgRating(),
                product.getReviewCount(),
                product.getShop().getId(),
                product.getShop().getName(),
                category != null ? category.getId() : null,
                category != null ? category.getName() : null);
    }
}