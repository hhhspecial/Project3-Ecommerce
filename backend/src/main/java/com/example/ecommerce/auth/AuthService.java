package com.example.ecommerce.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.auth.dto.LoginRequest;
import com.example.ecommerce.auth.dto.LoginResponse;
import com.example.ecommerce.auth.dto.RegisterRequest;
import com.example.ecommerce.common.exception.ConflictException;
import com.example.ecommerce.common.exception.UnauthorizedException;
import com.example.ecommerce.user.User;
import com.example.ecommerce.user.UserService;
import com.example.ecommerce.user.UserStatus;
import com.example.ecommerce.user.dto.UserResponse;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserService userService,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase();

        if (userService.usernameExists(username)) {
            throw new ConflictException("Tên đăng nhập đã tồn tại");
        }
        if (userService.emailExists(email)) {
            throw new ConflictException("Email đã được sử dụng");
        }

        String passwordHash = passwordEncoder.encode(request.password());
        User user = userService.createCustomer(
                username, email, passwordHash, request.firstName(), request.lastName());
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userService.findByUsername(request.username().trim())
                .orElseThrow(() -> new UnauthorizedException("Sai tên đăng nhập hoặc mật khẩu"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Sai tên đăng nhập hoặc mật khẩu");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("Tài khoản không khả dụng");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, "Bearer");
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) {
        return UserResponse.from(userService.getById(userId));
    }
}