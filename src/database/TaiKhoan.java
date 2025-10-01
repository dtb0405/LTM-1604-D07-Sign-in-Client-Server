package database;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Lớp model cho tài khoản người dùng
 */
public class TaiKhoan implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private Date ngaySinh;
    private String vaiTro;
    private String trangThai;
    private String trangThaiOnline;
    private Timestamp ngayTao;
    private Timestamp lanDangNhapCuoi;
    
    // Constructor
    public TaiKhoan() {}
    
    public TaiKhoan(String tenDangNhap, String matKhau, String hoTen) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTenDangNhap() {
        return tenDangNhap;
    }
    
    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }
    
    public String getMatKhau() {
        return matKhau;
    }
    
    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
    
    public String getHoTen() {
        return hoTen;
    }
    
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSoDienThoai() {
        return soDienThoai;
    }
    
    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
    
    public Date getNgaySinh() {
        return ngaySinh;
    }
    
    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
    
    public String getVaiTro() {
        return vaiTro;
    }
    
    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    public String getTrangThaiOnline() {
        return trangThaiOnline;
    }
    
    public void setTrangThaiOnline(String trangThaiOnline) {
        this.trangThaiOnline = trangThaiOnline;
    }
    
    public Timestamp getNgayTao() {
        return ngayTao;
    }
    
    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }
    
    public Timestamp getLanDangNhapCuoi() {
        return lanDangNhapCuoi;
    }
    
    public void setLanDangNhapCuoi(Timestamp lanDangNhapCuoi) {
        this.lanDangNhapCuoi = lanDangNhapCuoi;
    }
    
    public boolean laAdmin() {
        return "admin".equals(vaiTro);
    }
    
    public boolean dangOnline() {
        return "online".equals(trangThaiOnline);
    }
    
    public boolean isOnline() {
        return "online".equals(trangThaiOnline);
    }
    
    public boolean isBiKhoa() {
        return "bi_khoa".equals(trangThai);
    }
    
    public boolean taiKhoanHoatDong() {
        return "hoat_dong".equals(trangThai);
    }
    
    @Override
    public String toString() {
        return "TaiKhoan{" +
                "id=" + id +
                ", tenDangNhap='" + tenDangNhap + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", vaiTro='" + vaiTro + '\'' +
                ", trangThai='" + trangThai + '\'' +
                ", trangThaiOnline='" + trangThaiOnline + '\'' +
                '}';
    }
}
