INSERT INTO users (id, username, email, password_hash, role, first_name, last_name, status, created_at, updated_at) VALUES
(1, 'admin',   'admin@shop.local',   'placeholder', 'ADMIN',  'Admin', 'System', 'ACTIVE', NOW(), NOW()),
(2, 'seller1', 'seller1@shop.local', 'placeholder', 'SELLER', 'An',    'Nguyen', 'ACTIVE', NOW(), NOW()),
(3, 'seller2', 'seller2@shop.local', 'placeholder', 'SELLER', 'Binh',  'Tran',   'ACTIVE', NOW(), NOW());

INSERT INTO shops (id, owner_user_id, name, description, status, created_at, updated_at) VALUES
(1, 2, 'Shop Thời Trang An',  'Quần áo, giày dép',     'ACTIVE', NOW(), NOW()),
(2, 3, 'Shop Điện Tử Bình',   'Điện thoại, phụ kiện',  'ACTIVE', NOW(), NOW());

INSERT INTO products (id, shop_id, category_id, name, summary, status, avg_rating, review_count, created_at, updated_at) VALUES
(1, 1, 4, 'Áo thun cotton basic',  'Áo thun 100% cotton',        'ACTIVE', 0, 0, NOW(), NOW()),
(2, 1, 4, 'Áo thun oversize',      'Form rộng, nhiều màu',       'ACTIVE', 0, 0, NOW(), NOW()),
(3, 1, 5, 'Giày sneaker trắng',    'Giày thể thao dễ phối đồ',   'ACTIVE', 0, 0, NOW(), NOW()),
(4, 2, 6, 'Điện thoại X1',         'Màn hình 6.5 inch',          'ACTIVE', 0, 0, NOW(), NOW()),
(5, 2, 6, 'Điện thoại X2 Pro',     'Camera 108MP',               'ACTIVE', 0, 0, NOW(), NOW()),
(6, 2, 6, 'Điện thoại cũ (nháp)',  'Chưa mở bán',                'DRAFT',  0, 0, NOW(), NOW());