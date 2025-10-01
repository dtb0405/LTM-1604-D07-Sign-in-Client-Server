package client;

import protocol.ThongDiepTCP;
import database.TaiKhoan;

import java.io.*;
import java.net.Socket;
import java.net.ConnectException;

/**
 * Lớp kết nối TCP client
 */
public class KetNoiTCP {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String diaChiServer;
    private int congServer;
    private boolean daKetNoi = false;
    
    public KetNoiTCP(String diaChiServer, int congServer) {
        this.diaChiServer = diaChiServer;
        this.congServer = congServer;
    }
    
    /**
     * Kết nối tới server
     */
    public boolean ketNoi() {
        try {
            socket = new Socket(diaChiServer, congServer);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            daKetNoi = true;
            
            System.out.println("Connected to server: " + diaChiServer + ":" + congServer);
            return true;
            
        } catch (ConnectException e) {
            System.err.println("Cannot connect to server. Please check if server is running.");
            return false;
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ngắt kết nối
     */
    public void ngatketnoi() {
        try {
            daKetNoi = false;
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
            
            System.out.println("Đã ngắt kết nối khỏi server");
            
        } catch (IOException e) {
            System.err.println("Lỗi khi ngắt kết nối: " + e.getMessage());
        }
    }
    
    /**
     * Gửi yêu cầu tới server
     */
    public ThongDiepTCP guiYeuCau(ThongDiepTCP yeuCau) {
        if (!daKetNoi) {
            System.err.println("Not connected to server");
            return null;
        }
        
        try {
            // Gửi yêu cầu
            oos.writeObject(yeuCau);
            oos.flush();
            
            // Nhận phản hồi
            ThongDiepTCP phanhoi = (ThongDiepTCP) ois.readObject();
            return phanhoi;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error sending/receiving data: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Đăng nhập
     */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        ThongDiepTCP yeuCau = new ThongDiepTCP(ThongDiepTCP.DANG_NHAP);
        yeuCau.themDuLieu("tenDangNhap", tenDangNhap);
        yeuCau.themDuLieu("matKhau", matKhau);
        
        ThongDiepTCP phanhoi = guiYeuCau(yeuCau);
        
        if (phanhoi != null && phanhoi.laThanhCong()) {
            return (TaiKhoan) phanhoi.layDuLieu("taiKhoan");
        }
        
        return null;
    }
    
    /**
     * Đăng ký
     */
    public boolean dangKy(String tenDangNhap, String matKhau, String hoTen) {
        ThongDiepTCP yeuCau = new ThongDiepTCP(ThongDiepTCP.DANG_KY);
        yeuCau.themDuLieu("tenDangNhap", tenDangNhap);
        yeuCau.themDuLieu("matKhau", matKhau);
        yeuCau.themDuLieu("hoTen", hoTen);
        
        ThongDiepTCP phanhoi = guiYeuCau(yeuCau);
        
        return phanhoi != null && phanhoi.laThanhCong();
    }
    
    /**
     * Đăng xuất
     */
    public boolean dangXuat() {
        ThongDiepTCP yeuCau = new ThongDiepTCP(ThongDiepTCP.DANG_XUAT);
        ThongDiepTCP phanhoi = guiYeuCau(yeuCau);
        
        return phanhoi != null && phanhoi.laThanhCong();
    }
    
    /**
     * Kiểm tra kết nối
     */
    public boolean isDaKetNoi() {
        return daKetNoi && socket != null && !socket.isClosed();
    }
    
    /**
     * Lấy thông tin server
     */
    public String getThongTinServer() {
        return diaChiServer + ":" + congServer;
    }
    
}


