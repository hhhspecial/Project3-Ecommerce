package com.example.ecommerce.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SkuRequest(

        @NotBlank(message = "Mã SKU không được để trống")
        @Size(max = 100, message = "Mã SKU tối đa 100 ký tự")
        String skuCode,

        @Size(max = 150, message = "Tên biến thể tối đa 150 ký tự")
        String variantName,

        @Size(max = 50, message = "Màu tối đa 50 ký tự")
        String color,

        @Size(max = 50, message = "Kích thước tối đa 50 ký tự")
        String size,

        @NotNull(message = "Giá không được để trống")
        @Positive(message = "Giá phải lớn hơn 0")
        BigDecimal price,

        @NotNull(message = "Tồn kho không được để trống")
        @Min(value = 0, message = "Tồn kho không được âm")
        Integer stockQuantity
) {
}
