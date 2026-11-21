package com.example.ecommerce.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.common.exception.ResourceNotFoundException;
import com.example.ecommerce.user.dto.UpdateProfileRequest;
import com.example.ecommerce.user.dto.UserResponse;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User createCustomer(String username, String email, String passwordHash,
                               String firstName, String lastName) {
        User user = new User(username, email, passwordHash, Role.CUSTOMER, firstName, lastName);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        return UserResponse.from(getById(userId));
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getById(userId);
        user.updateProfile(
                request.firstName(),
                request.lastName(),
                request.phoneNumber(),
                request.birthDate());
        return UserResponse.from(user);
    }

    @Transactional
    public void promoteToSeller(Long userId) {
        User user = getById(userId);
        user.changeRole(Role.SELLER);
    }
}