package com.example.ecommerce.product;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.common.PageResponse;
import com.example.ecommerce.common.exception.ResourceNotFoundException;
import com.example.ecommerce.product.dto.ProductDetailResponse;
import com.example.ecommerce.product.dto.ProductResponse;
import com.example.ecommerce.product.dto.SkuResponse;

@Service
public class ProductService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;

    public ProductService(ProductRepository productRepository,ProductSkuRepository productSkuRepository) {
        this.productRepository = productRepository;
        this.productSkuRepository = productSkuRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProducts(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("id").descending());

        Page<Product> products = productRepository
                .findByStatusAndDeletedAtIsNull(ProductStatus.ACTIVE, pageable);
        
        List<Long> productIds = products.getContent().stream()
        .map(Product::getId)
        .toList();

        Map<Long, BigDecimal> minPrices = new HashMap<>();
        if (!productIds.isEmpty()) {
            for (ProductSkuRepository.MinPrice row : productSkuRepository.findMinPrices(productIds)) {
                minPrices.put(row.getProductId(), row.getMinPrice());
            }
        }

        return PageResponse.from(products.map(p -> ProductResponse.from(p, minPrices.get(p.getId()))));
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProduct(Long id) {
        Product product = productRepository
                .findByIdAndStatusAndDeletedAtIsNull(id, ProductStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id " + id));

        List<SkuResponse> skus = productSkuRepository.findByProductIdOrderByPriceAsc(id).stream()
                .map(SkuResponse::from)
                .toList();
        return ProductDetailResponse.from(product, skus);
    }
}