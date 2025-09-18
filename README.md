<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
   Hệ thống đăng nhập Client-Server (TCP Socket + Java Swing + MySQL)
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
Hệ thống đăng nhập Client-Server được xây dựng dựa trên mô hình giao tiếp TCP Socket giữa máy khách (Client) và máy chủ (Server). Trong hệ thống này, Server lắng nghe tại cổng (port) 2712 để nhận và xử lý các yêu cầu từ phía Client. Người dùng phía Client sẽ thực hiện thao tác đăng nhập bằng cách nhập tên tài khoản và mật khẩu, sau đó thông tin này sẽ được gửi tới Server thông qua kết nối TCP.

Phía Server có giao diện quản lý tài khoản trực quan, cho phép Thêm, Sửa, Xoá dữ liệu người dùng. Dữ liệu này được lưu trữ và quản lý trong cơ sở dữ liệu MySQL, được kết nối bằng JDBC (Java Database Connectivity), đảm bảo tính an toàn và toàn vẹn dữ liệu.

## 🔧 2. Ngôn ngữ lập trình sử dụng  
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

### 🔑 Giao diện đăng nhập từ Client
![Login](docs/Client_login.png)

### 🔏 Giao diện đăng ký từ Client
![Register](docs/Client_register.png)

### 🛠️ Giao diện Server quản lý tài khoản
![Server](docs/Server_dashboard.png)

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
CREATE DATABASE LoginDB;
USE LoginDB;

CREATE TABLE NguoiDung (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    matKhau VARCHAR(100) NOT NULL
);
```
Thêm một số tài khoản test: 
```sql
INSERT INTO users(tenDangNhap, matKhau) VALUES ('admin', 'admin1');
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
1. Mở lớp `MayChu.java` trong thư mục `Server`.
2. Chạy chương trình (`Run`).
3. Server sẽ khởi động và lắng nghe kết nối trên **port 2712**.

#### Chạy Client
1. Mở lớp `GiaoDienNguoiDung.java` trong thư mục `Client`.
2. Chạy chương trình (`Run`).
3. Giao diện người dùng sẽ hiển thị, cho phép:
   - **Đăng nhập**: Nhập tài khoản và mật khẩu để đăng nhập.
   - **Đăng ký**: Nhập thông tin để tạo tài khoản mới.

### 4.6. Kiểm Tra Kết Quả
- **Đăng nhập thành công**: Server sẽ ghi log thông tin kết nối vào console hoặc file log.
- **Đăng ký tài khoản**: Dữ liệu người dùng sẽ được lưu trực tiếp vào cơ sở dữ liệu MySQL thông qua JDBC.
- **Giao diện Server**: Hỗ trợ các chức năng:
  - Thêm người dùng.
  - Sửa thông tin người dùng.
  - Xóa người dùng.
  - Đăng xuất người dùng.

## Lưu Ý
- Đảm bảo MySQL server đang chạy và cơ sở dữ liệu `NguoiDung` đã được tạo trước khi chạy chương trình.
- Kiểm tra thông tin kết nối JDBC (URL, TEN_NGUOI_DUNG, MAT_KHAU) để đảm bảo chính xác.
- Server phải được chạy trước khi Client kết nối.

## 📞 5. Liên hệ cá nhân  
- 👤 **Họ và tên**: *Đặng Thanh Bình*  
- 🎓 **Lớp**: *CNTT 16-04*
- 📧 **Email**: *dnagbinh12@gmail.com*  
- 📱 **Số điện thoại**: *0822968881*  
