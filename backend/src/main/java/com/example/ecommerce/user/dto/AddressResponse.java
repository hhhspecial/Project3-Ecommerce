package com.example.ecommerce.user.dto;

import com.example.ecommerce.user.Address;

public record AddressResponse(
        Long id,
        String recipientName,
        String phoneNumber,
        String addressLine1,
        String addressLine2,
        String ward,
        String district,
        String city,
        String country,
        String postalCode,
        boolean defaultAddress) {

    public static AddressResponse from(Address a) {
        return new AddressResponse(
                a.getId(),
                a.getRecipientName(),
                a.getPhoneNumber(),
                a.getAddressLine1(),
                a.getAddressLine2(),
                a.getWard(),
                a.getDistrict(),
                a.getCity(),
                a.getCountry(),
                a.getPostalCode(),
                a.isDefaultAddress());
    }
}
