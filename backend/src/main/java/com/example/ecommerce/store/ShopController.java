package com.example.ecommerce.store;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.store.dto.ShopRequest;
import com.example.ecommerce.store.dto.ShopResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShopResponse>> register(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ShopRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(shopService.register(userId, request)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ShopResponse>> getMyShop(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(shopService.getMyShop(userId)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ShopResponse>> updateMyShop(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ShopRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(shopService.updateMyShop(userId, request)));
    }
}