# Frontend (React + Vite)

## Chạy

Cần Node.js 18 trở lên.

```bash
npm install
npm run dev
```

Mở http://localhost:5173. **Backend phải đang chạy ở cổng 8080.**

Vite chuyển tiếp mọi request `/api/*` và `/uploads/*` sang `http://localhost:8080`
(xem `vite.config.js`), nên không cần cấu hình CORS ở backend khi phát triển.
Nếu backend chạy cổng khác, sửa hai dòng `proxy` trong `vite.config.js`.

## Cấu trúc

```
src/
  main.jsx            điểm vào, bọc BrowserRouter
  App.jsx             khung trang (header, footer) và các route
  api.js              gọi backend, tự bóc { success, message, data }
  format.js           định dạng giá tiền, điểm đánh giá, tên biến thể
  styles.css          toàn bộ giao diện
  components/
    ProductCard.jsx   thẻ sản phẩm trong danh sách
    Pagination.jsx    phân trang
    SafeImage.jsx     ảnh có khung thay thế khi trống hoặc lỗi tải
  pages/
    ProductListPage.jsx    danh sách: tìm kiếm, danh mục, sắp xếp, phân trang
    ProductDetailPage.jsx  chi tiết: thư viện ảnh, chọn biến thể, giá, tồn kho
```

## Các route

| Đường dẫn | Trang |
|---|---|
| `/` | Danh sách sản phẩm. Bộ lọc nằm trên URL: `?keyword=&categoryId=&sort=&page=` |
| `/products/:id` | Chi tiết sản phẩm |

## Ghi chú

- Dữ liệu seed có link ảnh giả (`example.com`) nên sẽ hiện khung "Chưa có ảnh".
  Sản phẩm bạn tạo ở trang Seller và upload ảnh thật sẽ hiện đúng.
- Nút "Thêm vào giỏ" đang bị khóa, sẽ làm cùng giỏ hàng ở tuần 6.
