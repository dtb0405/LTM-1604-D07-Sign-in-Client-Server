<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
   Hệ thống đăng nhập Client-Server
</h2>
<div align="center">
    <p align="center">
        <img src="docs/aiotlab_logo.png" alt="AIoTLab Logo" width="170"/>
        <img src="docs/fitdnu_logo.png" alt="AIoTLab Logo" width="180"/>
        <img src="docs/dnu_logo.png" alt="DaiNam University Logo" width="200"/>
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)

</div>

## 📖 1. Giới thiệu hệ thống
Hệ thống đăng nhập Client-Server được xây dựng dựa trên mô hình giao tiếp TCP Socket giữa máy khách (Client) và máy chủ (Server). Trong hệ thống này, Server sẽ nhận và xử lý các yêu cầu từ phía Client. Người dùng phía Client sẽ thực hiện thao tác đăng nhập bằng cách nhập tên tài khoản và mật khẩu, sau đó thông tin này sẽ được gửi tới Server thông qua kết nối TCP.
Phía Server có giao diện quản lý tài khoản trực quan, cho phép Thêm, Sửa, Xoá dữ liệu người dùng. Dữ liệu này được lưu trữ và quản lý trong cơ sở dữ liệu MySQL, được kết nối bằng JDBC (Java Database Connectivity), đảm bảo tính an toàn và toàn vẹn dữ liệu.

## 🔧 2. Công nghệ sử dụng   
<div align="center">

[![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/) 
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/) 
[![JDBC](https://img.shields.io/badge/JDBC-FF6F00?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/)

</div>

Trong đó:  
- **Java**: ngôn ngữ chính để xây dựng Client và Server theo mô hình TCP Socket.  
- **MySQL**: hệ quản trị cơ sở dữ liệu lưu trữ thông tin tài khoản người dùng.  
- **JDBC**: cầu nối giữa ứng dụng Java và cơ sở dữ liệu MySQL.  

## 🖼️ 3. Một số hình ảnh hệ thống  


<div align="center">

<table>
  <tr>
    <td align="center">
      <img width="400" src="docs/dang_nhap.png" /><br/>
      <b>Giao diện đăng nhập</b>
    </td>
    <td align="center">
      <img width="400" src="docs/dang_ky.png" /><br/>
      <b>Giao diện đăng ký</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="400" src="docs/user_main.png" /><br/>
      <b>Giao diện User (màn hình chính)</b>
    </td>
    <td align="center">
      <img width="400" src="docs/user_ho_so.png" /><br/>
      <b>Thông tin hồ sơ</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="400" src="docs/user_cap_nhat.png" /><br/>
      <b>Sửa thông tin User</b>
    </td>
    <td align="center">
      <img width="400" src="docs/user_doi_mat_khau.png" /><br/>
      <b>Đổi mật khẩu</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="400" src="docs/user_avt.png" /><br/>
      <b>Cập nhật ảnh đại diện</b>
    </td>
  </tr>
</table>

</div>

<div align="center">

<table>
  <tr>
    <td align="center">
      <img width="500" src="docs/admin_dashboard.png" /><br/>
      <b>Giao diện Admin - Dashboard hệ thống</b>
    </td>
    <td align="center">
      <img width="500" src="docs/server_main.png" /><br/>
      <b>Màn hình chính của Server</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="500" src="docs/admin_avt.png" /><br/>
      <b>Admin - Cập nhật ảnh đại diện</b>
    </td>
    <td align="center">
      <img width="500" src="docs/admin_bieu_do.png" /><br/>
      <b>Biểu đồ thống kê tài khoản</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="500" src="docs/admin_quan_ly.png" /><br/>
      <b>Giao diện Admin - Quản lý tài khoản</b>
    </td>
    <td align="center">
      <img width="400" src="docs/admin_them_acc.png" /><br/>
      <b>Thêm tài khoản mới</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="400" src="docs/admin_sua_thong_tin.png" /><br/>
      <b>Cập nhật thông tin tài khoản</b>
    </td>
    <td align="center">
      <img width="300" src="docs/admin_khoa_acc.png" /><br/>
      <b>Xác nhận khóa tài khoản</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="500" src="docs/admin_mo_acc.png" /><br/>
      <b>Xác nhận mở tài khoản</b>
    </td>
    <td align="center">
      <img width="300" src="docs/admin_delete.png" /><br/>
      <b>Xác nhận xóa tài khoản</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="500" src="docs/admin_lich_su.png" /><br/>
      <b>Giao diện Admin - Lịch sử đăng nhập</b>
    </td>
    <td align="center">
      <img width="300" src="docs/admin_xuat_lich_su.png" /><br/>
      <b>Xuất lịch sử đăng nhập</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="500" src="docs/bao_cao_txt.png" /><br/>
      <b>Báo cáo lịch sử đăng nhập đươc xuất ra file .txt</b>
    </td>
  </tr>
</table>

</div>

## 🛠️ 4. Các bước cài đặt
### 4.1. Cài đặt môi trường
- Cài đặt **JDK 8+**: [Download Java](https://www.oracle.com/java/technologies/javase-downloads.html)  
- Cài đặt **MySQL Server**: [Download MySQL](https://dev.mysql.com/downloads/)  
- Cài đặt **Git** (nếu chưa có): [Download Git](https://git-scm.com/downloads)  
- IDE khuyến nghị: **IntelliJ IDEA** hoặc **Eclipse**  
### 4.2. Clone source code
Mở terminal/cmd và chạy lệnh:  
```bash
git clone https://github.com/dtb0405/LTM-1604-D07-Sign-in-Client-Server.git
cd LTM-1604-D07-Sign-in-Client-Server
```
### 4.3. Khởi tạo cơ sở dữ liệu MySQL
Mở **MySQL Workbench** và chạy lệnh:
```sql
CREATE DATABASE IF NOT EXISTS he_thong_dang_nhap;
USE he_thong_dang_nhap;

-- =====================================================
-- BẢNG TÀI KHOẢN NGƯỜI DÙNG
-- =====================================================
CREATE TABLE tai_khoan (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ten_dang_nhap VARCHAR(50) UNIQUE NOT NULL,
    mat_khau VARCHAR(255) NOT NULL,
    ho_ten VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    so_dien_thoai VARCHAR(15),
    ngay_sinh DATE,
    vai_tro ENUM('user', 'admin') DEFAULT 'user',
    trang_thai ENUM('hoat_dong', 'bi_khoa') DEFAULT 'hoat_dong',
    trang_thai_online ENUM('online', 'offline') DEFAULT 'offline',
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lan_dang_nhap_cuoi TIMESTAMP NULL,
    -- Các cột mới cho tính năng ảnh đại diện
    avatar_path VARCHAR(500) NULL,
    avatar_data LONGBLOB NULL
);

-- =====================================================
-- BẢNG LỊCH SỬ ĐĂNG NHẬP
-- =====================================================
CREATE TABLE lich_su_dang_nhap (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tai_khoan_id INT,
    ten_dang_nhap VARCHAR(50),
    thoi_gian_dang_nhap TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    thoi_gian_dang_xuat TIMESTAMP NULL,
    dia_chi_ip VARCHAR(45),
    trang_thai ENUM('thanh_cong', 'that_bai') DEFAULT 'thanh_cong',
    ghi_chu TEXT,
    FOREIGN KEY (tai_khoan_id) REFERENCES tai_khoan(id) ON DELETE CASCADE
);

-- =====================================================
-- DỮ LIỆU MẪU
-- =====================================================

-- Tạo tài khoản admin mặc định
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, vai_tro, avatar_path) 
VALUES ('binh', 'binhbinh', 'Đặng Thanh Bình', 'admin', 'default_avatar.png');

-- Tạo một số tài khoản user mẫu
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, avatar_path) 
VALUES 
('user1', 'user123', 'Nguyen Van A', 'nguyenvana@email.com', 'user', 'default_avatar.png'),
('user2', 'user123', 'Tran Thi B', 'tranthib@email.com', 'user', 'default_avatar.png'),
('admin1', 'admin123', 'Admin User', 'admin@system.com', 'admin', 'default_avatar.png'),
('testuser', 'test123', 'Test User', 'test@example.com', 'user', 'default_avatar.png');


-- Index cho tìm kiếm theo tên đăng nhập
CREATE INDEX idx_ten_dang_nhap ON tai_khoan(ten_dang_nhap);

-- Index cho tìm kiếm theo email
CREATE INDEX idx_email ON tai_khoan(email);

-- Index cho lọc theo vai trò
CREATE INDEX idx_vai_tro ON tai_khoan(vai_tro);

-- Index cho lọc theo trạng thái
CREATE INDEX idx_trang_thai ON tai_khoan(trang_thai);

-- Index cho lọc theo trạng thái online
CREATE INDEX idx_trang_thai_online ON tai_khoan(trang_thai_online);

-- Index cho lịch sử đăng nhập theo tài khoản
CREATE INDEX idx_lich_su_tai_khoan ON lich_su_dang_nhap(tai_khoan_id);

-- Index cho lịch sử đăng nhập theo thời gian
CREATE INDEX idx_lich_su_thoi_gian ON lich_su_dang_nhap(thoi_gian_dang_nhap);

-- Index cho lịch sử đăng nhập theo trạng thái
CREATE INDEX idx_lich_su_trang_thai ON lich_su_dang_nhap(trang_thai);

-- =====================================================
-- VIEWS ĐỂ BÁO CÁO
-- =====================================================

-- View thống kê tổng quan
CREATE VIEW vw_thong_ke_tong_quan AS
SELECT 
    COUNT(*) as tong_tai_khoan,
    SUM(CASE WHEN vai_tro = 'admin' THEN 1 ELSE 0 END) as so_admin,
    SUM(CASE WHEN vai_tro = 'user' THEN 1 ELSE 0 END) as so_user,
    SUM(CASE WHEN trang_thai = 'hoat_dong' THEN 1 ELSE 0 END) as tai_khoan_hoat_dong,
    SUM(CASE WHEN trang_thai = 'bi_khoa' THEN 1 ELSE 0 END) as tai_khoan_bi_khoa,
    SUM(CASE WHEN trang_thai_online = 'online' THEN 1 ELSE 0 END) as tai_khoan_online,
    SUM(CASE WHEN trang_thai_online = 'offline' THEN 1 ELSE 0 END) as tai_khoan_offline
FROM tai_khoan;

-- View thống kê đăng nhập theo ngày
CREATE VIEW vw_thong_ke_dang_nhap_ngay AS
SELECT 
    DATE(thoi_gian_dang_nhap) as ngay,
    COUNT(*) as tong_luot_dang_nhap,
    SUM(CASE WHEN trang_thai = 'thanh_cong' THEN 1 ELSE 0 END) as dang_nhap_thanh_cong,
    SUM(CASE WHEN trang_thai = 'that_bai' THEN 1 ELSE 0 END) as dang_nhap_that_bai
FROM lich_su_dang_nhap
GROUP BY DATE(thoi_gian_dang_nhap)
ORDER BY ngay DESC;

-- View thống kê đăng nhập theo tài khoản
CREATE VIEW vw_thong_ke_dang_nhap_tai_khoan AS
SELECT 
    tk.id,
    tk.ten_dang_nhap,
    tk.ho_ten,
    tk.vai_tro,
    COUNT(ls.id) as tong_luot_dang_nhap,
    SUM(CASE WHEN ls.trang_thai = 'thanh_cong' THEN 1 ELSE 0 END) as dang_nhap_thanh_cong,
    SUM(CASE WHEN ls.trang_thai = 'that_bai' THEN 1 ELSE 0 END) as dang_nhap_that_bai,
    MAX(ls.thoi_gian_dang_nhap) as lan_dang_nhap_cuoi
FROM tai_khoan tk
LEFT JOIN lich_su_dang_nhap ls ON tk.id = ls.tai_khoan_id
GROUP BY tk.id, tk.ten_dang_nhap, tk.ho_ten, tk.vai_tro;

-- =====================================================
-- STORED PROCEDURES
-- =====================================================

-- Procedure để cập nhật trạng thái online/offline
DELIMITER //
CREATE PROCEDURE sp_cap_nhat_trang_thai_online(
    IN p_tai_khoan_id INT,
    IN p_trang_thai_online ENUM('online', 'offline')
)
BEGIN
    UPDATE tai_khoan 
    SET trang_thai_online = p_trang_thai_online,
        lan_dang_nhap_cuoi = CASE 
            WHEN p_trang_thai_online = 'online' THEN NOW() 
            ELSE lan_dang_nhap_cuoi 
        END
    WHERE id = p_tai_khoan_id;
END //
DELIMITER ;

-- Procedure để lấy thống kê đăng nhập trong ngày
DELIMITER //
CREATE PROCEDURE sp_thong_ke_dang_nhap_ngay(IN p_ngay DATE)
BEGIN
    SELECT 
        COUNT(*) as tong_luot,
        SUM(CASE WHEN trang_thai = 'thanh_cong' THEN 1 ELSE 0 END) as thanh_cong,
        SUM(CASE WHEN trang_thai = 'that_bai' THEN 1 ELSE 0 END) as that_bai
    FROM lich_su_dang_nhap
    WHERE DATE(thoi_gian_dang_nhap) = p_ngay;
END //
DELIMITER ;

-- =====================================================
-- TRIGGERS
-- =====================================================

-- Trigger để tự động cập nhật thời gian đăng nhập cuối
DELIMITER //
CREATE TRIGGER tr_cap_nhat_dang_nhap_cuoi
AFTER INSERT ON lich_su_dang_nhap
FOR EACH ROW
BEGIN
    IF NEW.trang_thai = 'thanh_cong' THEN
        UPDATE tai_khoan 
        SET lan_dang_nhap_cuoi = NEW.thoi_gian_dang_nhap
        WHERE id = NEW.tai_khoan_id;
    END IF;
END //
DELIMITER ;

-- =====================================================
-- QUYỀN TRUY CẬP
-- =====================================================

-- Tạo user cho ứng dụng
CREATE USER IF NOT EXISTS 'app_user'@'localhost' IDENTIFIED BY 'app_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON he_thong_dang_nhap.* TO 'app_user'@'localhost';

-- Tạo user cho báo cáo (chỉ đọc)
CREATE USER IF NOT EXISTS 'report_user'@'localhost' IDENTIFIED BY 'report_password';
GRANT SELECT ON he_thong_dang_nhap.* TO 'report_user'@'localhost';
```

### 4.4. Cấu Hình Kết Nối JDBC
1. Mở file `DBConnection.java` trong thư mục `Server`.
2. Cập nhật thông tin kết nối cơ sở dữ liệu MySQL như sau:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/LoginDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
   private static final String TEN_NGUOI_DUNG = "root";  // thay bằng user MySQL
   private static final String MAT_KHAU = "your_password";   
   ```
   - **URL**: Địa chỉ kết nối đến cơ sở dữ liệu MySQL (thay `LoginDB` nếu tên database khác).
   - **TEN_NGUOI_DUNG**: Tên người dùng MySQL (mặc định là `root`).
   - **MAT_KHAU**: Mật khẩu MySQL (thay `your_password` bằng mật khẩu thực tế của bạn).

### 4.5. Chạy Chương Trình
#### Chạy Server
1. Mở lớp `GiaoDienServer.java` trong thư mục `gui`.
2. Chạy chương trình (`Run`).
3. Bật Server, Server sẽ khởi động và lắng nghe kết nối trên **port 2712**.

#### Chạy Client
1. Mở lớp `ManHinhDangNhap.java` trong thư mục `gui`.
2. Chạy chương trình (`Run`).
3. Giao diện người dùng sẽ hiển thị, cho phép:
   - **Đăng nhập**: Nhập tài khoản và mật khẩu để đăng nhập.
   - **Đăng ký**: Nhập thông tin để tạo tài khoản mới.

### 4.6. Kiểm Tra Kết Quả
#### **Đăng nhập thành công:**
- **Server log:** Hiển thị thông tin kết nối chi tiết
- **Giao diện Server:** Hiển thị trạng thái "Đang chạy" và log real-time
- **Giao diện Client:** Chuyển đến giao diện tương ứng (User/Admin)

#### **Đăng ký tài khoản:**
- **Database:** Dữ liệu được lưu trực tiếp vào MySQL
  ```sql
  INSERT INTO tai_khoan (ho_ten, ten_dang_nhap, mat_khau, vai_tro, trang_thai, ngay_tao)
  VALUES ('Nguyễn Văn A', 'nguyenvana', 'hashed_password', 'user', 'hoat_dong', NOW());
  ```
- **Validation:** Kiểm tra tên đăng nhập trùng lặp
- **Mật khẩu:** Mã hóa an toàn trước khi lưu database

#### **Giao diện Server - Quản lý tài khoản:**

**🔹 Thêm người dùng:**
- **Hộp thoại:** Màu xanh dương với hoa văn chìm
- **Thông tin:** Họ tên, tên đăng nhập, mật khẩu, vai trò, trạng thái
- **Validation:** Kiểm tra tên đăng nhập trùng lặp, độ dài mật khẩu
- **Kết quả:** Tài khoản được tạo và hiển thị trong bảng quản lý

**🔹 Sửa thông tin người dùng:**
- **Hiển thị:** Thông tin hiện tại của tài khoản được chọn
- **Chỉnh sửa:** Có thể thay đổi họ tên, email, số điện thoại, ngày sinh
- **Mật khẩu:** Hiển thị mật khẩu hiện tại, cho phép đổi mật khẩu mới
- **Lưu:** Cập nhật thông tin vào database

**🔹 Xóa người dùng:**
- **Xác nhận:** Hộp thoại xác nhận trước khi xóa
- **Thông tin:** Hiển thị tên tài khoản sẽ bị xóa
- **Kết quả:** Tài khoản bị xóa khỏi database và bảng quản lý

**🔹 Khóa/Mở khóa tài khoản:**
- **Khóa:** Thay đổi trạng thái từ "hoat_dong" thành "bi_khoa"
- **Mở khóa:** Thay đổi trạng thái từ "bi_khoa" thành "hoat_dong"
- **Xác nhận:** Hộp thoại xác nhận trước khi thực hiện
- **Hiển thị:** Trạng thái được cập nhật trong bảng quản lý

#### **Lịch sử đăng nhập:**
- **Ghi log:** Mọi lần đăng nhập/đăng xuất được ghi lại
- **Thông tin:** Tên đăng nhập, thời gian, địa chỉ IP, trạng thái, ghi chú
- **Hiển thị:** Bảng lịch sử với màu xen kẽ (xanh nhạt/trắng)
- **Xuất báo cáo:** Tạo file Excel với lịch sử đăng nhập

#### **Tính năng bảo mật:**
- **Mã hóa mật khẩu:** Sử dụng hash an toàn
- **Session management:** Theo dõi trạng thái đăng nhập
- **Logging:** Ghi lại mọi hoạt động của người dùng
- **Validation:** Kiểm tra dữ liệu đầu vào

#### **Giao diện người dùng:**
- **Theme:** Màu hồng cho User, màu xanh dương cho Admin
- **Responsive:** Tự động điều chỉnh kích thước
- **User-friendly:** Giao diện thân thiện, dễ sử dụng
- **Real-time:** Cập nhật thông tin ngay lập tức

#### **Kiểm tra toàn diện:**
1. **Kết nối:** Server và Client kết nối thành công
2. **Database:** Dữ liệu được lưu và truy xuất chính xác
3. **Giao diện:** Tất cả chức năng hoạt động bình thường
4. **Bảo mật:** Mật khẩu được mã hóa, session được quản lý
5. **Logging:** Mọi hoạt động được ghi lại chi tiết

## Lưu Ý
- Đảm bảo MySQL server đang chạy và cơ sở dữ liệu `he_thong_dang_nhap` đã được tạo trước khi chạy chương trình.
- Kiểm tra thông tin kết nối JDBC (URL, TEN_NGUOI_DUNG, MAT_KHAU) để đảm bảo chính xác.
- Server phải được chạy trước khi Client kết nối.

## 📞 5. Liên hệ cá nhân  
- 👨‍🎓 **Người thực hiện**: Đặng Thanh Bình
- 🎓 **Khoa**: Công nghệ thông tin – Trường Đại học Đại Nam
- 📞 **Số điện thoại**: 0822968881
- 📧 **Email**: dnagbinh12@gmail.com