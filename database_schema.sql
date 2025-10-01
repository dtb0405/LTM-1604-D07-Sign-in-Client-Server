-- Tạo database cho hệ thống đăng nhập
CREATE DATABASE IF NOT EXISTS he_thong_dang_nhap;
USE he_thong_dang_nhap;

-- Bảng tài khoản người dùng
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
    lan_dang_nhap_cuoi TIMESTAMP NULL
);

-- Bảng lịch sử đăng nhập
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

-- Tạo tài khoản admin mặc định
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, vai_tro) 
VALUES ('binh', 'binh', 'Đặng Thanh Bình', 'admin');

-- Tạo một số tài khoản user mẫu
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro) 
VALUES 
('user1', 'user123', 'Nguyen Van A', 'nguyenvana@email.com', 'user'),
('user2', 'user123', 'Tran Thi B', 'tranthib@email.com', 'user');



