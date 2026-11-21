package com.example.ecommerce.product;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.common.PageResponse;
import com.example.ecommerce.product.dto.ProductStatusRequest;
import com.example.ecommerce.product.dto.SellerProductRequest;
import com.example.ecommerce.product.dto.SellerProductResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/seller/products")
public class SellerProductController {

    private final SellerProductService sellerProductService;

    public SellerProductController(SellerProductService sellerProductService) {
        this.sellerProductService = sellerProductService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SellerProductResponse>> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SellerProductRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(sellerProductService.create(userId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SellerProductResponse>>> list(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(sellerProductService.list(userId, status, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SellerProductResponse>> get(
            @AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(sellerProductService.get(userId, id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SellerProductResponse>> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody SellerProductRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(sellerProductService.update(userId, id, request)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<SellerProductResponse>> changeStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody ProductStatusRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(sellerProductService.changeStatus(userId, id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        Long userId = Long.valueOf(jwt.getSubject());
        sellerProductService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
