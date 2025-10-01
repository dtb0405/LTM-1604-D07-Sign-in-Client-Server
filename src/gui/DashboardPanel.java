package gui;

import client.KetNoiTCP;
import database.KetNoiDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.BasicStroke;
import java.util.Timer;

/**
 * Panel Dashboard hiển thị thống kê tổng quan
 */
public class DashboardPanel extends JPanel {
    @SuppressWarnings("unused")
    private KetNoiTCP ketNoi;
    
    private JLabel lblTongTaiKhoan;
    private JLabel lblTaiKhoanOnline;
    private JLabel lblTaiKhoanOffline;
    private JLabel lblTaiKhoanBiKhoa;
    private JLabel lblTaiKhoanHoatDong;
    private JLabel lblLuotDangNhapTrongNgay;
    
    private Timer timerCapNhat;
    
    public DashboardPanel(KetNoiTCP ketNoi) {
        this.ketNoi = ketNoi;
        
        khoiTaoGiaoDien();
        khoiTaoTimer();
        lamMoiDuLieu();
    }
    
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        
        // Tạo nền gradient đẹp cho toàn bộ panel
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient từ xanh nhạt đến trắng
                Color color1 = new Color(240, 248, 255); // Xanh rất nhạt
                Color color2 = new Color(255, 255, 255); // Trắng
                
                // Gradient theo chiều dọc
                for (int y = 0; y < getHeight(); y++) {
                    float ratio = (float) y / getHeight();
                    int r = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
                    int green = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
                    int b = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
                    g2d.setColor(new Color(r, green, b));
                    g2d.drawLine(0, y, getWidth(), y);
                }
                
                // Thêm hiệu ứng ánh sáng
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(-80, -80, 200, 200);
                g2d.fillOval(getWidth() - 120, getHeight() - 100, 180, 180);
            }
        };
        
        // Thêm tất cả component vào backgroundPanel
        add(backgroundPanel);
        
        // Panel tiêu đề
        JPanel panelTieuDe = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền trắng với bo góc
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Vẽ shadow
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(3, 3, getWidth(), getHeight(), 15, 15);
                
                // Vẽ border
                g2d.setColor(new Color(220, 220, 220));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        panelTieuDe.setOpaque(false);
        panelTieuDe.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel lblTieuDe = new JLabel("DASHBOARD - TỔNG QUAN HỆ THỐNG", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        lblTieuDe.setForeground(new Color(33, 150, 243));
        panelTieuDe.add(lblTieuDe);
        
        backgroundPanel.add(panelTieuDe, BorderLayout.NORTH);
        
        // Panel thống kê
        JPanel panelThongKe = taoPanelThongKe();
        backgroundPanel.add(panelThongKe, BorderLayout.CENTER);
    }
    
    private JPanel taoPanelThongKe() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);
        
        // Tổng số tài khoản - Tông lạnh xanh dương
        JPanel cardTongTaiKhoan = taoCardThongKe("TỔNG SỐ TÀI KHOẢN", "0", 
            new Color(30, 136, 229), "");
        lblTongTaiKhoan = (JLabel) ((JPanel) cardTongTaiKhoan.getComponent(1)).getComponent(0);
        panel.add(cardTongTaiKhoan);
        
        // Tài khoản online - Tông lạnh xanh lá
        JPanel cardOnline = taoCardThongKe("TÀI KHOẢN ONLINE", "0", 
            new Color(38, 166, 91), "");
        lblTaiKhoanOnline = (JLabel) ((JPanel) cardOnline.getComponent(1)).getComponent(0);
        panel.add(cardOnline);
        
        // Tài khoản offline - Tông lạnh xám xanh
        JPanel cardOffline = taoCardThongKe("TÀI KHOẢN OFFLINE", "0", 
            new Color(84, 110, 122), "");
        lblTaiKhoanOffline = (JLabel) ((JPanel) cardOffline.getComponent(1)).getComponent(0);
        panel.add(cardOffline);
        
        // Tài khoản bị khóa - Tông lạnh đỏ
        JPanel cardBiKhoa = taoCardThongKe("TÀI KHOẢN BỊ KHÓA", "0", 
            new Color(211, 47, 47), "");
        lblTaiKhoanBiKhoa = (JLabel) ((JPanel) cardBiKhoa.getComponent(1)).getComponent(0);
        panel.add(cardBiKhoa);
        
        // Tài khoản hoạt động - Tông lạnh cam
        JPanel cardHoatDong = taoCardThongKe("TÀI KHOẢN HOẠT ĐỘNG", "0", 
            new Color(255, 112, 67), "");
        lblTaiKhoanHoatDong = (JLabel) ((JPanel) cardHoatDong.getComponent(1)).getComponent(0);
        panel.add(cardHoatDong);
        
        // Lượt đăng nhập trong ngày - Tông lạnh tím
        JPanel cardDangNhapTrongNgay = taoCardThongKe("LƯỢT ĐĂNG NHẬP HÔM NAY", "0", 
            new Color(142, 36, 170), "");
        lblLuotDangNhapTrongNgay = (JLabel) ((JPanel) cardDangNhapTrongNgay.getComponent(1)).getComponent(0);
        panel.add(cardDangNhapTrongNgay);
        
        return panel;
    }
    
    private JPanel taoCardThongKe(String tieuDe, String giaTri, Color mauNen, String icon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(3, 3, getWidth(), getHeight(), 20, 20);
                
                // Vẽ background chính với bo góc mềm mại
                g2d.setColor(mauNen);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);
                
                // Vẽ border tinh tế
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(200, 120));
        
        // Panel tiêu đề - căn chính giữa
        JPanel panelTieuDe = new JPanel(new GridBagLayout());
        panelTieuDe.setOpaque(false);
        
        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 14));
        lblTieuDe.setForeground(Color.WHITE);
        lblTieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 10, 5, 10); // Thêm padding: top, left, bottom, right
        panelTieuDe.add(lblTieuDe, gbc);
        
        // Panel giá trị - căn chính giữa
        JPanel panelGiaTri = new JPanel(new GridBagLayout());
        panelGiaTri.setOpaque(false);
        
        JLabel lblGiaTri = new JLabel(giaTri);
        lblGiaTri.setFont(new Font("Arial", Font.BOLD, 36));
        lblGiaTri.setForeground(Color.WHITE);
        lblGiaTri.setHorizontalAlignment(SwingConstants.CENTER);
        
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.anchor = GridBagConstraints.CENTER;
        gbc2.insets = new Insets(5, 10, 15, 10); // Thêm padding: top, left, bottom, right
        panelGiaTri.add(lblGiaTri, gbc2);
        
        card.add(panelTieuDe, BorderLayout.NORTH);
        card.add(panelGiaTri, BorderLayout.CENTER);
        
        return card;
    }
    
    private void khoiTaoTimer() {
        // Không sử dụng timer tự động nữa
        // Dữ liệu sẽ được cập nhật khi cần thiết
    }
    
    public void lamMoiDuLieu() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int tongTaiKhoan = 0;
            private int taiKhoanOnline = 0;
            private int taiKhoanBiKhoa = 0;
            private int taiKhoanHoatDong = 0;
            private int luotDangNhapTrongNgay = 0;
            private boolean thanhCong = false;
            
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Lấy dữ liệu thực tế từ database
                    KetNoiDatabase db = KetNoiDatabase.getInstance();
                    if (db.getConnection() != null) {
                        // Lấy dữ liệu thực tế từ database
                        tongTaiKhoan = db.demTongTaiKhoan();
                        taiKhoanBiKhoa = db.demTaiKhoanBiKhoa();
                        taiKhoanHoatDong = db.demTaiKhoanHoatDong();
                        luotDangNhapTrongNgay = db.demLuotDangNhapTrongNgay();
                        
                        // Số người online thực tế từ database
                        taiKhoanOnline = db.demTaiKhoanOnline();
                        
                        thanhCong = true;
                    } else {
                        // Dữ liệu mặc định khi không có kết nối database
                        tongTaiKhoan = 0;
                        taiKhoanOnline = 0;
                        taiKhoanBiKhoa = 0;
                        taiKhoanHoatDong = 0;
                        luotDangNhapTrongNgay = 0;
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi lấy dữ liệu thống kê từ database: " + e.getMessage());
                    thanhCong = false;
                }
                
                return null;
            }
            
            @Override
            protected void done() {
                if (thanhCong) {
                    // Cập nhật giao diện với dữ liệu thực tế
                    lblTongTaiKhoan.setText(String.valueOf(tongTaiKhoan));
                    lblTaiKhoanOnline.setText(String.valueOf(taiKhoanOnline));
                    lblTaiKhoanOffline.setText(String.valueOf(tongTaiKhoan - taiKhoanOnline));
                    lblTaiKhoanBiKhoa.setText(String.valueOf(taiKhoanBiKhoa));
                    lblTaiKhoanHoatDong.setText(String.valueOf(taiKhoanHoatDong));
                    lblLuotDangNhapTrongNgay.setText(String.valueOf(luotDangNhapTrongNgay));
                    
                    // Thêm hiệu ứng cập nhật
                    for (JLabel label : new JLabel[]{lblTongTaiKhoan, lblTaiKhoanOnline, 
                            lblTaiKhoanOffline, lblTaiKhoanBiKhoa, lblTaiKhoanHoatDong, lblLuotDangNhapTrongNgay}) {
                        label.setForeground(Color.WHITE);
                        label.setFont(new Font("Arial", Font.BOLD, 32));
                    }
                } else {
                    // Hiển thị lỗi
                    lblTongTaiKhoan.setText("--");
                    lblTaiKhoanOnline.setText("--");
                    lblTaiKhoanOffline.setText("--");
                    lblTaiKhoanBiKhoa.setText("--");
                    lblTaiKhoanHoatDong.setText("--");
                    lblLuotDangNhapTrongNgay.setText("--");
                }
                
                repaint();
            }
        };
        
        worker.execute();
    }
    
    public void dungTimer() {
        if (timerCapNhat != null) {
            timerCapNhat.cancel();
        }
    }
}
