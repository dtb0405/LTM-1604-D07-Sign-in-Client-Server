package protocol;

import java.io.Serializable;
import java.util.Map;

/**
 * Lớp định nghĩa thông điệp TCP giữa client và server
 */
public class ThongDiepTCP implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Các loại yêu cầu
    public static final String DANG_NHAP = "DANG_NHAP";
    public static final String DANG_KY = "DANG_KY";
    public static final String DANG_XUAT = "DANG_XUAT";
    public static final String CAP_NHAT_THONG_TIN = "CAP_NHAT_THONG_TIN";
    public static final String DOI_MAT_KHAU = "DOI_MAT_KHAU";
    public static final String LAY_DANH_SACH_TAI_KHOAN = "LAY_DANH_SACH_TAI_KHOAN";
    public static final String KHOA_TAI_KHOAN = "KHOA_TAI_KHOAN";
    public static final String MO_KHOA_TAI_KHOAN = "MO_KHOA_TAI_KHOAN";
    public static final String XOA_TAI_KHOAN = "XOA_TAI_KHOAN";
    public static final String CAP_NHAT_VAI_TRO = "CAP_NHAT_VAI_TRO";
    public static final String LAY_THONG_KE_DASHBOARD = "LAY_THONG_KE_DASHBOARD";
    public static final String LAY_LICH_SU_DANG_NHAP = "LAY_LICH_SU_DANG_NHAP";
    
    // Các mã phản hồi
    public static final String THANH_CONG = "THANH_CONG";
    public static final String THAT_BAI = "THAT_BAI";
    public static final String LOI_HE_THONG = "LOI_HE_THONG";
    public static final String KHONG_CO_QUYEN = "KHONG_CO_QUYEN";
    public static final String TAI_KHOAN_DA_TON_TAI = "TAI_KHOAN_DA_TON_TAI";
    public static final String TAI_KHOAN_KHONG_TON_TAI = "TAI_KHOAN_KHONG_TON_TAI";
    public static final String SAI_MAT_KHAU = "SAI_MAT_KHAU";
    public static final String TAI_KHOAN_BI_KHOA = "TAI_KHOAN_BI_KHOA";
    
    private String loaiYeuCau;
    private String maPhanhoi;
    private Map<String, Object> duLieu;
    private String thongBaoLoi;
    
    // Constructor
    public ThongDiepTCP() {}
    
    public ThongDiepTCP(String loaiYeuCau) {
        this.loaiYeuCau = loaiYeuCau;
    }
    
    public ThongDiepTCP(String loaiYeuCau, Map<String, Object> duLieu) {
        this.loaiYeuCau = loaiYeuCau;
        this.duLieu = duLieu;
    }
    
    // Getters and Setters
    public String getLoaiYeuCau() {
        return loaiYeuCau;
    }
    
    public void setLoaiYeuCau(String loaiYeuCau) {
        this.loaiYeuCau = loaiYeuCau;
    }
    
    public String getMaPhanhoi() {
        return maPhanhoi;
    }
    
    public void setMaPhanhoi(String maPhanhoi) {
        this.maPhanhoi = maPhanhoi;
    }
    
    public Map<String, Object> getDuLieu() {
        return duLieu;
    }
    
    public void setDuLieu(Map<String, Object> duLieu) {
        this.duLieu = duLieu;
    }
    
    public String getThongBaoLoi() {
        return thongBaoLoi;
    }
    
    public void setThongBaoLoi(String thongBaoLoi) {
        this.thongBaoLoi = thongBaoLoi;
    }
    
    // Các phương thức tiện ích
    public Object layDuLieu(String key) {
        return duLieu != null ? duLieu.get(key) : null;
    }
    
    public void themDuLieu(String key, Object value) {
        if (duLieu == null) {
            duLieu = new java.util.HashMap<>();
        }
        duLieu.put(key, value);
    }
    
    public boolean laThanhCong() {
        return THANH_CONG.equals(maPhanhoi);
    }
    
    public boolean laThatBai() {
        return THAT_BAI.equals(maPhanhoi) || LOI_HE_THONG.equals(maPhanhoi);
    }
    
    @Override
    public String toString() {
        return "ThongDiepTCP{" +
                "loaiYeuCau='" + loaiYeuCau + '\'' +
                ", maPhanhoi='" + maPhanhoi + '\'' +
                ", duLieu=" + duLieu +
                ", thongBaoLoi='" + thongBaoLoi + '\'' +
                '}';
    }
}


