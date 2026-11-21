package com.example.ecommerce.product.dto;

import java.math.BigDecimal;

import com.example.ecommerce.product.ProductSku;

public record SellerSkuResponse(
        Long id,
        Long productId,
        String skuCode,
        String variantName,
        String color,
        String size,
        BigDecimal price,
        int stockQuantity) {

    public static SellerSkuResponse from(ProductSku s) {
        return new SellerSkuResponse(
                s.getId(),
                s.getProductId(),
                s.getSkuCode(),
                s.getVariantName(),
                s.getColor(),
                s.getSize(),
                s.getPrice(),
                s.getStockQuantity());
    }
}