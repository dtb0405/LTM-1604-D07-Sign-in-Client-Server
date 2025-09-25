package Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class QuanLyNguoiDung {

    // Đăng nhập đơn giản
    public boolean dangNhap(String tenDangNhap, String matKhau) {
        String sql = "SELECT * FROM NguoiDung WHERE tenDangNhap=? AND matKhau=? AND trangThai='Active'";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Đăng ký với đầy đủ thông tin
    public boolean dangKy(String hoTen, String tenDangNhap, String matKhau, String email, String soDienThoai) {
        String sql = "INSERT INTO NguoiDung(hoTen, tenDangNhap, matKhau, email, soDienThoai, vaiTro, trangThai) VALUES(?, ?, ?, ?, ?, 'User', 'Active')";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hoTen);
            ps.setString(2, tenDangNhap);
            ps.setString(3, matKhau);
            ps.setString(4, email);
            ps.setString(5, soDienThoai);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Đổi mật khẩu
    public boolean doiMatKhau(String tenDangNhap, String matKhauCu, String matKhauMoi) {
        String sql = "UPDATE NguoiDung SET matKhau=? WHERE tenDangNhap=? AND matKhau=?";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matKhauMoi);
            ps.setString(2, tenDangNhap);
            ps.setString(3, matKhauCu);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách người dùng online (đơn giản)
    // Danh sách user thực sự đang online (được quản lý bởi ServerMain)
    private static List<String> onlineUsers = new ArrayList<>();
    
    public List<String> layDanhSachNguoiDungOnline() {
        return new ArrayList<>(onlineUsers);
    }
    
    // Thêm user vào danh sách online
    public static void themUserOnline(String tenDangNhap) {
        if (!onlineUsers.contains(tenDangNhap)) {
            onlineUsers.add(tenDangNhap);
        }
    }
    
    // Xóa user khỏi danh sách online
    public static void xoaUserOnline(String tenDangNhap) {
        onlineUsers.remove(tenDangNhap);
    }
    
    // Lấy thông tin chi tiết của user online
    public List<String> layDanhSachNguoiDungOnlineChiTiet() {
        List<String> danhSach = new ArrayList<>();
        if (onlineUsers.isEmpty()) {
            return danhSach;
        }
        
        String sql = "SELECT tenDangNhap, hoTen, vaiTro FROM NguoiDung WHERE tenDangNhap IN (" + 
                    String.join(",", java.util.Collections.nCopies(onlineUsers.size(), "?")) + ")";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < onlineUsers.size(); i++) {
                ps.setString(i + 1, onlineUsers.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                danhSach.add(rs.getString("tenDangNhap") + " (" + rs.getString("hoTen") + ") - " + rs.getString("vaiTro"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // Lấy thống kê người dùng thực sự
    public int[] layThongKeNguoiDung() {
        int[] thongKe = new int[4]; // [tổng, online, admin, user]
        
        // Tổng số user trong database
        String sql = "SELECT COUNT(*) as total FROM NguoiDung";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                thongKe[0] = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Số user đang online thực sự
        thongKe[1] = onlineUsers.size();
        
        // Đếm admin và user từ danh sách online
        if (!onlineUsers.isEmpty()) {
            String sql2 = "SELECT vaiTro FROM NguoiDung WHERE tenDangNhap IN (" + 
                         String.join(",", java.util.Collections.nCopies(onlineUsers.size(), "?")) + ")";
            try (Connection conn = KetNoiDB.ketNoi();
                 PreparedStatement ps = conn.prepareStatement(sql2)) {
                for (int i = 0; i < onlineUsers.size(); i++) {
                    ps.setString(i + 1, onlineUsers.get(i));
                }
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    if ("Admin".equals(rs.getString("vaiTro"))) {
                        thongKe[2]++;
                    } else {
                        thongKe[3]++;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return thongKe;
    }
    
    // Lấy số lần đăng nhập hôm nay
    public int laySoLanDangNhapHomNay() {
        String today = java.time.LocalDate.now().toString();
        return (int) loginHistory.stream()
                .filter(entry -> entry.startsWith(today))
                .count();
    }

    // Khóa/mở khóa tài khoản
    public boolean khoaTaiKhoan(String tenDangNhap, boolean khoa) {
        String trangThai = khoa ? "Locked" : "Active";
        String sql = "UPDATE NguoiDung SET trangThai=? WHERE tenDangNhap=?";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật vai trò
    public boolean capNhatVaiTro(String tenDangNhap, String vaiTro) {
        String sql = "UPDATE NguoiDung SET vaiTro=? WHERE tenDangNhap=?";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vaiTro);
            ps.setString(2, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa tài khoản
    public boolean xoaTaiKhoan(String tenDangNhap) {
        String sql = "DELETE FROM NguoiDung WHERE tenDangNhap=?";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm người dùng
    public List<String> timKiemNguoiDung(String tuKhoa) {
        List<String> ketQua = new ArrayList<>();
        String sql = "SELECT tenDangNhap, hoTen, vaiTro, trangThai FROM NguoiDung WHERE tenDangNhap LIKE ? OR hoTen LIKE ?";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + tuKhoa + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ketQua.add(rs.getString("tenDangNhap") + " - " + rs.getString("hoTen") + " (" + rs.getString("vaiTro") + ") - " + rs.getString("trangThai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    // Lọc người dùng theo vai trò và trạng thái
    public List<String> locNguoiDung(String vaiTro, String trangThai) {
        List<String> ketQua = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT tenDangNhap, hoTen, vaiTro, trangThai FROM NguoiDung WHERE 1=1");
        
        if (!vaiTro.equals("Tất cả")) {
            sql.append(" AND vaiTro = ?");
        }
        if (!trangThai.equals("Tất cả")) {
            sql.append(" AND trangThai = ?");
        }
        
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (!vaiTro.equals("Tất cả")) {
                ps.setString(paramIndex++, vaiTro);
            }
            if (!trangThai.equals("Tất cả")) {
                ps.setString(paramIndex++, trangThai);
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ketQua.add(rs.getString("tenDangNhap") + " - " + rs.getString("hoTen") + " (" + rs.getString("vaiTro") + ") - " + rs.getString("trangThai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    // Lưu trữ lịch sử đăng nhập thực sự
    private static List<String> loginHistory = new ArrayList<>();
    
    // Ghi lịch sử đăng nhập
    public static void ghiLichSuDangNhap(String tenDangNhap, String hoTen, String vaiTro) {
        String timestamp = java.time.LocalDateTime.now().toString();
        String logEntry = timestamp + " - " + tenDangNhap + " - " + hoTen + " - " + vaiTro;
        loginHistory.add(0, logEntry); // Thêm vào đầu danh sách
        
        // Giới hạn lịch sử tối đa 100 entries
        if (loginHistory.size() > 100) {
            loginHistory = loginHistory.subList(0, 100);
        }
    }
    
    // Lấy lịch sử đăng nhập thực sự
    public List<String> layLichSuDangNhap(String tenDangNhap) {
        if (tenDangNhap.isEmpty()) {
            // Trả về tất cả lịch sử
            return new ArrayList<>(loginHistory);
        } else {
            // Trả về lịch sử của user cụ thể
            return loginHistory.stream()
                    .filter(entry -> entry.contains(" - " + tenDangNhap + " - "))
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    // Cập nhật lần đăng nhập cuối
    private void capNhatLanDangNhapCuoi(String tenDangNhap, String ipDiaChi) {
        String sql = "UPDATE NguoiDung SET lanDangNhapCuoi=CURRENT_TIMESTAMP, ipDangNhapCuoi=? WHERE tenDangNhap=?";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ipDiaChi);
            ps.setString(2, tenDangNhap);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy thông tin chi tiết người dùng
    public String layThongTinNguoiDung(String tenDangNhap) {
        String sql = "SELECT * FROM NguoiDung WHERE tenDangNhap=?";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Tên đăng nhập: " + rs.getString("tenDangNhap") + 
                       ";Họ tên: " + rs.getString("hoTen") +
                       ";Vai trò: " + rs.getString("vaiTro") +
                       ";Trạng thái: " + rs.getString("trangThai") +
                       ";Email: " + (rs.getString("email") != null ? rs.getString("email") : "Chưa cập nhật") +
                       ";SĐT: " + (rs.getString("soDienThoai") != null ? rs.getString("soDienThoai") : "Chưa cập nhật") +
                       ";Ngày tạo: " + rs.getTimestamp("ngayTao");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Không tìm thấy thông tin";
    }
    
    // Cập nhật thông tin người dùng
    public boolean capNhatThongTinNguoiDung(String tenDangNhap, String hoTen, String email, String soDienThoai) {
        String sql = "UPDATE NguoiDung SET hoTen=?, email=?, soDienThoai=? WHERE tenDangNhap=?";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hoTen);
            ps.setString(2, email);
            ps.setString(3, soDienThoai);
            ps.setString(4, tenDangNhap);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Cập nhật mật khẩu
    public boolean capNhatMatKhau(String tenDangNhap, String matKhauCu, String matKhauMoi) {
        // Kiểm tra mật khẩu cũ
        String checkSql = "SELECT * FROM NguoiDung WHERE tenDangNhap=? AND matKhau=?";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setString(1, tenDangNhap);
            checkPs.setString(2, matKhauCu);
            ResultSet rs = checkPs.executeQuery();
            
            if (!rs.next()) {
                return false; // Mật khẩu cũ không đúng
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        // Cập nhật mật khẩu mới
        String updateSql = "UPDATE NguoiDung SET matKhau=? WHERE tenDangNhap=?";
        try (Connection conn = KetNoiDB.ketNoi();
             PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
            updatePs.setString(1, matKhauMoi);
            updatePs.setString(2, tenDangNhap);
            
            int rowsAffected = updatePs.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
