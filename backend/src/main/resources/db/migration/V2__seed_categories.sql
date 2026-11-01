INSERT INTO categories (id, parent_id, name, description, created_at, updated_at) VALUES
(1, NULL, 'Thời trang', 'Quần áo, giày dép, phụ kiện', NOW(), NOW()),
(2, NULL, 'Điện tử', 'Điện thoại, laptop, phụ kiện', NOW(), NOW()),
(3, NULL, 'Nhà cửa', 'Đồ gia dụng, nội thất', NOW(), NOW()),
(4, 1, 'Áo thun', NULL, NOW(), NOW()),
(5, 1, 'Giày', NULL, NOW(), NOW()),
(6, 2, 'Điện thoại', NULL, NOW(), NOW());