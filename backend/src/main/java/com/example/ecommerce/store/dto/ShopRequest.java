package com.example.ecommerce.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShopRequest(

        @NotBlank(message = "Tên cửa hàng không được để trống")
        @Size(max = 150, message = "Tên cửa hàng tối đa 150 ký tự")
        String name,

        @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
        String description
) {
}