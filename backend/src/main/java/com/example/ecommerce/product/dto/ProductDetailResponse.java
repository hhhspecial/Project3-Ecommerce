package com.example.ecommerce.product.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.ecommerce.product.Category;
import com.example.ecommerce.product.Product;

public record ProductDetailResponse(
        Long id,
        String name,
        String summary,
        String description,
        String coverUrl,
        BigDecimal avgRating,
        int reviewCount,
        Long shopId,
        String shopName,
        Long categoryId,
        String categoryName,
        List<SkuResponse> skus) {

    public static ProductDetailResponse from(Product product, List<SkuResponse> skus) {
        Category category = product.getCategory();
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getSummary(),
                product.getDescription(),
                product.getCoverUrl(),
                product.getAvgRating(),
                product.getReviewCount(),
                product.getShop().getId(),
                product.getShop().getName(),
                category != null ? category.getId() : null,
                category != null ? category.getName() : null,
                skus);
    }
}
