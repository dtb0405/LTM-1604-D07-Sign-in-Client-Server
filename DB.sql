CREATE DATABASE LoginDB;

USE LoginDB;

CREATE TABLE NguoiDung (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    matKhau VARCHAR(50) NOT NULL
);

-- Them tai khoan nguoi dung
INSERT INTO NguoiDung(tenDangNhap, matKhau) VALUES('admin', '0');
INSERT INTO NguoiDung(tenDangNhap, matKhau) VALUES('user1', '1');
