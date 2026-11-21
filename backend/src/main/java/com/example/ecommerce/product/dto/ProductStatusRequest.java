package com.example.ecommerce.product.dto;

import com.example.ecommerce.product.ProductStatus;

import jakarta.validation.constraints.NotNull;

public record ProductStatusRequest(
        @NotNull(message = "Vui lòng chọn trạng thái") ProductStatus status) {
}
