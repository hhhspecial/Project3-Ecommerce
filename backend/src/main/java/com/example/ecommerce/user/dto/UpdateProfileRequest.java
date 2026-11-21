package com.example.ecommerce.user.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @Size(max = 100, message = "Tên tối đa 100 ký tự")
        String firstName,

        @Size(max = 100, message = "Họ tối đa 100 ký tự")
        String lastName,

        @Pattern(regexp = "^(0|\\+84)\\d{9,10}$", message = "Số điện thoại không hợp lệ")
        String phoneNumber,

        @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
        LocalDate birthDate
) {
}