package com.example.ecommerce.store;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.common.PageResponse;
import com.example.ecommerce.store.dto.ShopResponse;

@RestController
@RequestMapping("/api/admin/shops")
public class AdminShopController {

    private final ShopService shopService;

    public AdminShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ShopResponse>>> list(
            @RequestParam(required = false) ShopStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(shopService.listForAdmin(status, page, size)));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<ShopResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(shopService.approve(id)));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id) {
        shopService.reject(id);
        return ResponseEntity.noContent().build();
    }
}
