-- Tạo database cho hệ thống đăng nhập
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

-- =====================================================
-- COMMENTS VÀ DOCUMENTATION
-- =====================================================

-- Bảng tai_khoan: Lưu trữ thông tin tài khoản người dùng
-- - id: Khóa chính, tự tăng
-- - ten_dang_nhap: Tên đăng nhập duy nhất
-- - mat_khau: Mật khẩu đã mã hóa
-- - ho_ten: Họ tên đầy đủ
-- - email: Email liên hệ
-- - so_dien_thoai: Số điện thoại
-- - ngay_sinh: Ngày sinh
-- - vai_tro: Vai trò (user/admin)
-- - trang_thai: Trạng thái tài khoản (hoat_dong/bi_khoa)
-- - trang_thai_online: Trạng thái online/offline
-- - ngay_tao: Ngày tạo tài khoản
-- - lan_dang_nhap_cuoi: Lần đăng nhập cuối cùng
-- - avatar_path: Đường dẫn ảnh đại diện
-- - avatar_data: Dữ liệu ảnh đại diện (BLOB)

-- Bảng lich_su_dang_nhap: Lưu trữ lịch sử đăng nhập
-- - id: Khóa chính, tự tăng
-- - tai_khoan_id: ID tài khoản (khóa ngoại)
-- - ten_dang_nhap: Tên đăng nhập
-- - thoi_gian_dang_nhap: Thời gian đăng nhập
-- - thoi_gian_dang_xuat: Thời gian đăng xuất
-- - dia_chi_ip: Địa chỉ IP
-- - trang_thai: Trạng thái đăng nhập (thanh_cong/that_bai)
-- - ghi_chu: Ghi chú bổ sung

-- =====================================================
-- KẾT THÚC SCHEMA
-- =====================================================
