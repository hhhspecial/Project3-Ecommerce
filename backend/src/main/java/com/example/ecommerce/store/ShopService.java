package com.example.ecommerce.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.common.exception.ConflictException;
import com.example.ecommerce.common.exception.ResourceNotFoundException;
import com.example.ecommerce.common.PageResponse;
import com.example.ecommerce.store.dto.ShopRequest;
import com.example.ecommerce.store.dto.ShopResponse;
import com.example.ecommerce.user.UserService;

@Service
public class ShopService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ShopRepository shopRepository;
    private final UserService userService;

    public ShopService(ShopRepository shopRepository, UserService userService) {
        this.shopRepository = shopRepository;
        this.userService = userService;
    }

    // ---------- Phía người dùng ----------

    @Transactional
    public ShopResponse register(Long userId, ShopRequest request) {
        if (shopRepository.existsByOwnerUserId(userId)) {
            throw new ConflictException("Bạn đã đăng ký cửa hàng rồi");
        }
        String name = request.name().trim();
        if (shopRepository.existsByName(name)) {
            throw new ConflictException("Tên cửa hàng đã được sử dụng");
        }
        Shop shop = new Shop(userId, name, request.description());
        return ShopResponse.from(shopRepository.save(shop));
    }

    @Transactional(readOnly = true)
    public ShopResponse getMyShop(Long userId) {
        return ShopResponse.from(findByOwner(userId));
    }

    @Transactional
    public ShopResponse updateMyShop(Long userId, ShopRequest request) {
        Shop shop = findByOwner(userId);
        if (shop.getStatus() == ShopStatus.SUSPENDED) {
            throw new ConflictException("Cửa hàng đang bị khóa, không thể chỉnh sửa");
        }
        String name = request.name().trim();
        if (shopRepository.existsByNameAndIdNot(name, shop.getId())) {
            throw new ConflictException("Tên cửa hàng đã được sử dụng");
        }
        shop.update(name, request.description());
        return ShopResponse.from(shop);
    }

    // ---------- Phía Admin ----------

    @Transactional(readOnly = true)
    public PageResponse<ShopResponse> listForAdmin(ShopStatus status, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("createdAt").descending());

        Page<Shop> result = (status == null)
                ? shopRepository.findAll(pageable)
                : shopRepository.findByStatus(status, pageable);
        return PageResponse.from(result.map(ShopResponse::from));
    }

    @Transactional
    public ShopResponse approve(Long shopId) {
        Shop shop = findById(shopId);
        if (shop.getStatus() != ShopStatus.PENDING) {
            throw new ConflictException("Chỉ duyệt được cửa hàng đang chờ duyệt");
        }
        shop.approve();
        userService.promoteToSeller(shop.getOwnerUserId());
        return ShopResponse.from(shop);
    }

    @Transactional
    public void reject(Long shopId) {
        Shop shop = findById(shopId);
        if (shop.getStatus() != ShopStatus.PENDING) {
            throw new ConflictException("Chỉ từ chối được cửa hàng đang chờ duyệt");
        }
        shopRepository.delete(shop);
    }

    @Transactional(readOnly = true)
    public Shop requireActiveShop(Long ownerUserId) {
        Shop shop = findByOwner(ownerUserId);
        if (shop.getStatus() != ShopStatus.ACTIVE) {
            throw new ConflictException("Cửa hàng chưa hoạt động (đang chờ duyệt hoặc bị khóa)");
        }
        return shop;
    }
    // ---------- Hàm phụ ----------

    private Shop findById(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cửa hàng"));
    }

    private Shop findByOwner(Long userId) {
        return shopRepository.findByOwnerUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bạn chưa có cửa hàng"));
    }
}
