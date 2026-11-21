package com.example.ecommerce.user.dto;

import java.time.LocalDate;

import com.example.ecommerce.user.Role;
import com.example.ecommerce.user.User;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthDate,
        String avatarUrl) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getBirthDate(),
                user.getAvatarUrl());
    }
}