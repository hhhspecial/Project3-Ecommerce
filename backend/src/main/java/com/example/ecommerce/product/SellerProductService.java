package com.example.ecommerce.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.common.exception.ConflictException;
import com.example.ecommerce.common.exception.ResourceNotFoundException;
import com.example.ecommerce.common.PageResponse;
import com.example.ecommerce.product.dto.ProductStatusRequest;
import com.example.ecommerce.product.dto.SellerProductRequest;
import com.example.ecommerce.product.dto.SellerProductResponse;
import com.example.ecommerce.store.Shop;
import com.example.ecommerce.store.ShopService;

@Service
public class SellerProductService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;
    private final CategoryRepository categoryRepository;
    private final ShopService shopService;

    public SellerProductService(ProductRepository productRepository,
                                ProductSkuRepository productSkuRepository,
                                CategoryRepository categoryRepository,
                                ShopService shopService) {
        this.productRepository = productRepository;
        this.productSkuRepository = productSkuRepository;
        this.categoryRepository = categoryRepository;
        this.shopService = shopService;
    }

    @Transactional
    public SellerProductResponse create(Long userId, SellerProductRequest request) {
        Shop shop = shopService.requireActiveShop(userId);
        Category category = findCategory(request.categoryId());

        Product product = new Product(
                shop,
                category,
                request.name().trim(),
                request.summary(),
                request.description());
        return SellerProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public PageResponse<SellerProductResponse> list(Long userId, ProductStatus status,
                                                    int page, int size) {
        Shop shop = shopService.requireActiveShop(userId);
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("id").descending());

        Page<Product> result = (status == null)
                ? productRepository.findByShopIdAndDeletedAtIsNull(shop.getId(), pageable)
                : productRepository.findByShopIdAndStatusAndDeletedAtIsNull(shop.getId(), status, pageable);
        return PageResponse.from(result.map(SellerProductResponse::from));
    }

    @Transactional(readOnly = true)
    public SellerProductResponse get(Long userId, Long productId) {
        return SellerProductResponse.from(findOwned(userId, productId));
    }

    @Transactional
    public SellerProductResponse update(Long userId, Long productId, SellerProductRequest request) {
        Product product = findOwned(userId, productId);
        Category category = findCategory(request.categoryId());
        product.update(request.name().trim(), request.summary(), request.description(), category);
        return SellerProductResponse.from(product);
    }

    @Transactional
    public SellerProductResponse changeStatus(Long userId, Long productId,
                                              ProductStatusRequest request) {
        Product product = findOwned(userId, productId);
        if (request.status() == ProductStatus.ACTIVE
                && !productSkuRepository.existsByProductId(productId)) {
            throw new ConflictException(
                    "Sản phẩm cần có ít nhất một biến thể (giá, tồn kho) trước khi mở bán");
        }
        product.changeStatus(request.status());
        return SellerProductResponse.from(product);
    }

    @Transactional
    public void delete(Long userId, Long productId) {
        Product product = findOwned(userId, productId);
        product.markDeleted();
    }

    // Lấy sản phẩm thuộc shop của người đang đăng nhập, không phải thì 404
    Product findOwned(Long userId, Long productId) {
        Shop shop = shopService.requireActiveShop(userId);
        return productRepository.findByIdAndShopIdAndDeletedAtIsNull(productId, shop.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
    }
}
