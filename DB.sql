
CREATE DATABASE LoginDB;
USE LoginDB;

-- Bảng người dùng đơn giản
CREATE TABLE NguoiDung (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    hoTen VARCHAR(100) NOT NULL,
    matKhau VARCHAR(50) NOT NULL,
    vaiTro ENUM('Admin', 'User') DEFAULT 'User',
    trangThai ENUM('Active', 'Locked') DEFAULT 'Active',
    email VARCHAR(100),
    soDienThoai VARCHAR(15),
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Thêm dữ liệu mẫu
INSERT INTO NguoiDung(tenDangNhap, hoTen, matKhau, vaiTro, email, soDienThoai) 
VALUES 
('admin', 'Quan tri vien he thong', 'admin', 'Admin', 'admin@system.com', '0123456789'),
('user1', 'Dang Thanh Binh', 'user1', 'User', 'binh.dang@email.com', '0987654321'),
('user2', 'Nguyen Van A', 'user2', 'User', 'nguyenvana@email.com', '0912345678'),
('user3', 'Le Van C', 'user3', 'User', 'levanc@email.com', '0934567890'),
('test', 'Tai khoan Test', 'test', 'User', 'test@email.com', '0123456789');

-- Xem dữ liệu
SELECT '=== THONG TIN NGUOI DUNG ===' as Info;
SELECT * FROM NguoiDung;
