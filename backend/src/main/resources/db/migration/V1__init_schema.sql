-- =========================================================
-- DATABASE: E-Commerce Marketplace
-- MySQL 8.0+
-- Theo tài liệu mô tả CSDL ngày 23/10/2026
-- =========================================================


-- =========================================================
-- 1. USERS: Tài khoản CUSTOMER, SELLER, ADMIN
-- =========================================================

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20),
    avatar_url VARCHAR(500),
    birth_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    CONSTRAINT chk_users_role
        CHECK (role IN ('CUSTOMER', 'SELLER', 'ADMIN')),
    CONSTRAINT chk_users_status
        CHECK (status IN ('ACTIVE', 'SUSPENDED'))
) ENGINE = InnoDB;

-- =========================================================
-- 2. ADDRESSES: Địa chỉ nhận hàng của người dùng
-- =========================================================

CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipient_name VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    address_line_1 VARCHAR(255) NOT NULL,
    address_line_2 VARCHAR(255),
    ward VARCHAR(100),
    district VARCHAR(100),
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL DEFAULT 'Vietnam',
    postal_code VARCHAR(20),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_addresses_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    INDEX idx_addresses_user (user_id)
) ENGINE = InnoDB;

-- =========================================================
-- 3. SHOPS: Cửa hàng của Seller
-- =========================================================

CREATE TABLE IF NOT EXISTS shops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_user_id BIGINT NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    logo_url VARCHAR(500),
    bank_name VARCHAR(100),
    bank_account_number VARCHAR(30),
    bank_account_holder VARCHAR(150),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_shops_owner
        FOREIGN KEY (owner_user_id) REFERENCES users(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_shops_status
        CHECK (status IN ('PENDING', 'ACTIVE', 'SUSPENDED'))
) ENGINE = InnoDB;

-- =========================================================
-- 4. CATEGORIES: Danh mục chung của sàn
-- =========================================================

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_categories_parent
        FOREIGN KEY (parent_id) REFERENCES categories(id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    INDEX idx_categories_parent (parent_id)
) ENGINE = InnoDB;

-- =========================================================
-- 5. SHOP_CATEGORIES: Danh mục riêng của từng shop
-- =========================================================

CREATE TABLE IF NOT EXISTS shop_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_shop_categories_shop
        FOREIGN KEY (shop_id) REFERENCES shops(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT uq_shop_category_name
        UNIQUE (shop_id, name),

    INDEX idx_shop_categories_shop (shop_id)
) ENGINE = InnoDB;

-- =========================================================
-- 6. PRODUCTS: Thông tin chung của sản phẩm
-- =========================================================

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    category_id BIGINT NULL,
    shop_category_id BIGINT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    summary VARCHAR(500),
    cover_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    avg_rating DECIMAL(3,2) NOT NULL DEFAULT 0,
    review_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    CONSTRAINT fk_products_shop
        FOREIGN KEY (shop_id) REFERENCES shops(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    CONSTRAINT fk_products_shop_category
        FOREIGN KEY (shop_category_id) REFERENCES shop_categories(id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    CONSTRAINT chk_products_status
        CHECK (status IN ('DRAFT', 'ACTIVE', 'HIDDEN')),

    CONSTRAINT chk_products_rating
        CHECK (avg_rating BETWEEN 0 AND 5),

    CONSTRAINT chk_products_review_count
        CHECK (review_count >= 0),

    INDEX idx_products_shop_name (shop_id, name),
    INDEX idx_products_category (category_id),
    INDEX idx_products_shop_category (shop_category_id)
) ENGINE = InnoDB;

-- =========================================================
-- 7. PRODUCT_IMAGES: Nhiều ảnh cho một sản phẩm
-- =========================================================

CREATE TABLE IF NOT EXISTS product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    INDEX idx_product_images_product (product_id)
) ENGINE = InnoDB;

-- =========================================================
-- 8. PRODUCT_SKUS: Biến thể, giá và tồn kho
-- =========================================================

CREATE TABLE IF NOT EXISTS product_skus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku_code VARCHAR(100) NOT NULL UNIQUE,
    variant_name VARCHAR(150),
    color VARCHAR(50),
    size VARCHAR(50),
    price DECIMAL(12,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_skus_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_product_skus_price
        CHECK (price > 0),

    CONSTRAINT chk_product_skus_stock
        CHECK (stock_quantity >= 0),

    INDEX idx_product_skus_product (product_id)
) ENGINE = InnoDB;

-- =========================================================
-- 9. CARTS: Mỗi người dùng có một giỏ hàng
-- =========================================================

CREATE TABLE IF NOT EXISTS carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_carts_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- =========================================================
-- 10. CART_ITEMS: Các sản phẩm trong giỏ
-- =========================================================

CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    sku_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    selected BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_cart_items_cart
        FOREIGN KEY (cart_id) REFERENCES carts(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_cart_items_sku
        FOREIGN KEY (sku_id) REFERENCES product_skus(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT uq_cart_sku UNIQUE (cart_id, sku_id),

    CONSTRAINT chk_cart_items_quantity
        CHECK (quantity >= 1),

    INDEX idx_cart_items_sku (sku_id)
) ENGINE = InnoDB;

-- =========================================================
-- 11. WISHLISTS: Sản phẩm yêu thích (mở rộng)
-- =========================================================

CREATE TABLE IF NOT EXISTS wishlists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_wishlists_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_wishlists_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT uq_wishlist_user_product
        UNIQUE (user_id, product_id),

    INDEX idx_wishlists_product (product_id)
) ENGINE = InnoDB;

-- =========================================================
-- 12. VOUCHERS: Mã giảm giá toàn sàn
-- =========================================================

CREATE TABLE IF NOT EXISTS vouchers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(12,2) NOT NULL,
    max_discount_amount DECIMAL(12,2),
    min_order_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    usage_limit INT NULL,
    used_count INT NOT NULL DEFAULT 0,
    start_at TIMESTAMP NULL,
    end_at TIMESTAMP NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_vouchers_type
        CHECK (discount_type IN ('PERCENT', 'FIXED')),

    CONSTRAINT chk_vouchers_discount
        CHECK (discount_value > 0),

    CONSTRAINT chk_vouchers_min_amount
        CHECK (min_order_amount >= 0),

    CONSTRAINT chk_vouchers_usage
        CHECK (
            (usage_limit IS NULL OR usage_limit >= 0)
            AND used_count >= 0
        )
) ENGINE = InnoDB;

-- =========================================================
-- 13. ORDERS: Đơn tổng của một lần checkout
-- =========================================================

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    address_id BIGINT NULL,
    voucher_id BIGINT NULL,
    recipient_name VARCHAR(150) NOT NULL,
    recipient_phone VARCHAR(20) NOT NULL,
    shipping_address TEXT NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    shipping_fee DECIMAL(12,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL,
    note TEXT,
    idempotency_key VARCHAR(100) NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_orders_address
        FOREIGN KEY (address_id) REFERENCES addresses(id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    CONSTRAINT fk_orders_voucher
        FOREIGN KEY (voucher_id) REFERENCES vouchers(id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    CONSTRAINT chk_orders_amounts
        CHECK (
            subtotal >= 0
            AND shipping_fee >= 0
            AND discount_amount >= 0
            AND total_amount >= 0
        ),

    INDEX idx_orders_user (user_id),
    INDEX idx_orders_created_at (created_at)
) ENGINE = InnoDB;

-- =========================================================
-- 14. SELLER_ORDERS: Đơn con theo từng cửa hàng
-- =========================================================

CREATE TABLE IF NOT EXISTS seller_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    shop_id BIGINT NOT NULL,
    sub_order_code VARCHAR(50) NOT NULL UNIQUE,
    subtotal DECIMAL(12,2) NOT NULL,
    shipping_fee DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL,
    commission_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    seller_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    payout_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    paid_out_at TIMESTAMP NULL,
    payout_reference VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    cancel_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_seller_orders_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_seller_orders_shop
        FOREIGN KEY (shop_id) REFERENCES shops(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT uq_seller_orders_order_shop
        UNIQUE (order_id, shop_id),

    CONSTRAINT chk_seller_orders_payout
        CHECK (payout_status IN ('UNPAID', 'PAID')),

    CONSTRAINT chk_seller_orders_status
        CHECK (
            status IN (
                'PENDING', 'CONFIRMED', 'PREPARING',
                'SHIPPING', 'DELIVERED', 'COMPLETED',
                'CANCELLED'
            )
        ),

    CONSTRAINT chk_seller_orders_amounts
        CHECK (
            subtotal >= 0
            AND shipping_fee >= 0
            AND total_amount >= 0
            AND commission_amount >= 0
            AND seller_amount >= 0
        ),

    INDEX idx_seller_orders_order (order_id),
    INDEX idx_seller_orders_shop (shop_id)
) ENGINE = InnoDB;

-- =========================================================
-- 15. ORDER_ITEMS: Các sản phẩm trong đơn con
-- =========================================================

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_order_id BIGINT NOT NULL,
    sku_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    sku_description VARCHAR(150),
    unit_price DECIMAL(12,2) NOT NULL,
    quantity INT NOT NULL,
    line_total DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_items_seller_order
        FOREIGN KEY (seller_order_id) REFERENCES seller_orders(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_order_items_sku
        FOREIGN KEY (sku_id) REFERENCES product_skus(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_order_items_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_order_items_amounts
        CHECK (unit_price >= 0 AND line_total >= 0),

    INDEX idx_order_items_seller_order (seller_order_id),
    INDEX idx_order_items_sku (sku_id),
    INDEX idx_order_items_product (product_id)
) ENGINE = InnoDB;

-- =========================================================
-- 16. PAYMENTS: Thanh toán cho đơn tổng
-- =========================================================

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    method VARCHAR(20) NOT NULL,
    provider VARCHAR(100),
    transaction_code VARCHAR(255),
    amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_payments_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_payments_method
        CHECK (method IN ('COD', 'BANK_TRANSFER', 'ONLINE')),

    CONSTRAINT chk_payments_status
        CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),

    CONSTRAINT chk_payments_amount
        CHECK (amount >= 0),

    CONSTRAINT uq_payments_provider_transaction
        UNIQUE (provider, transaction_code),

    INDEX idx_payments_order (order_id)
) ENGINE = InnoDB;

-- =========================================================
-- 17. SHIPMENTS: Vận đơn của từng đơn con
-- =========================================================

CREATE TABLE IF NOT EXISTS shipments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_order_id BIGINT NOT NULL UNIQUE,
    carrier VARCHAR(100),
    tracking_code VARCHAR(150),
    shipping_fee DECIMAL(12,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    shipped_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_shipments_seller_order
        FOREIGN KEY (seller_order_id) REFERENCES seller_orders(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_shipments_status
        CHECK (status IN ('CREATED', 'IN_TRANSIT', 'DELIVERED', 'FAILED')),

    CONSTRAINT chk_shipments_fee
        CHECK (shipping_fee >= 0)
) ENGINE = InnoDB;

-- =========================================================
-- 18. REVIEWS: Đánh giá sản phẩm sau khi mua
-- =========================================================

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    order_item_id BIGINT NOT NULL UNIQUE,
    rating INT NOT NULL,
    comment TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'VISIBLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_reviews_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_reviews_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_reviews_order_item
        FOREIGN KEY (order_item_id) REFERENCES order_items(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_reviews_rating
        CHECK (rating BETWEEN 1 AND 5),

    CONSTRAINT chk_reviews_status
        CHECK (status IN ('VISIBLE', 'HIDDEN')),

    INDEX idx_reviews_product (product_id),
    INDEX idx_reviews_user (user_id)
) ENGINE = InnoDB;
