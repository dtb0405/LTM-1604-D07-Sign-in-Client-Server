package server;

import protocol.ThongDiepTCP;
import database.KetNoiDatabase;
import database.TaiKhoan;

import java.io.*;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp xử lý yêu cầu từ client
 */
public class XuLyClient implements Runnable {
    private Socket clientSocket;
    private MayChuTCP server;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private TaiKhoan taiKhoanHienTai;
    private KetNoiDatabase database;
    
    public XuLyClient(Socket clientSocket, MayChuTCP server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.database = server.getDatabase();
    }
    
    @Override
    public void run() {
        String clientInfo = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
        server.ghiLog("Thread xử lý client " + clientInfo + " đã bắt đầu");
        
        try {
            // Khởi tạo input/output stream
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ois = new ObjectInputStream(clientSocket.getInputStream());
            server.ghiLog("Đã khởi tạo I/O streams cho client " + clientInfo);
            
            // Xử lý các yêu cầu từ client
            while (!clientSocket.isClosed()) {
                try {
                    ThongDiepTCP yeuCau = (ThongDiepTCP) ois.readObject();
                    server.ghiLog("Nhận yêu cầu từ " + clientInfo + ": " + yeuCau.getLoaiYeuCau());
                    
                    ThongDiepTCP phanhoi = xuLyYeuCau(yeuCau);
                    
                    if (phanhoi != null) {
                        oos.writeObject(phanhoi);
                        oos.flush();
                        server.ghiLog("Đã gửi phản hồi cho " + clientInfo + ": " + phanhoi.getMaPhanhoi());
                    }
                    
                } catch (ClassNotFoundException e) {
                    server.ghiLog("Lỗi đọc thông điệp: " + e.getMessage());
                    break;
                } catch (EOFException e) {
                    // Client đã đóng kết nối
                    break;
                }
            }
            
        } catch (IOException e) {
            server.ghiLog("Lỗi kết nối client: " + e.getMessage());
        } finally {
            // Đóng kết nối và cập nhật trạng thái
            dongKetNoi();
        }
    }
    
    /**
     * Xử lý các loại yêu cầu từ client
     */
    private ThongDiepTCP xuLyYeuCau(ThongDiepTCP yeuCau) {
        ThongDiepTCP phanhoi = new ThongDiepTCP();
        
        try {
            switch (yeuCau.getLoaiYeuCau()) {
                case ThongDiepTCP.DANG_NHAP:
                    return xuLyDangNhap(yeuCau);
                    
                case ThongDiepTCP.DANG_KY:
                    return xuLyDangKy(yeuCau);
                    
                case ThongDiepTCP.DANG_XUAT:
                    return xuLyDangXuat(yeuCau);
                    
                case ThongDiepTCP.CAP_NHAT_THONG_TIN:
                    return xuLyCapNhatThongTin(yeuCau);
                    
                case ThongDiepTCP.DOI_MAT_KHAU:
                    return xuLyDoiMatKhau(yeuCau);
                    
                case ThongDiepTCP.LAY_DANH_SACH_TAI_KHOAN:
                    return xuLyLayDanhSachTaiKhoan(yeuCau);
                    
                case ThongDiepTCP.LAY_THONG_KE_DASHBOARD:
                    return xuLyLayThongKeDashboard(yeuCau);
                    
                case ThongDiepTCP.KHOA_TAI_KHOAN:
                case ThongDiepTCP.MO_KHOA_TAI_KHOAN:
                case ThongDiepTCP.XOA_TAI_KHOAN:
                case ThongDiepTCP.CAP_NHAT_VAI_TRO:
                    return xuLyQuanLyTaiKhoan(yeuCau);
                    
                    
                default:
                    phanhoi.setMaPhanhoi(ThongDiepTCP.THAT_BAI);
                    phanhoi.setThongBaoLoi("Yêu cầu không được hỗ trợ");
            }
            
        } catch (Exception e) {
            server.ghiLog("Lỗi xử lý yêu cầu: " + e.getMessage());
            phanhoi.setMaPhanhoi(ThongDiepTCP.LOI_HE_THONG);
            phanhoi.setThongBaoLoi("Lỗi hệ thống: " + e.getMessage());
        }
        
        return phanhoi;
    }
    
    /**
     * Xử lý đăng nhập
     */
    private ThongDiepTCP xuLyDangNhap(ThongDiepTCP yeuCau) {
        String tenDangNhap = (String) yeuCau.layDuLieu("tenDangNhap");
        String matKhau = (String) yeuCau.layDuLieu("matKhau");
        
        server.ghiLog("Xử lý đăng nhập cho tài khoản: " + tenDangNhap);
        System.out.println("DEBUG: Bắt đầu xử lý đăng nhập cho " + tenDangNhap);
        
        ThongDiepTCP phanhoi = new ThongDiepTCP();
        
        TaiKhoan taiKhoan = database.xacThucDangNhap(tenDangNhap, matKhau);
        
        if (taiKhoan != null) {
            if (taiKhoan.taiKhoanHoatDong()) {
                taiKhoanHienTai = taiKhoan;
                server.themClientOnline(tenDangNhap, clientSocket);
                database.capNhatTrangThaiOnline(taiKhoan.getId(), true);
                
                // Ghi lịch sử đăng nhập thành công
                database.ghiLichSuDangNhap(taiKhoan.getId(), clientSocket.getInetAddress().getHostAddress(), "thanh_cong");
                
                phanhoi.setMaPhanhoi(ThongDiepTCP.THANH_CONG);
                Map<String, Object> duLieu = new HashMap<>();
                duLieu.put("taiKhoan", taiKhoan);
                phanhoi.setDuLieu(duLieu);
                
                server.ghiLog(">>> Đăng nhập thành công: " + tenDangNhap + " (" + taiKhoan.getVaiTro() + ")");
            } else {
                // Ghi lịch sử đăng nhập thất bại - tài khoản bị khóa
                database.ghiLichSuDangNhapThatBai(tenDangNhap, clientSocket.getInetAddress().getHostAddress(), "Tài khoản đã bị khóa");
                
                phanhoi.setMaPhanhoi(ThongDiepTCP.TAI_KHOAN_BI_KHOA);
                phanhoi.setThongBaoLoi("Tài khoản đã bị khóa");
                
                server.ghiLog(">>> Đăng nhập thất bại: " + tenDangNhap + " - Tài khoản bị khóa");
            }
        } else {
            // Ghi lịch sử đăng nhập thất bại - sai thông tin
            database.ghiLichSuDangNhapThatBai(tenDangNhap, clientSocket.getInetAddress().getHostAddress(), "Tên đăng nhập hoặc mật khẩu không đúng");
            
            phanhoi.setMaPhanhoi(ThongDiepTCP.SAI_MAT_KHAU);
            phanhoi.setThongBaoLoi("Tên đăng nhập hoặc mật khẩu không đúng");
            
            server.ghiLog(">>> Đăng nhập thất bại: " + tenDangNhap + " - Sai thông tin đăng nhập");
        }
        
        return phanhoi;
    }
    
    /**
     * Xử lý đăng ký
     */
    private ThongDiepTCP xuLyDangKy(ThongDiepTCP yeuCau) {
        String tenDangNhap = (String) yeuCau.layDuLieu("tenDangNhap");
        String matKhau = (String) yeuCau.layDuLieu("matKhau");
        String hoTen = (String) yeuCau.layDuLieu("hoTen");
        
        ThongDiepTCP phanhoi = new ThongDiepTCP();
        
        if (database.dangKyTaiKhoan(tenDangNhap, matKhau, hoTen)) {
            phanhoi.setMaPhanhoi(ThongDiepTCP.THANH_CONG);
            server.ghiLog("Đăng ký thành công tài khoản: " + tenDangNhap);
        } else {
            phanhoi.setMaPhanhoi(ThongDiepTCP.TAI_KHOAN_DA_TON_TAI);
            phanhoi.setThongBaoLoi("Tên đăng nhập đã tồn tại");
        }
        
        return phanhoi;
    }
    
    /**
     * Xử lý đăng xuất
     */
    private ThongDiepTCP xuLyDangXuat(ThongDiepTCP yeuCau) {
        ThongDiepTCP phanhoi = new ThongDiepTCP();
        
        if (taiKhoanHienTai != null) {
            String tenDangNhap = taiKhoanHienTai.getTenDangNhap();
            server.ghiLog("Xử lý đăng xuất cho tài khoản: " + tenDangNhap);
            
            // Cập nhật trạng thái offline
            database.capNhatTrangThaiOnline(taiKhoanHienTai.getId(), false);
            server.ghiLog("Đã cập nhật trạng thái offline cho " + tenDangNhap);
            
            // Cập nhật thời gian đăng xuất trong lịch sử
            capNhatThoiGianDangXuat(taiKhoanHienTai.getId());
            server.ghiLog("Đã cập nhật thời gian đăng xuất cho " + tenDangNhap);
            
            server.xoaClientOnline(tenDangNhap);
            taiKhoanHienTai = null;
        } else {
            server.ghiLog("Yêu cầu đăng xuất từ client chưa đăng nhập");
        }
        
        phanhoi.setMaPhanhoi(ThongDiepTCP.THANH_CONG);
        return phanhoi;
    }
    
    /**
     * Cập nhật thời gian đăng xuất trong lịch sử
     */
    private void capNhatThoiGianDangXuat(int taiKhoanId) {
        String sql = "UPDATE lich_su_dang_nhap SET thoi_gian_dang_xuat = NOW() WHERE tai_khoan_id = ? AND thoi_gian_dang_xuat IS NULL ORDER BY thoi_gian_dang_nhap DESC LIMIT 1";
        try (PreparedStatement stmt = database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, taiKhoanId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Xử lý cập nhật thông tin
     */
    private ThongDiepTCP xuLyCapNhatThongTin(ThongDiepTCP yeuCau) {
        ThongDiepTCP phanhoi = new ThongDiepTCP();
        
        if (taiKhoanHienTai == null) {
            phanhoi.setMaPhanhoi(ThongDiepTCP.KHONG_CO_QUYEN);
            return phanhoi;
        }
        
        // Implement cập nhật thông tin
        // ... code cập nhật ...
        
        phanhoi.setMaPhanhoi(ThongDiepTCP.THANH_CONG);
        return phanhoi;
    }
    
    /**
     * Xử lý đổi mật khẩu
     */
    private ThongDiepTCP xuLyDoiMatKhau(ThongDiepTCP yeuCau) {
        ThongDiepTCP phanhoi = new ThongDiepTCP();
        
        if (taiKhoanHienTai == null) {
            phanhoi.setMaPhanhoi(ThongDiepTCP.KHONG_CO_QUYEN);
            return phanhoi;
        }
        
        // Implement đổi mật khẩu
        // ... code đổi mật khẩu ...
        
        phanhoi.setMaPhanhoi(ThongDiepTCP.THANH_CONG);
        return phanhoi;
    }
    
    /**
     * Xử lý lấy danh sách tài khoản (chỉ admin)
     */
    private ThongDiepTCP xuLyLayDanhSachTaiKhoan(ThongDiepTCP yeuCau) {
        ThongDiepTCP phanhoi = new ThongDiepTCP();
        
        if (taiKhoanHienTai == null || !taiKhoanHienTai.laAdmin()) {
            phanhoi.setMaPhanhoi(ThongDiepTCP.KHONG_CO_QUYEN);
            return phanhoi;
        }
        
        List<TaiKhoan> danhSach = database.layDanhSachTaiKhoan();
        Map<String, Object> duLieu = new HashMap<>();
        duLieu.put("danhSachTaiKhoan", danhSach);
        
        phanhoi.setMaPhanhoi(ThongDiepTCP.THANH_CONG);
        phanhoi.setDuLieu(duLieu);
        
        return phanhoi;
    }
    
    /**
     * Xử lý lấy thống kê dashboard (chỉ admin)
     */
    private ThongDiepTCP xuLyLayThongKeDashboard(ThongDiepTCP yeuCau) {
        ThongDiepTCP phanhoi = new ThongDiepTCP();
        
        if (taiKhoanHienTai == null || !taiKhoanHienTai.laAdmin()) {
            phanhoi.setMaPhanhoi(ThongDiepTCP.KHONG_CO_QUYEN);
            return phanhoi;
        }
        
        // Implement lấy thống kê
        Map<String, Object> thongKe = new HashMap<>();
        thongKe.put("tongTaiKhoan", database.demTongTaiKhoan());
        thongKe.put("taiKhoanOnline", server.getSoLuongClientOnline());
        thongKe.put("taiKhoanBiKhoa", database.demTaiKhoanBiKhoa());
        // ... thêm các thống kê khác ...
        
        phanhoi.setMaPhanhoi(ThongDiepTCP.THANH_CONG);
        phanhoi.setDuLieu(thongKe);
        
        return phanhoi;
    }
    
    /**
     * Xử lý quản lý tài khoản (chỉ admin)
     */
    private ThongDiepTCP xuLyQuanLyTaiKhoan(ThongDiepTCP yeuCau) {
        ThongDiepTCP phanhoi = new ThongDiepTCP();
        
        if (taiKhoanHienTai == null || !taiKhoanHienTai.laAdmin()) {
            phanhoi.setMaPhanhoi(ThongDiepTCP.KHONG_CO_QUYEN);
            return phanhoi;
        }
        
        // Implement các chức năng quản lý tài khoản
        // ... code quản lý ...
        
        phanhoi.setMaPhanhoi(ThongDiepTCP.THANH_CONG);
        return phanhoi;
    }
    
    /**
     * Đóng kết nối
     */
    private void dongKetNoi() {
        String clientInfo = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
        server.ghiLog("Đang đóng kết nối cho client " + clientInfo);
        
        try {
            if (taiKhoanHienTai != null) {
                String tenDangNhap = taiKhoanHienTai.getTenDangNhap();
                server.ghiLog("Client " + clientInfo + " đang đăng nhập với tài khoản: " + tenDangNhap);
                
                database.capNhatTrangThaiOnline(taiKhoanHienTai.getId(), false);
                server.ghiLog("Đã cập nhật trạng thái offline cho " + tenDangNhap);
                
                // Cập nhật thời gian đăng xuất khi đóng kết nối
                capNhatThoiGianDangXuat(taiKhoanHienTai.getId());
                server.ghiLog("Đã cập nhật thời gian đăng xuất cho " + tenDangNhap);
                
                server.xoaClientOnline(tenDangNhap);
                server.ghiLog(">>> Người dùng " + tenDangNhap + " đã đóng kết nối");
            } else {
                server.ghiLog("Client " + clientInfo + " đóng kết nối mà chưa đăng nhập");
            }
            
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (clientSocket != null) clientSocket.close();
            
        } catch (IOException e) {
            server.ghiLog("Lỗi đóng kết nối: " + e.getMessage());
        }
    }
    
}
