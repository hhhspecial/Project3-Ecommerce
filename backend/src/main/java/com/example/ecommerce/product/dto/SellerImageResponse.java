package com.example.ecommerce.product.dto;

import com.example.ecommerce.product.ProductImage;

public record SellerImageResponse(Long id, String imageUrl, int sortOrder) {

    public static SellerImageResponse from(ProductImage image) {
        return new SellerImageResponse(image.getId(), image.getImageUrl(), image.getSortOrder());
    }
}