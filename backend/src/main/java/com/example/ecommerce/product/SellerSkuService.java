package com.example.ecommerce.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.common.exception.ConflictException;
import com.example.ecommerce.common.exception.ResourceNotFoundException;
import com.example.ecommerce.product.dto.SellerSkuResponse;
import com.example.ecommerce.product.dto.SkuRequest;

@Service
public class SellerSkuService {

    private final ProductSkuRepository skuRepository;
    private final SellerProductService sellerProductService;

    public SellerSkuService(ProductSkuRepository skuRepository,
                            SellerProductService sellerProductService) {
        this.skuRepository = skuRepository;
        this.sellerProductService = sellerProductService;
    }

    @Transactional(readOnly = true)
    public List<SellerSkuResponse> list(Long userId, Long productId) {
        sellerProductService.findOwned(userId, productId);
        return skuRepository.findByProductIdOrderByPriceAsc(productId)
                .stream()
                .map(SellerSkuResponse::from)
                .toList();
    }

    @Transactional
    public SellerSkuResponse create(Long userId, Long productId, SkuRequest request) {
        sellerProductService.findOwned(userId, productId);
        String code = request.skuCode().trim();
        if (skuRepository.existsBySkuCode(code)) {
            throw new ConflictException("Mã SKU đã tồn tại");
        }
        ProductSku sku = new ProductSku(
                productId,
                code,
                request.variantName(),
                request.color(),
                request.size(),
                request.price(),
                request.stockQuantity());
        return SellerSkuResponse.from(skuRepository.save(sku));
    }

    @Transactional
    public SellerSkuResponse update(Long userId, Long productId, Long skuId, SkuRequest request) {
        sellerProductService.findOwned(userId, productId);
        ProductSku sku = findSku(skuId, productId);
        String code = request.skuCode().trim();
        if (skuRepository.existsBySkuCodeAndIdNot(code, skuId)) {
            throw new ConflictException("Mã SKU đã tồn tại");
        }
        sku.update(
                code,
                request.variantName(),
                request.color(),
                request.size(),
                request.price(),
                request.stockQuantity());
        return SellerSkuResponse.from(sku);
    }

    @Transactional
    public void delete(Long userId, Long productId, Long skuId) {
        Product product = sellerProductService.findOwned(userId, productId);
        ProductSku sku = findSku(skuId, productId);
        if (product.getStatus() == ProductStatus.ACTIVE
                && skuRepository.countByProductId(productId) <= 1) {
            throw new ConflictException(
                    "Không thể xóa biến thể cuối cùng của sản phẩm đang bán");
        }
        skuRepository.delete(sku);
    }

    private ProductSku findSku(Long skuId, Long productId) {
        return skuRepository.findByIdAndProductId(skuId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể"));
    }
}