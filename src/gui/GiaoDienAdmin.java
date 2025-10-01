package gui;

import client.KetNoiTCP;
import database.TaiKhoan;
import database.KetNoiDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.BasicStroke;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Giao diện quản trị viên
 */
public class GiaoDienAdmin extends JFrame {
    private TaiKhoan taiKhoanAdmin;
    private KetNoiTCP ketNoi;
    
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private QuanLyTaiKhoanPanel quanLyTaiKhoanPanel;
    private LichSuDangNhapPanel lichSuPanel;
    
    public GiaoDienAdmin(TaiKhoan taiKhoan, KetNoiTCP ketNoi) {
        this.taiKhoanAdmin = taiKhoan;
        this.ketNoi = ketNoi;
        
        khoiTaoGiaoDien();
    }
    
    private void khoiTaoGiaoDien() {
        setTitle("Trang Quản Trị - " + taiKhoanAdmin.getHoTen());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Không thiết lập màu nền cố định để hoa văn hiển thị
        
        // Tạo nền gradient cho toàn bộ frame
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient từ tím nhạt đến trắng
                Color color1 = new Color(248, 240, 255); // Tím rất nhạt
                Color color2 = new Color(255, 255, 255); // Trắng
                Color color3 = new Color(245, 240, 250); // Tím cực nhạt
                
                // Gradient theo chiều chéo
                for (int y = 0; y < getHeight(); y++) {
                    for (int x = 0; x < getWidth(); x++) {
                        float ratio = (float) (x + y) / (getWidth() + getHeight());
                        int r = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
                        int green = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
                        int b = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
                        g2d.setColor(new Color(r, green, b));
                        g2d.drawLine(x, y, x, y);
                    }
                }
                
                // Thêm hiệu ứng ánh sáng
                g2d.setColor(new Color(255, 255, 255, 25));
                g2d.fillOval(-80, -80, 250, 250);
                g2d.fillOval(getWidth() - 180, getHeight() - 120, 200, 200);
                
                // Thêm hoa văn nền chìm cho admin
                g2d.setColor(new Color(220, 200, 240, 25));
                g2d.setStroke(new BasicStroke(1));
                
                // Vẽ các hình lục giác nhẹ
                for (int i = 0; i < 6; i++) {
                    int centerX = (getWidth() / 6) * i + 50;
                    int centerY = getHeight() / 2;
                    int radius = 40;
                    
                    int[] xPoints = new int[6];
                    int[] yPoints = new int[6];
                    for (int j = 0; j < 6; j++) {
                        double angle = Math.PI / 3 * j;
                        xPoints[j] = centerX + (int) (radius * Math.cos(angle));
                        yPoints[j] = centerY + (int) (radius * Math.sin(angle));
                    }
                    g2d.drawPolygon(xPoints, yPoints, 6);
                }
                
                // Vẽ các đường sóng
                g2d.setColor(new Color(200, 180, 220, 15));
                for (int i = 0; i < 3; i++) {
                    int startY = (getHeight() / 3) * i + 50;
                    for (int x = 0; x < getWidth(); x += 5) {
                        int y = startY + (int) (Math.sin(x * 0.05) * 20);
                        g2d.drawLine(x, y, x + 5, y + (int) (Math.sin((x + 5) * 0.05) * 20));
                    }
                }
                
                // Vẽ các hình vuông xoay
                g2d.setColor(new Color(190, 170, 210, 12));
                for (int i = 0; i < 8; i++) {
                    int x = (getWidth() / 8) * i;
                    int y = (int) (Math.sin(i * 0.8) * 100 + getHeight() / 2);
                    g2d.rotate(Math.PI / 4, x + 25, y + 25);
                    g2d.drawRect(x, y, 50, 50);
                    g2d.rotate(-Math.PI / 4, x + 25, y + 25);
                }
                
                // Vẽ các chấm tròn nhỏ
                g2d.setColor(new Color(180, 160, 200, 10));
                for (int i = 0; i < 20; i++) {
                    int x = (int) (Math.random() * getWidth());
                    int y = (int) (Math.random() * getHeight());
                    int size = (int) (Math.random() * 15 + 5);
                    g2d.fillOval(x, y, size, size);
                }
            }
        };
        
        // Thêm tất cả component vào backgroundPanel
        add(backgroundPanel);
        
        // Panel tiêu đề
        JPanel panelTieuDe = taoPanelTieuDe();
        backgroundPanel.add(panelTieuDe, BorderLayout.NORTH);
        
        // Tạo tabbed pane với nền trong suốt
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setOpaque(false);
        tabbedPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        // Tab Dashboard
        dashboardPanel = new DashboardPanel(ketNoi);
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.setToolTipTextAt(0, "Tổng quan hệ thống");
        
        // Tab Quản lý tài khoản
        quanLyTaiKhoanPanel = new QuanLyTaiKhoanPanel(ketNoi);
        tabbedPane.addTab("Quản Lý Tài Khoản", quanLyTaiKhoanPanel);
        tabbedPane.setToolTipTextAt(1, "Quản lý người dùng");
        
        // Tab Lịch sử đăng nhập
        lichSuPanel = new LichSuDangNhapPanel(ketNoi);
        tabbedPane.addTab("Lịch Sử Đăng Nhập", lichSuPanel);
        tabbedPane.setToolTipTextAt(2, "Xem lịch sử đăng nhập");
        
        backgroundPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Panel nút với nền trong suốt
        JPanel panelNut = taoPanelNut();
        panelNut.setOpaque(false);
        backgroundPanel.add(panelNut, BorderLayout.SOUTH);
        
        // Xử lý sự kiện đóng cửa sổ
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dangXuat();
            }
            
            @Override
            public void windowClosed(WindowEvent e) {
                // Đảm bảo đăng xuất khi cửa sổ đã đóng hoàn toàn
                // (fallback nếu windowClosing không được gọi)
            }
        });
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
    
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout()) {
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
                
                // Thêm hoa văn nền chìm cho header
                g2d.setColor(new Color(200, 220, 240, 25));
                g2d.setStroke(new BasicStroke(1));
                
                // Vẽ các hình lục giác nhẹ
                for (int i = 0; i < 4; i++) {
                    int centerX = (getWidth() / 4) * i + 50;
                    int centerY = getHeight() / 2;
                    int radius = 30;
                    
                    int[] xPoints = new int[6];
                    int[] yPoints = new int[6];
                    for (int j = 0; j < 6; j++) {
                        double angle = Math.PI / 3 * j;
                        xPoints[j] = centerX + (int) (radius * Math.cos(angle));
                        yPoints[j] = centerY + (int) (radius * Math.sin(angle));
                    }
                    g2d.drawPolygon(xPoints, yPoints, 6);
                }
                
                // Vẽ các đường sóng
                g2d.setColor(new Color(180, 200, 220, 15));
                for (int i = 0; i < 2; i++) {
                    int startY = (getHeight() / 2) * i + 30;
                    for (int x = 0; x < getWidth(); x += 5) {
                        int y = startY + (int) (Math.sin(x * 0.05) * 15);
                        g2d.drawLine(x, y, x + 5, y + (int) (Math.sin((x + 5) * 0.05) * 15));
                    }
                }
                
                // Vẽ border bo góc
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JLabel lblTieuDe = new JLabel("TRANG QUẢN TRỊ HỆ THỐNG", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 30));
        lblTieuDe.setForeground(new Color(25, 118, 210)); // Xanh dương đậm
        
        JLabel lblAdmin = new JLabel("Quản trị viên: " + taiKhoanAdmin.getHoTen(), SwingConstants.RIGHT);
        lblAdmin.setFont(new Font("Arial", Font.BOLD, 16));
        lblAdmin.setForeground(new Color(55, 71, 79)); // Xám đậm
        
        panel.add(lblTieuDe, BorderLayout.CENTER);
        panel.add(lblAdmin, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Nút làm mới dữ liệu
        JButton btnLamMoi = taoNutHienDai("Làm Mới", new Color(33, 150, 243));
        btnLamMoi.addActionListener(e -> lamMoiDuLieu());
        panel.add(btnLamMoi);
        
        JButton btnDangXuat = taoNutHienDai("Đăng Xuất", new Color(244, 67, 54));
        btnDangXuat.addActionListener(e -> dangXuat(true)); // Hiển thị dialog khi click nút
        panel.add(btnDangXuat);
        
        return panel;
    }
    
    private JButton taoNutHienDai(String text, Color mauNen) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(3, 3, getWidth(), getHeight(), 25, 25);
                
                // Vẽ background chính
                g2d.setColor(mauNen);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 25, 25);
                
                // Vẽ border
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 25, 25);
                
                // Vẽ text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight / 2) / 2;
                g2d.drawString(text, x, y);
            }
        };
        
        button.setPreferredSize(new Dimension(150, 50));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }
    
    private void lamMoiDuLieu() {
        // Làm mới dữ liệu trên tất cả các tab
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (dashboardPanel != null) {
                    dashboardPanel.lamMoiDuLieu();
                }
                if (quanLyTaiKhoanPanel != null) {
                    quanLyTaiKhoanPanel.lamMoiDuLieu();
                }
                if (lichSuPanel != null) {
                    lichSuPanel.lamMoiDuLieu();
                }
                return null;
            }
            
            @Override
            protected void done() {
                JOptionPane.showMessageDialog(GiaoDienAdmin.this, 
                    "Đã làm mới dữ liệu thành công!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        
        worker.execute();
    }
    
    private void dangXuat() {
        dangXuat(false);
    }
    
    private void dangXuat(boolean hienThiDialog) {
        boolean thucHienDangXuat = true;
        
        if (hienThiDialog) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            thucHienDangXuat = (confirm == JOptionPane.YES_OPTION);
        }
        
        if (thucHienDangXuat) {
            // Gửi yêu cầu đăng xuất tới server
            boolean dangXuatThanhCong = false;
            try {
                dangXuatThanhCong = ketNoi.dangXuat();
            } catch (Exception e) {
                System.err.println("Lỗi khi đăng xuất: " + e.getMessage());
            }
            
            // Nếu không thể gửi yêu cầu tới server, cập nhật trực tiếp database
            if (!dangXuatThanhCong && taiKhoanAdmin != null) {
                try {
                    KetNoiDatabase db = KetNoiDatabase.getInstance();
                    db.capNhatTrangThaiOnline(taiKhoanAdmin.getId(), false);
                    // Cập nhật thời gian đăng xuất
                    String sql = "UPDATE lich_su_dang_nhap SET thoi_gian_dang_xuat = NOW() WHERE tai_khoan_id = ? AND thoi_gian_dang_xuat IS NULL ORDER BY thoi_gian_dang_nhap DESC LIMIT 1";
                    try (java.sql.PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                        stmt.setInt(1, taiKhoanAdmin.getId());
                        stmt.executeUpdate();
                    }
                    System.out.println("Đã cập nhật trạng thái đăng xuất trực tiếp vào database");
                } catch (Exception e) {
                    System.err.println("Lỗi cập nhật database: " + e.getMessage());
                }
            }
            
            if (hienThiDialog && dangXuatThanhCong) {
                JOptionPane.showMessageDialog(this, 
                    "Đã đăng xuất thành công", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            
            // Đóng kết nối và mở lại màn hình đăng nhập
            ketNoi.ngatketnoi();
            dispose();
            
            SwingUtilities.invokeLater(() -> {
                new ManHinhDangNhap().setVisible(true);
            });
        }
    }
    
}
