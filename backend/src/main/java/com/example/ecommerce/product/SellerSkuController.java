package com.example.ecommerce.product;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.product.dto.SellerSkuResponse;
import com.example.ecommerce.product.dto.SkuRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/seller/products/{productId}/skus")
public class SellerSkuController {

    private final SellerSkuService sellerSkuService;

    public SellerSkuController(SellerSkuService sellerSkuService) {
        this.sellerSkuService = sellerSkuService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SellerSkuResponse>>> list(
            @AuthenticationPrincipal Jwt jwt, @PathVariable Long productId) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(sellerSkuService.list(userId, productId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SellerSkuResponse>> create(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @Valid @RequestBody SkuRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(sellerSkuService.create(userId, productId, request)));
    }

    @PutMapping("/{skuId}")
    public ResponseEntity<ApiResponse<SellerSkuResponse>> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @PathVariable Long skuId,
            @Valid @RequestBody SkuRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(sellerSkuService.update(userId, productId, skuId, request)));
    }

    @DeleteMapping("/{skuId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @PathVariable Long skuId) {
        Long userId = Long.valueOf(jwt.getSubject());
        sellerSkuService.delete(userId, productId, skuId);
        return ResponseEntity.noContent().build();
    }
}
