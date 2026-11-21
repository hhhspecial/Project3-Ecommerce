package com.example.ecommerce.product;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.product.dto.SellerImageResponse;

@RestController
@RequestMapping("/api/seller/products/{productId}/images")
public class SellerImageController {

    private final SellerImageService sellerImageService;

    public SellerImageController(SellerImageService sellerImageService) {
        this.sellerImageService = sellerImageService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SellerImageResponse>>> list(
            @AuthenticationPrincipal Jwt jwt, @PathVariable Long productId) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(sellerImageService.list(userId, productId)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SellerImageResponse>> upload(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(sellerImageService.upload(userId, productId, file)));
    }

    @PutMapping("/{imageId}/cover")
    public ResponseEntity<ApiResponse<SellerImageResponse>> setCover(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(sellerImageService.setCover(userId, productId, imageId)));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        Long userId = Long.valueOf(jwt.getSubject());
        sellerImageService.delete(userId, productId, imageId);
        return ResponseEntity.noContent().build();
    }
}
