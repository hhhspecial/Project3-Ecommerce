package com.example.ecommerce.product.dto;

import com.example.ecommerce.product.ProductImage;

public record ImageResponse(Long id, String imageUrl, int sortOrder) {

    public static ImageResponse from(ProductImage image) {
        return new ImageResponse(image.getId(), image.getImageUrl(), image.getSortOrder());
    }
}