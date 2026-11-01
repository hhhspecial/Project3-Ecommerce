package com.example.ecommerce.product.dto;

import java.math.BigDecimal;

import com.example.ecommerce.product.ProductSku;

public record SkuResponse(
        Long id,
        String skuCode,
        String variantName,
        String color,
        String size,
        BigDecimal price,
        int stockQuantity) {

    public static SkuResponse from(ProductSku sku) {
        return new SkuResponse(
                sku.getId(),
                sku.getSkuCode(),
                sku.getVariantName(),
                sku.getColor(),
                sku.getSize(),
                sku.getPrice(),
                sku.getStockQuantity());
    }
}
