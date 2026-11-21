package com.example.ecommerce.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(

        @NotBlank(message = "Tên người nhận không được để trống")
        @Size(max = 150, message = "Tên người nhận tối đa 150 ký tự")
        String recipientName,

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(0|\\+84)\\d{9,10}$", message = "Số điện thoại không hợp lệ")
        String phoneNumber,

        @NotBlank(message = "Địa chỉ không được để trống")
        @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
        String addressLine1,

        @Size(max = 255, message = "Địa chỉ bổ sung tối đa 255 ký tự")
        String addressLine2,

        @Size(max = 100, message = "Phường, xã tối đa 100 ký tự")
        String ward,

        @Size(max = 100, message = "Quận, huyện tối đa 100 ký tự")
        String district,

        @NotBlank(message = "Tỉnh, thành phố không được để trống")
        @Size(max = 100, message = "Tỉnh, thành phố tối đa 100 ký tự")
        String city,

        @Size(max = 100, message = "Quốc gia tối đa 100 ký tự")
        String country,

        @Size(max = 20, message = "Mã bưu điện tối đa 20 ký tự")
        String postalCode,

        Boolean defaultAddress
) {
}