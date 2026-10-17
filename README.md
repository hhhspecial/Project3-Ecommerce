# E-Commerce Platform

Nền tảng thương mại điện tử kết nối người mua (Customer) và người bán (Seller), do quản trị viên (Admin) giám sát. Đồ án cá nhân.

**Công nghệ:** Java 21, Spring Boot 4.1.1, Spring Data JPA, MySQL, Maven. Frontend dùng React (làm sau).

## Cấu trúc thư mục

```
ecommerce-project/
├── README.md
├── backend/      ứng dụng Spring Boot (REST API)
└── frontend/     ứng dụng React (giao diện người dùng)
```

- `backend/`: xử lý nghiệp vụ, kết nối MySQL, cung cấp REST API dạng JSON.
- `frontend/`: giao diện web, gọi API của backend. Hai phần chạy độc lập, chỉ giao tiếp qua API.

## Cấu trúc backend

Mã nguồn nằm ở `backend/src/main/java/com/example/ecommerce/`, chia theo module (mỗi module là một nhóm chức năng):

```
ecommerce/
├── EcommerceApplication.java   điểm khởi động ứng dụng
├── common/                     thành phần dùng chung
│   ├── config/                 cấu hình chung (CORS, Swagger...)
│   ├── exception/              các exception và bộ xử lý lỗi chung
│   └── response/               định dạng JSON trả về chung cho API
├── auth/                       đăng ký, đăng nhập, JWT, phân quyền
├── user/                       tài khoản, hồ sơ cá nhân, địa chỉ nhận hàng
├── store/                      cửa hàng của Seller
├── product/                    danh mục, sản phẩm, biến thể, tìm kiếm và lọc
├── cart/                       giỏ hàng của Customer
├── order/                      đặt hàng, đơn hàng, tồn kho, mã giảm giá
├── payment/                    thanh toán
├── shipping/                   vận chuyển, theo dõi giao hàng
└── review/                     đánh giá sản phẩm
```

Bên trong mỗi module có các file theo 3 tầng:

| Thành phần | Vai trò |
| --- | --- |
| `Controller` | Nhận request từ client, kiểm tra dữ liệu đầu vào, trả response |
| `Service` | Xử lý nghiệp vụ, transaction, kiểm tra quyền sở hữu dữ liệu |
| `Repository` | Truy vấn database (Spring Data JPA) |
| `Entity` | Lớp ánh xạ với bảng trong database |
| `dto/` | Các lớp dữ liệu dùng để nhận và trả qua API (chỉ tạo khi cần) |

## Quy tắc

- Controller chỉ gọi Service của module mình, không viết nghiệp vụ trong Controller.
- Module A cần dữ liệu của module B thì gọi Service của B, không gọi thẳng Repository của B.
- Không để hai module gọi vòng tròn lẫn nhau.

## Cấu hình và chạy backend

Chưa viết, sẽ bổ sung sau khi cấu hình xong kết nối MySQL.