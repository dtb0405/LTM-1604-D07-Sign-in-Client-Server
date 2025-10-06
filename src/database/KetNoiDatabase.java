package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp quản lý kết nối và thao tác với database MySQL
 */
public class KetNoiDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/he_thong_dang_nhap";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "binh";
    
    private static KetNoiDatabase instance;
    private Connection connection;
    
    private KetNoiDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
            this.connection = null; // Đảm bảo connection = null khi lỗi
        }
    }
    
    public static synchronized KetNoiDatabase getInstance() {
        if (instance == null) {
            instance = new KetNoiDatabase();
        }
        return instance;
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tạo kết nối database: " + e.getMessage());
            e.printStackTrace();
            connection = null;
        }
        return connection;
    }
    
    /**
     * Xác thực đăng nhập
     */
    public TaiKhoan xacThucDangNhap(String tenDangNhap, String matKhau) {
        if (connection == null) {
            System.err.println("Lỗi: Không có kết nối database!");
            return null;
        }
        
        String sql = "SELECT * FROM tai_khoan WHERE ten_dang_nhap = ? AND mat_khau = ? AND trang_thai = 'hoat_dong'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap);
            stmt.setString(2, matKhau);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setId(rs.getInt("id"));
                tk.setTenDangNhap(rs.getString("ten_dang_nhap"));
                tk.setHoTen(rs.getString("ho_ten"));
                tk.setEmail(rs.getString("email"));
                tk.setSoDienThoai(rs.getString("so_dien_thoai"));
                tk.setNgaySinh(rs.getDate("ngay_sinh"));
                tk.setVaiTro(rs.getString("vai_tro"));
                tk.setTrangThai(rs.getString("trang_thai"));
                
                // Cập nhật trạng thái online và thời gian đăng nhập cuối
                capNhatTrangThaiOnline(tk.getId(), true);
                capNhatLanDangNhapCuoi(tk.getId());
                
                // Cập nhật trạng thái online trong object
                tk.setTrangThaiOnline("online");
                
                return tk;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Đăng ký tài khoản mới
     */
    public boolean dangKyTaiKhoan(String tenDangNhap, String matKhau, String hoTen) {
        // Kiểm tra tên đăng nhập đã tồn tại
        if (kiemTraTenDangNhapTonTai(tenDangNhap)) {
            return false;
        }
        
        String sql = "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, trang_thai, trang_thai_online, vai_tro, ngay_sinh) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap);
            stmt.setString(2, matKhau);
            stmt.setString(3, hoTen);
            stmt.setString(4, "hoat_dong"); // Trạng thái mặc định: hoạt động
            stmt.setString(5, "offline");   // Trạng thái online mặc định: offline
            stmt.setString(6, "user");      // Vai trò mặc định: user
            stmt.setNull(7, java.sql.Types.DATE); // Ngày sinh mặc định: NULL
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Kiểm tra tên đăng nhập đã tồn tại
     */
    public boolean kiemTraTenDangNhapTonTai(String tenDangNhap) {
        String sql = "SELECT COUNT(*) FROM tai_khoan WHERE ten_dang_nhap = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật trạng thái online/offline
     */
    public void capNhatTrangThaiOnline(int taiKhoanId, boolean online) {
        String sql = "UPDATE tai_khoan SET trang_thai_online = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, online ? "online" : "offline");
            stmt.setInt(2, taiKhoanId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Đặt tất cả tài khoản về offline (khi khởi động server)
     */
    public void datTatCaTaiKhoanOffline() {
        String sql = "UPDATE tai_khoan SET trang_thai_online = 'offline'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Cập nhật thời gian đăng nhập cuối
     */
    private void capNhatLanDangNhapCuoi(int taiKhoanId) {
        String sql = "UPDATE tai_khoan SET lan_dang_nhap_cuoi = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taiKhoanId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Ghi lịch sử đăng nhập
     */
    public void ghiLichSuDangNhap(int taiKhoanId, String diaChiIp, String trangThai) {
        // Lấy tên đăng nhập từ taiKhoanId
        String tenDangNhap = layTenDangNhapTheoId(taiKhoanId);
        
        String sql = "INSERT INTO lich_su_dang_nhap (tai_khoan_id, ten_dang_nhap, dia_chi_ip, trang_thai) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taiKhoanId);
            stmt.setString(2, tenDangNhap);
            stmt.setString(3, diaChiIp);
            stmt.setString(4, trangThai);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database: Lỗi ghi lịch sử đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy tên đăng nhập theo ID
     */
    private String layTenDangNhapTheoId(int taiKhoanId) {
        String sql = "SELECT ten_dang_nhap FROM tai_khoan WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taiKhoanId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("ten_dang_nhap");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tên đăng nhập: " + e.getMessage());
        }
        return "Unknown";
    }
    
    /**
     * Ghi lịch sử đăng nhập thất bại (không cần taiKhoanId)
     */
    public void ghiLichSuDangNhapThatBai(String tenDangNhap, String diaChiIp, String lyDo) {
        String sql = "INSERT INTO lich_su_dang_nhap (ten_dang_nhap, dia_chi_ip, trang_thai, ghi_chu) VALUES (?, ?, 'that_bai', ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap);
            stmt.setString(2, diaChiIp);
            stmt.setString(3, lyDo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database: Lỗi ghi lịch sử đăng nhập thất bại: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy mật khẩu thật của tài khoản theo ID
     */
    public String layMatKhauThuc(int taiKhoanId) {
        String sql = "SELECT mat_khau FROM tai_khoan WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taiKhoanId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("mat_khau");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy mật khẩu: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Đổi mật khẩu cho admin (không cần mật khẩu cũ)
     */
    public boolean doiMatKhauAdmin(int taiKhoanId, String matKhauMoi) {
        String sql = "UPDATE tai_khoan SET mat_khau = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, matKhauMoi);
            stmt.setInt(2, taiKhoanId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database: Lỗi đổi mật khẩu admin: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Lấy danh sách tất cả tài khoản (cho admin)
     */
    public List<TaiKhoan> layDanhSachTaiKhoan() {
        List<TaiKhoan> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM tai_khoan ORDER BY ngay_tao DESC";
        
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                TaiKhoan tk = new TaiKhoan();
                tk.setId(rs.getInt("id"));
                tk.setTenDangNhap(rs.getString("ten_dang_nhap"));
                tk.setHoTen(rs.getString("ho_ten"));
                tk.setEmail(rs.getString("email"));
                tk.setSoDienThoai(rs.getString("so_dien_thoai"));
                tk.setNgaySinh(rs.getDate("ngay_sinh"));
                tk.setVaiTro(rs.getString("vai_tro"));
                tk.setTrangThai(rs.getString("trang_thai"));
                tk.setTrangThaiOnline(rs.getString("trang_thai_online"));
                tk.setNgayTao(rs.getTimestamp("ngay_tao"));
                tk.setLanDangNhapCuoi(rs.getTimestamp("lan_dang_nhap_cuoi"));
                
                
                danhSach.add(tk);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách tài khoản: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }
    
    /**
     * Tìm tài khoản theo tên đăng nhập
     */
    public TaiKhoan timTaiKhoanTheoTen(String tenDangNhap) {
        String sql = "SELECT * FROM tai_khoan WHERE ten_dang_nhap = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setId(rs.getInt("id"));
                tk.setTenDangNhap(rs.getString("ten_dang_nhap"));
                tk.setHoTen(rs.getString("ho_ten"));
                tk.setEmail(rs.getString("email"));
                tk.setSoDienThoai(rs.getString("so_dien_thoai"));
                tk.setNgaySinh(rs.getDate("ngay_sinh"));
                tk.setVaiTro(rs.getString("vai_tro"));
                tk.setTrangThai(rs.getString("trang_thai"));
                tk.setTrangThaiOnline(rs.getString("trang_thai_online"));
                tk.setNgayTao(rs.getTimestamp("ngay_tao"));
                tk.setLanDangNhapCuoi(rs.getTimestamp("lan_dang_nhap_cuoi"));
                return tk;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Đếm tổng số tài khoản
     */
    public int demTongTaiKhoan() {
        String sql = "SELECT COUNT(*) FROM tai_khoan";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Đếm số tài khoản bị khóa
     */
    public int demTaiKhoanBiKhoa() {
        String sql = "SELECT COUNT(*) FROM tai_khoan WHERE trang_thai = 'bi_khoa'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Đếm số tài khoản hoạt động
     */
    public int demTaiKhoanHoatDong() {
        String sql = "SELECT COUNT(*) FROM tai_khoan WHERE trang_thai = 'hoat_dong'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Đếm lượt đăng nhập trong ngày
     */
    public int demLuotDangNhapTrongNgay() {
        String sql = "SELECT COUNT(*) FROM lich_su_dang_nhap WHERE DATE(thoi_gian_dang_nhap) = CURDATE() AND trang_thai = 'thanh_cong'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Kiểm tra dữ liệu trong bảng lịch sử đăng nhập
     */
    
    /**
     * Đóng kết nối
     */
    public List<String[]> layLichSuDangNhap() {
        List<String[]> lichSu = new ArrayList<>();
        try {
            String sql = "SELECT ls.id, ls.tai_khoan_id, COALESCE(ls.ten_dang_nhap, tk.ten_dang_nhap) as ten_dang_nhap, " +
                        "ls.thoi_gian_dang_nhap, ls.thoi_gian_dang_xuat, ls.dia_chi_ip, ls.trang_thai, ls.ghi_chu " +
                        "FROM lich_su_dang_nhap ls " +
                        "LEFT JOIN tai_khoan tk ON ls.tai_khoan_id = tk.id " +
                        "ORDER BY ls.thoi_gian_dang_nhap DESC LIMIT 50";
            
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String[] record = {
                    String.valueOf(rs.getInt("id")),
                    rs.getString("ten_dang_nhap"),
                    rs.getTimestamp("thoi_gian_dang_nhap").toString(),
                    rs.getTimestamp("thoi_gian_dang_xuat") != null ? 
                        rs.getTimestamp("thoi_gian_dang_xuat").toString() : "Chưa đăng xuất",
                    rs.getString("dia_chi_ip"),
                    rs.getString("trang_thai"),
                    rs.getString("ghi_chu") != null ? rs.getString("ghi_chu") : ""
                };
                lichSu.add(record);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return lichSu;
    }
    
    /**
     * Đếm số lượng tài khoản online
     */
    public int demTaiKhoanOnline() {
        if (connection == null) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM tai_khoan WHERE trang_thai_online = 'online'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public void dongKetNoi() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Thêm tài khoản mới
     */
    public boolean themTaiKhoan(String tenDangNhap, String matKhau, String hoTen, String email, String soDienThoai, String vaiTro) {
        return themTaiKhoan(tenDangNhap, matKhau, hoTen, email, soDienThoai, vaiTro, "hoat_dong");
    }
    
    public boolean themTaiKhoan(String tenDangNhap, String matKhau, String hoTen, String email, String soDienThoai, String vaiTro, String trangThai) {
        if (connection == null) {
            System.err.println("Lỗi: Không có kết nối database!");
            return false;
        }
        
        // Kiểm tra tên đăng nhập đã tồn tại
        if (kiemTraTenDangNhapTonTai(tenDangNhap)) {
            return false;
        }
        
        String sql = "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, so_dien_thoai, vai_tro, trang_thai, trang_thai_online) VALUES (?, ?, ?, ?, ?, ?, ?, 'offline')";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap);
            stmt.setString(2, matKhau);
            stmt.setString(3, hoTen);
            stmt.setString(4, email);
            stmt.setString(5, soDienThoai);
            stmt.setString(6, vaiTro);
            stmt.setString(7, trangThai);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật thông tin tài khoản
     */
    public boolean capNhatTaiKhoan(int taiKhoanId, String hoTen, String email, String soDienThoai, String vaiTro) {
        if (connection == null) {
            System.err.println("Lỗi: Không có kết nối database!");
            return false;
        }
        
        String sql = "UPDATE tai_khoan SET ho_ten = ?, email = ?, so_dien_thoai = ?, vai_tro = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, hoTen);
            stmt.setString(2, email);
            stmt.setString(3, soDienThoai);
            stmt.setString(4, vaiTro);
            stmt.setInt(5, taiKhoanId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật thông tin tài khoản bao gồm ngày sinh
     */
    public boolean capNhatTaiKhoan(int taiKhoanId, String hoTen, String email, String soDienThoai, String vaiTro, java.sql.Date ngaySinh) {
        if (connection == null) {
            System.err.println("Lỗi: Không có kết nối database!");
            return false;
        }
        
        
        String sql = "UPDATE tai_khoan SET ho_ten = ?, email = ?, so_dien_thoai = ?, vai_tro = ?, ngay_sinh = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, hoTen);
            if (email == null || email.isEmpty()) {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(2, email);
            }
            if (soDienThoai == null || soDienThoai.isEmpty()) {
                stmt.setNull(3, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(3, soDienThoai);
            }
            stmt.setString(4, vaiTro);
            stmt.setDate(5, ngaySinh);
            stmt.setInt(6, taiKhoanId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database: Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Xóa tài khoản
     */
    public boolean xoaTaiKhoan(int taiKhoanId) {
        if (connection == null) {
            System.err.println("Lỗi: Không có kết nối database!");
            return false;
        }
        
        String sql = "DELETE FROM tai_khoan WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taiKhoanId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Đổi mật khẩu
     */
    public boolean doiMatKhau(int taiKhoanId, String matKhauCu, String matKhauMoi) {
        if (connection == null) {
            System.err.println("Lỗi: Không có kết nối database!");
            return false;
        }
        
        // Kiểm tra mật khẩu cũ
        String sqlCheck = "SELECT mat_khau FROM tai_khoan WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlCheck)) {
            stmt.setInt(1, taiKhoanId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String matKhauTrongDB = rs.getString("mat_khau");
                if (!matKhauCu.equals(matKhauTrongDB)) {
                    return false; // Mật khẩu cũ không đúng
                }
            } else {
                return false; // Không tìm thấy tài khoản
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        // Cập nhật mật khẩu mới
        String sqlUpdate = "UPDATE tai_khoan SET mat_khau = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlUpdate)) {
            stmt.setString(1, matKhauMoi);
            stmt.setInt(2, taiKhoanId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Khóa/mở khóa tài khoản
     */
    public boolean capNhatTrangThaiTaiKhoan(int taiKhoanId, String trangThai) {
        if (connection == null) {
            System.err.println("Lỗi: Không có kết nối database!");
            return false;
        }
        
        String sql = "UPDATE tai_khoan SET trang_thai = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, trangThai);
            stmt.setInt(2, taiKhoanId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Đăng xuất tài khoản - cập nhật trạng thái offline
     */
    public void dangXuatTaiKhoan(int taiKhoanId) {
        if (connection == null) {
            System.err.println("Lỗi: Không có kết nối database!");
            return;
        }
        
        // Cập nhật trạng thái offline và thời gian đăng xuất
        String sql = "UPDATE tai_khoan SET trang_thai_online = 'offline', lan_dang_xuat_cuoi = NOW() WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taiKhoanId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Cập nhật thời gian đăng xuất trong lịch sử đăng nhập
        String sqlLichSu = "UPDATE lich_su_dang_nhap SET thoi_gian_dang_xuat = NOW() WHERE tai_khoan_id = ? AND thoi_gian_dang_xuat IS NULL ORDER BY thoi_gian_dang_nhap DESC LIMIT 1";
        try (PreparedStatement stmtLichSu = connection.prepareStatement(sqlLichSu)) {
            stmtLichSu.setInt(1, taiKhoanId);
            stmtLichSu.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Ghi lịch sử đăng xuất
        ghiLichSuDangNhap(taiKhoanId, "127.0.0.1", "dang_xuat");
    }
    
    /**
     * Cập nhật ảnh đại diện cho tài khoản
     */
    public boolean capNhatAnhDaiDien(int taiKhoanId, String avatarPath) {
        Connection connection = getConnection();
        if (connection == null) return false;
        
        String sql = "UPDATE tai_khoan SET avatar_path = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, avatarPath);
            stmt.setInt(2, taiKhoanId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật ảnh đại diện: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy đường dẫn ảnh đại diện của tài khoản
     */
    public String layAnhDaiDien(int taiKhoanId) {
        Connection connection = getConnection();
        if (connection == null) return null;
        
        String sql = "SELECT avatar_path FROM tai_khoan WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taiKhoanId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("avatar_path");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy ảnh đại diện: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Xóa ảnh đại diện của tài khoản
     */
    public boolean xoaAnhDaiDien(int taiKhoanId) {
        Connection connection = getConnection();
        if (connection == null) return false;
        
        String sql = "UPDATE tai_khoan SET avatar_path = NULL WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taiKhoanId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa ảnh đại diện: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lưu ảnh đại diện dạng BLOB vào database
     */
    public boolean luuAnhDaiDienBlob(int taiKhoanId, byte[] imageData) {
        Connection connection = getConnection();
        if (connection == null) return false;
        
        String sql = "UPDATE tai_khoan SET avatar_data = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBytes(1, imageData);
            stmt.setInt(2, taiKhoanId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi lưu ảnh đại diện BLOB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy ảnh đại diện dạng BLOB từ database
     */
    public byte[] layAnhDaiDienBlob(int taiKhoanId) {
        Connection connection = getConnection();
        if (connection == null) return null;
        
        String sql = "SELECT avatar_data FROM tai_khoan WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taiKhoanId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBytes("avatar_data");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy ảnh đại diện BLOB: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
