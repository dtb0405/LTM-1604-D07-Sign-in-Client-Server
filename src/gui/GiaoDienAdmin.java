package gui;

import client.KetNoiTCP;
import database.TaiKhoan;
import database.KetNoiDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
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
    
    private java.util.Timer timerLangNghe;
    
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
                
                // Gradient từ xanh nhạt đến trắng
                Color color1 = new Color(240, 248, 255); // Xanh rất nhạt
                Color color2 = new Color(255, 255, 255); // Trắng
                Color color3 = new Color(245, 250, 255); // Xanh cực nhạt
                
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
                g2d.setColor(new Color(200, 220, 240, 20));
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
                g2d.setColor(new Color(180, 200, 220, 12));
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
        
        // Khởi tạo timer lắng nghe thông báo
        khoiTaoTimerLangNghe();
    }
    
    /**
     * Khởi tạo timer lắng nghe thông báo từ server
     */
    private void khoiTaoTimerLangNghe() {
        if (timerLangNghe != null) {
            timerLangNghe.cancel();
        }
        
        timerLangNghe = new java.util.Timer("AdminMessageListener", true);
        timerLangNghe.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    kiemTraThongBaoTuServer();
                });
            }
        }, 0, 2000); // Kiểm tra mỗi 2 giây
    }
    
    /**
     * Kiểm tra thông báo từ server
     */
    private void kiemTraThongBaoTuServer() {
        try {
            // Kiểm tra trạng thái tài khoản trong database
            if (taiKhoanAdmin != null) {
                database.KetNoiDatabase db = database.KetNoiDatabase.getInstance();
                database.TaiKhoan taiKhoanMoi = db.timTaiKhoanTheoTen(taiKhoanAdmin.getTenDangNhap());
                
                if (taiKhoanMoi != null && !taiKhoanMoi.taiKhoanHoatDong()) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, 
                            "Tài khoản của bạn đã bị khóa bởi quản trị viên khác.\n" +
                            "Bạn sẽ bị đăng xuất khỏi hệ thống.",
                            "Tài khoản bị khóa", JOptionPane.WARNING_MESSAGE);
                        
                        // Đóng giao diện và quay về màn hình đăng nhập
                        thucHienDangXuat(false);
                    });
                }
            }
        } catch (Exception e) {
            // Không hiển thị lỗi để tránh spam
            System.err.println("Lỗi kiểm tra trạng thái tài khoản: " + e.getMessage());
        }
    }
    
    /**
     * Dừng timer lắng nghe
     */
    private void dungTimerLangNghe() {
        if (timerLangNghe != null) {
            timerLangNghe.cancel();
            timerLangNghe = null;
        }
    }
    
    /**
     * Thực hiện đăng xuất
     */
    private void thucHienDangXuat(boolean hienThiThongBao) {
        // Dừng timer
        dungTimerLangNghe();
        
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
            } catch (Exception e) {
                System.err.println("Lỗi cập nhật database: " + e.getMessage());
            }
        }
        
        if (hienThiThongBao && dangXuatThanhCong) {
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
        
        // Panel thông tin admin với ảnh đại diện
        JPanel panelThongTin = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        panelThongTin.setOpaque(false);
        
        // Ảnh đại diện admin
        JLabel lblAvatar = taoAnhDaiDienAdmin();
        panelThongTin.add(lblAvatar);
        
        JLabel lblAdmin = new JLabel("Quản trị viên: " + taiKhoanAdmin.getHoTen());
        lblAdmin.setFont(new Font("Arial", Font.BOLD, 16));
        lblAdmin.setForeground(new Color(55, 71, 79)); // Xám đậm
        panelThongTin.add(lblAdmin);
        
        panel.add(lblTieuDe, BorderLayout.CENTER);
        panel.add(panelThongTin, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Nút làm mới dữ liệu với tông màu lạnh
        JButton btnLamMoi = ButtonUtils.createCoolButton("Làm Mới");
        btnLamMoi.addActionListener(e -> lamMoiDuLieu());
        panel.add(btnLamMoi);
        
        // Nút đăng xuất với tông màu lạnh
        JButton btnDangXuat = ButtonUtils.createRedCoolButton("Đăng Xuất");
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
            thucHienDangXuat(hienThiDialog);
        }
    }
    
    /**
     * Tạo ảnh đại diện admin với hiệu ứng đẹp
     */
    private JLabel taoAnhDaiDienAdmin() {
        JLabel lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(60, 60));
        lblAvatar.setBorder(null); // Bỏ viền vuông
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatar.setVerticalAlignment(SwingConstants.CENTER);
        
        // Hiệu ứng hover
        lblAvatar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                lblAvatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                lblAvatar.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                moQuanLyAnhDaiDienAdmin();
            }
        });
        
        // Load ảnh đại diện hiện tại
        taiAnhDaiDienAdmin(lblAvatar);
        
        return lblAvatar;
    }
    
    /**
     * Tải ảnh đại diện admin hiện tại
     */
    private void taiAnhDaiDienAdmin(JLabel lblAvatar) {
        try {
            database.KetNoiDatabase db = database.KetNoiDatabase.getInstance();
            String avatarPath = db.layAnhDaiDien(taiKhoanAdmin.getId());
            
            if (avatarPath != null && !avatarPath.isEmpty() && !avatarPath.equals("default_avatar.png")) {
                java.io.File avatarFile = new java.io.File(avatarPath);
                if (avatarFile.exists()) {
                    java.awt.image.BufferedImage avatarImage = javax.imageio.ImageIO.read(avatarFile);
                    BufferedImage croppedImage = cropToSquare(avatarImage, 60);
                    ImageIcon icon = new ImageIcon(croppedImage);
                    lblAvatar.setIcon(icon);
                    lblAvatar.setText("");
                    lblAvatar.repaint(); // Force repaint
                } else {
                    hienThiAnhMacDinhAdmin(lblAvatar);
                }
            } else {
                hienThiAnhMacDinhAdmin(lblAvatar);
            }
        } catch (Exception e) {
            hienThiAnhMacDinhAdmin(lblAvatar);
        }
    }
    
    /**
     * Hiển thị ảnh mặc định cho admin
     */
    private void hienThiAnhMacDinhAdmin(JLabel lblAvatar) {
        // Tạo ảnh mặc định với icon admin
        BufferedImage defaultImage = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = defaultImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Vẽ nền tròn
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillOval(5, 5, 50, 50);
        
        // Vẽ chữ cái đầu của tên đăng nhập admin
        g2d.setColor(new Color(100, 100, 100));
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "A"; // Default fallback
        if (taiKhoanAdmin != null && taiKhoanAdmin.getTenDangNhap() != null && !taiKhoanAdmin.getTenDangNhap().isEmpty()) {
            text = taiKhoanAdmin.getTenDangNhap().substring(0, 1).toUpperCase();
        }
        int x = (60 - fm.stringWidth(text)) / 2;
        int y = (60 - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        
        ImageIcon icon = new ImageIcon(defaultImage);
        lblAvatar.setIcon(icon);
        lblAvatar.setText("");
        lblAvatar.repaint(); // Force repaint
    }
    
    /**
     * Cắt ảnh thành hình vuông từ giữa
     */
    private BufferedImage cropToSquare(BufferedImage originalImage, int size) {
        if (originalImage == null) {
            return null;
        }
        
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // Tạo ảnh vuông mới với nền trong suốt
        BufferedImage squareImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = squareImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        
        // Tính toán vùng cắt thông minh
        int cropSize;
        int startX, startY;
        
        if (originalWidth > originalHeight) {
            // Ảnh ngang - cắt theo chiều rộng, giữ chiều cao
            cropSize = originalHeight;
            startX = (originalWidth - cropSize) / 2; // Cắt ở giữa
            startY = 0;
        } else if (originalHeight > originalWidth) {
            // Ảnh dọc - cắt theo chiều cao, giữ chiều rộng
            cropSize = originalWidth;
            startX = 0;
            startY = (originalHeight - cropSize) / 2; // Cắt ở giữa
        } else {
            // Ảnh vuông - giữ nguyên
            cropSize = Math.min(originalWidth, originalHeight);
            startX = 0;
            startY = 0;
        }
        
        // Vẽ ảnh đã cắt và resize để vừa khung vuông
        g2d.drawImage(originalImage, 0, 0, size, size, 
                     startX, startY, startX + cropSize, startY + cropSize, null);
        
        g2d.dispose();
        return squareImage;
    }
    
    /**
     * Getter cho tài khoản admin
     */
    public database.TaiKhoan getTaiKhoanAdmin() {
        return taiKhoanAdmin;
    }
    
    /**
     * Cập nhật ảnh đại diện admin trong giao diện
     */
    private void capNhatAnhDaiDienAdmin() {
        // Tìm và cập nhật ảnh đại diện trong header panel
        JPanel backgroundPanel = (JPanel) getContentPane().getComponent(0);
        JPanel headerPanel = (JPanel) backgroundPanel.getComponent(0);
        
        // Tìm JLabel avatar trong header panel (tìm label có kích thước 60x60)
        for (Component comp : headerPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel subPanel = (JPanel) comp;
                for (Component subComp : subPanel.getComponents()) {
                    if (subComp instanceof JLabel) {
                        JLabel label = (JLabel) subComp;
                        Dimension size = label.getPreferredSize();
                        if (size != null && size.width == 60 && size.height == 60) {
                            // Đây là avatar label
                            taiAnhDaiDienAdmin(label);
                            label.repaint(); // Force repaint
                            return;
                        }
                    }
                }
            }
        }
        
        // Force repaint toàn bộ header panel
        headerPanel.repaint();
    }
    
    /**
     * Mở dialog quản lý ảnh đại diện admin
     */
    private void moQuanLyAnhDaiDienAdmin() {
        try {
            database.KetNoiDatabase db = database.KetNoiDatabase.getInstance();
            String currentAvatarPath = db.layAnhDaiDien(taiKhoanAdmin.getId());
            
            AvatarUploadDialog dialog = new AvatarUploadDialog(this, currentAvatarPath, () -> {
                // Callback khi ảnh được thay đổi
                capNhatAnhDaiDienAdmin();
            });
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi mở quản lý ảnh đại diện: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
