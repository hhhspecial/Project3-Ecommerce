package com.example.ecommerce.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SellerProductRequest(

        @NotBlank(message = "Tên sản phẩm không được để trống")
        @Size(max = 255, message = "Tên sản phẩm tối đa 255 ký tự")
        String name,

        @Size(max = 500, message = "Mô tả ngắn tối đa 500 ký tự")
        String summary,

        @Size(max = 5000, message = "Mô tả chi tiết tối đa 5000 ký tự")
        String description,

        @NotNull(message = "Vui lòng chọn danh mục")
        Long categoryId
) {
}