package com.example.ecommerce.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.common.exception.ConflictException;
import com.example.ecommerce.common.exception.ResourceNotFoundException;
import com.example.ecommerce.common.storage.FileStorageService;
import com.example.ecommerce.product.dto.SellerImageResponse;

@Service
public class SellerImageService {

    private static final int MAX_IMAGES = 8;

    private final ProductImageRepository imageRepository;
    private final SellerProductService sellerProductService;
    private final FileStorageService fileStorageService;

    public SellerImageService(ProductImageRepository imageRepository,
                              SellerProductService sellerProductService,
                              FileStorageService fileStorageService) {
        this.imageRepository = imageRepository;
        this.sellerProductService = sellerProductService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<SellerImageResponse> list(Long userId, Long productId) {
        sellerProductService.findOwned(userId, productId);
        return imageRepository.findByProductIdOrderBySortOrderAsc(productId)
                .stream()
                .map(SellerImageResponse::from)
                .toList();
    }

    @Transactional
    public SellerImageResponse upload(Long userId, Long productId, MultipartFile file) {
        Product product = sellerProductService.findOwned(userId, productId);
        long count = imageRepository.countByProductId(productId);
        if (count >= MAX_IMAGES) {
            throw new ConflictException("Mỗi sản phẩm tối đa " + MAX_IMAGES + " ảnh");
        }

        String url = fileStorageService.storeImage(file, "products");
        ProductImage image = imageRepository.save(new ProductImage(product, url, (int) count));

        if (product.getCoverUrl() == null) {
            product.changeCover(url);
        }
        return SellerImageResponse.from(image);
    }

    @Transactional
    public SellerImageResponse setCover(Long userId, Long productId, Long imageId) {
        Product product = sellerProductService.findOwned(userId, productId);
        ProductImage image = findImage(imageId, productId);
        product.changeCover(image.getImageUrl());
        return SellerImageResponse.from(image);
    }

    @Transactional
    public void delete(Long userId, Long productId, Long imageId) {
        Product product = sellerProductService.findOwned(userId, productId);
        ProductImage image = findImage(imageId, productId);
        String url = image.getImageUrl();
        imageRepository.delete(image);

        // Xóa đúng ảnh đại diện thì chọn ảnh còn lại đầu tiên làm ảnh đại diện mới
        if (url.equals(product.getCoverUrl())) {
            List<ProductImage> rest = imageRepository.findByProductIdOrderBySortOrderAsc(productId);
            product.changeCover(rest.isEmpty() ? null : rest.get(0).getImageUrl());
        }
        fileStorageService.deleteByUrl(url);
    }

    private ProductImage findImage(Long imageId, Long productId) {
        return imageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ảnh"));
    }
}
