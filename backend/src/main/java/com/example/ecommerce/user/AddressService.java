package com.example.ecommerce.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.common.exception.ResourceNotFoundException;
import com.example.ecommerce.user.dto.AddressRequest;
import com.example.ecommerce.user.dto.AddressResponse;

@Service
public class AddressService {

    private static final String DEFAULT_COUNTRY = "Vietnam";

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByDefaultAddressDescCreatedAtDesc(userId)
                .stream()
                .map(AddressResponse::from)
                .toList();
    }

    @Transactional
    public AddressResponse create(Long userId, AddressRequest request) {
        boolean isFirst = addressRepository.countByUserId(userId) == 0;

        Address address = new Address(
                userId,
                request.recipientName().trim(),
                request.phoneNumber(),
                request.addressLine1().trim(),
                request.addressLine2(),
                request.ward(),
                request.district(),
                request.city().trim(),
                countryOrDefault(request.country()),
                request.postalCode(),
                isFirst);
        Address saved = addressRepository.save(address);

        if (!isFirst && Boolean.TRUE.equals(request.defaultAddress())) {
            applyDefault(userId, saved.getId());
        }
        return AddressResponse.from(saved);
    }

    @Transactional
    public AddressResponse update(Long userId, Long addressId, AddressRequest request) {
        Address address = findOwned(userId, addressId);
        address.update(
                request.recipientName().trim(),
                request.phoneNumber(),
                request.addressLine1().trim(),
                request.addressLine2(),
                request.ward(),
                request.district(),
                request.city().trim(),
                countryOrDefault(request.country()),
                request.postalCode());

        if (Boolean.TRUE.equals(request.defaultAddress())) {
            applyDefault(userId, addressId);
        }
        return AddressResponse.from(address);
    }

    @Transactional
    public AddressResponse setDefault(Long userId, Long addressId) {
        Address address = findOwned(userId, addressId);
        applyDefault(userId, addressId);
        return AddressResponse.from(address);
    }

    @Transactional
    public void delete(Long userId, Long addressId) {
        Address address = findOwned(userId, addressId);
        boolean wasDefault = address.isDefaultAddress();
        addressRepository.delete(address);

        if (wasDefault) {
            List<Address> remaining =
                    addressRepository.findByUserIdOrderByDefaultAddressDescCreatedAtDesc(userId);
            if (!remaining.isEmpty()) {
                remaining.get(0).setDefaultAddress(true);
            }
        }
    }

    private Address findOwned(Long userId, Long addressId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));
    }

    // Đặt addressId làm mặc định, mọi địa chỉ khác của người này thành không mặc định
    private void applyDefault(Long userId, Long addressId) {
        List<Address> all = addressRepository.findByUserIdOrderByDefaultAddressDescCreatedAtDesc(userId);
        for (Address a : all) {
            a.setDefaultAddress(a.getId().equals(addressId));
        }
    }

    private String countryOrDefault(String country) {
        if (country == null || country.isBlank()) {
            return DEFAULT_COUNTRY;
        }
        return country.trim();
    }
}
