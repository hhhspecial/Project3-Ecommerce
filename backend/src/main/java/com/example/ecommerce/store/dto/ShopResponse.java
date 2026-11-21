package com.example.ecommerce.store.dto;

import java.time.LocalDateTime;

import com.example.ecommerce.store.Shop;
import com.example.ecommerce.store.ShopStatus;

public record ShopResponse(
        Long id,
        Long ownerUserId,
        String name,
        String description,
        String logoUrl,
        ShopStatus status,
        LocalDateTime createdAt) {

    public static ShopResponse from(Shop shop) {
        return new ShopResponse(
                shop.getId(),
                shop.getOwnerUserId(),
                shop.getName(),
                shop.getDescription(),
                shop.getLogoUrl(),
                shop.getStatus(),
                shop.getCreatedAt());
    }
}
