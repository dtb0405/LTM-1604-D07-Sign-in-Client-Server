package gui;

import client.KetNoiTCP;
import database.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;

/**
 * Giao diện người dùng thường
 */
public class GiaoDienUser extends JFrame {
    private TaiKhoan taiKhoanHienTai;
    private KetNoiTCP ketNoi;
    
    private JLabel lblChaoMung;
    
    
    private Timer timerLangNghe;
    
    public GiaoDienUser(TaiKhoan taiKhoan, KetNoiTCP ketNoi) {
        this.taiKhoanHienTai = taiKhoan;
        this.ketNoi = ketNoi;
        
        khoiTaoGiaoDien();
        capNhatThongTinTaiKhoan();
    }
    
    private void khoiTaoGiaoDien() {
        setTitle("Trang Người Dùng - " + taiKhoanHienTai.getHoTen());
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
                
                // Gradient từ tím nhạt đến hồng nhạt
                Color color1 = new Color(255, 240, 245); // Hồng rất nhạt
                Color color2 = new Color(248, 240, 255); // Tím rất nhạt
                
                // Gradient theo chiều dọc
                for (int y = 0; y < getHeight(); y++) {
                    float ratio = (float) y / getHeight();
                    int r = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
                    int green = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
                    int b = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
                    g2d.setColor(new Color(r, green, b));
                    g2d.drawLine(0, y, getWidth(), y);
                }
                
                // Thêm hiệu ứng ánh sáng màu sắc
                g2d.setColor(new Color(255, 182, 193, 40)); // Hồng nhạt
                g2d.fillOval(-100, -100, 300, 300);
                g2d.setColor(new Color(221, 160, 221, 35)); // Tím nhạt
                g2d.fillOval(getWidth() - 200, getHeight() - 150, 250, 250);
                g2d.setColor(new Color(255, 218, 185, 25)); // Cam nhạt
                g2d.fillOval(getWidth() / 2 - 100, getHeight() / 2 - 100, 200, 200);
                
                // Thêm hoa văn nền chìm màu sắc
                g2d.setColor(new Color(255, 192, 203, 25)); // Hồng nhạt
                g2d.setStroke(new BasicStroke(2));
                
                // Vẽ các đường cong nhẹ nhàng
                for (int i = 0; i < 8; i++) {
                    int x = (getWidth() / 8) * i;
                    int y = (int) (Math.sin(i * 0.5) * 50 + getHeight() / 2);
                    g2d.drawLine(x, 0, x + 100, y);
                    g2d.drawLine(x, getHeight(), x + 100, y);
                }
                
                // Vẽ các hình tròn nhỏ màu sắc
                Color[] colors = {
                    new Color(255, 182, 193, 20), // Hồng
                    new Color(221, 160, 221, 18), // Tím
                    new Color(255, 218, 185, 16), // Cam
                    new Color(173, 216, 230, 14)  // Xanh nhạt
                };
                for (int i = 0; i < 20; i++) {
                    g2d.setColor(colors[i % colors.length]);
                    int x = (int) (Math.random() * getWidth());
                    int y = (int) (Math.random() * getHeight());
                    int size = (int) (Math.random() * 40 + 15);
                    g2d.fillOval(x, y, size, size);
                }
                
                // Vẽ các đường zigzag nhẹ màu sắc
                Color[] zigzagColors = {
                    new Color(255, 192, 203, 12), // Hồng
                    new Color(221, 160, 221, 10), // Tím
                    new Color(255, 218, 185, 8)   // Cam
                };
                for (int i = 0; i < 5; i++) {
                    g2d.setColor(zigzagColors[i % zigzagColors.length]);
                    int startY = (getHeight() / 5) * i;
                    for (int x = 0; x < getWidth(); x += 20) {
                        int y = startY + (int) (Math.sin(x * 0.1) * 15);
                        g2d.drawLine(x, y, x + 20, y + (int) (Math.sin((x + 20) * 0.1) * 15));
                    }
                }
            }
        };
        
        // Thêm tất cả component vào backgroundPanel thay vì trực tiếp vào frame
        add(backgroundPanel);
        
        // Panel navigation với nền trong suốt
        JPanel panelNavigation = taoPanelNavigation();
        panelNavigation.setOpaque(false);
        backgroundPanel.add(panelNavigation, BorderLayout.NORTH);
        
        // Panel chính với lời chào với nền trong suốt
        JPanel panelChinh = taoPanelChinhMoi();
        panelChinh.setOpaque(false);
        backgroundPanel.add(panelChinh, BorderLayout.CENTER);
        
        // Panel nút đăng xuất với nền trong suốt
        JPanel panelNut = taoPanelNutMoi();
        panelNut.setOpaque(false);
        backgroundPanel.add(panelNut, BorderLayout.SOUTH);
        
        // Xử lý sự kiện đóng cửa sổ
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dungTimerLangNghe();
                dangXuatKhiDongCuaSo();
            }
            
            @Override
            public void windowClosed(WindowEvent e) {
                // Đảm bảo đăng xuất khi cửa sổ đã đóng hoàn toàn
                dungTimerLangNghe();
            }
        });
        
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(600, 500));
        
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
        
        timerLangNghe = new Timer("UserMessageListener", true);
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
            if (taiKhoanHienTai != null) {
                database.KetNoiDatabase db = database.KetNoiDatabase.getInstance();
                database.TaiKhoan taiKhoanMoi = db.timTaiKhoanTheoTen(taiKhoanHienTai.getTenDangNhap());
                
                if (taiKhoanMoi != null && !taiKhoanMoi.taiKhoanHoatDong()) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, 
                            "Tài khoản của bạn đã bị khóa bởi quản trị viên.\n" +
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
     * Tạo panel navigation với các link
     */
    private JPanel taoPanelNavigation() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền bo tròn với màu sắc
                g2d.setColor(new Color(255, 240, 245, 30)); // Hồng rất nhạt
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Vẽ viền bo tròn
                g2d.setColor(new Color(255, 192, 203, 50)); // Hồng nhạt
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        // Ảnh đại diện
        JLabel lblAvatar = taoAnhDaiDien();
        panel.add(lblAvatar);
        
        // Link Hồ sơ
        JLabel linkHoSo = taoLink("Hồ sơ");
        linkHoSo.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new HoSoNguoiDung(null, taiKhoanHienTai).setVisible(true);
            }
        });
        panel.add(linkHoSo);
        
        // Link Cập nhật thông tin
        JLabel linkCapNhat = taoLink("Cập nhật thông tin");
        linkCapNhat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new CapNhatThongTin(null, taiKhoanHienTai, ketNoi).setVisible(true);
            }
        });
        panel.add(linkCapNhat);
        
        // Link Quản lý ảnh đại diện
        JLabel linkAnhDaiDien = taoLink("Ảnh đại diện");
        linkAnhDaiDien.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                moQuanLyAnhDaiDien();
            }
        });
        panel.add(linkAnhDaiDien);
        
        // Link Đổi mật khẩu
        JLabel linkDoiMatKhau = taoLink("Đổi mật khẩu");
        linkDoiMatKhau.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new DoiMatKhau(null, taiKhoanHienTai, ketNoi).setVisible(true);
            }
        });
        panel.add(linkDoiMatKhau);
        
        return panel;
    }
    
    /**
     * Tạo link với style đẹp
     */
    private JLabel taoLink(String text) {
        JLabel link = new JLabel(text);
        link.setFont(new Font("Arial", Font.BOLD, 16));
        link.setForeground(new Color(138, 43, 226)); // Tím đậm
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(255, 105, 180))); // Hồng đậm
        
        // Hiệu ứng hover
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                link.setForeground(new Color(255, 20, 147)); // Hồng đậm
                link.setFont(new Font("Arial", Font.BOLD, 18));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                link.setForeground(new Color(138, 43, 226)); // Tím đậm
                link.setFont(new Font("Arial", Font.BOLD, 16));
            }
        });
        
        return link;
    }
    
    /**
     * Tạo panel chính với lời chào
     */
    private JPanel taoPanelChinhMoi() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền bo tròn với màu sắc
                g2d.setColor(new Color(248, 240, 255, 40)); // Tím rất nhạt
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                // Vẽ viền bo tròn
                g2d.setColor(new Color(221, 160, 221, 60)); // Tím nhạt
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        // Lời chào chính với màu sắc đẹp
        this.lblChaoMung = new JLabel("Xin chào, \"" + taiKhoanHienTai.getHoTen() + "\"", SwingConstants.CENTER);
        this.lblChaoMung.setFont(new Font("Serif", Font.BOLD, 38));
        this.lblChaoMung.setForeground(new Color(138, 43, 226)); // Tím đậm
        panel.add(this.lblChaoMung, BorderLayout.CENTER);
        
        // Thông báo chào mừng với màu sắc đẹp
        JLabel lblThongBao = new JLabel("Chào mừng đến với hệ thống! Tôi có thể giúp gì cho bạn.", SwingConstants.CENTER);
        lblThongBao.setFont(new Font("Arial", Font.ITALIC, 18));
        lblThongBao.setForeground(new Color(255, 105, 180)); // Hồng đậm
        lblThongBao.setBorder(new EmptyBorder(20, 0, 0, 0));
        panel.add(lblThongBao, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Tạo panel nút đăng xuất mới
     */
    private JPanel taoPanelNutMoi() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền bo tròn với màu sắc
                g2d.setColor(new Color(255, 218, 185, 30)); // Cam nhạt
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Vẽ viền bo tròn
                g2d.setColor(new Color(255, 182, 193, 50)); // Hồng nhạt
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 30, 20));
        
        // Nút đăng xuất
        JButton btnDangXuat = taoNutDangXuatMoi();
        btnDangXuat.addActionListener(e -> dangXuat());
        panel.add(btnDangXuat);
        
        return panel;
    }
    
    /**
     * Tạo nút đăng xuất với style mới
     */
    private JButton taoNutDangXuatMoi() {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền màu hồng thường
                g2d.setColor(new Color(255, 192, 203)); // Hồng thường
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Vẽ viền bo tròn đơn giản
                g2d.setColor(new Color(255, 105, 180)); // Hồng đậm hơn một chút
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Vẽ text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight / 2) / 2;
                g2d.drawString(getText(), x, y);
            }
        };
        
        button.setText("Đăng xuất");
        button.setPreferredSize(new Dimension(120, 40));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        // Hiệu ứng hover để nút nổi lên
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                // Tăng kích thước nhẹ để tạo hiệu ứng nổi
                button.setPreferredSize(new Dimension(125, 42));
                button.revalidate();
                button.repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Trở về kích thước ban đầu
                button.setPreferredSize(new Dimension(120, 40));
                button.revalidate();
                button.repaint();
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                // Hiệu ứng nhấn
                button.setPreferredSize(new Dimension(118, 38));
                button.revalidate();
                button.repaint();
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                // Trở về kích thước hover
                button.setPreferredSize(new Dimension(125, 42));
                button.revalidate();
                button.repaint();
            }
        });
        
        return button;
    }
    
    
    
    
    
    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Nút hồ sơ người dùng với gradient
        JButton btnHoSo = taoNutDep("Hồ Sơ Người Dùng", new Color(25, 118, 210), new Color(13, 71, 161));
        btnHoSo.addActionListener(e -> moHoSoNguoiDung());
        panel.add(btnHoSo);
        
        // Nút cập nhật thông tin với gradient
        JButton btnCapNhat = taoNutDep("Cập Nhật Thông Tin", new Color(255, 152, 0), new Color(255, 87, 34));
        btnCapNhat.addActionListener(e -> moCapNhatThongTin());
        panel.add(btnCapNhat);
        
        // Nút đổi mật khẩu với gradient
        JButton btnDoiMatKhau = taoNutDep("Đổi Mật Khẩu", new Color(156, 39, 176), new Color(123, 31, 162));
        btnDoiMatKhau.addActionListener(e -> moDoiMatKhau());
        panel.add(btnDoiMatKhau);
        
        // Nút đăng xuất với gradient
        JButton btnDangXuat = taoNutDep("Đăng Xuất", new Color(244, 67, 54), new Color(198, 40, 40));
        btnDangXuat.addActionListener(e -> dangXuat());
        panel.add(btnDangXuat);
        
        return panel;
    }
    
    private JButton taoNutDep(String text, Color mauNen1, Color mauNen2) {
        return ButtonUtils.createLargeElevatedButton(text, mauNen1);
    }
    
    private void capNhatThongTinTaiKhoan() {
        // Cập nhật label chào mừng với tên mới
        if (lblChaoMung != null && taiKhoanHienTai != null) {
            lblChaoMung.setText("Xin chào, \"" + taiKhoanHienTai.getHoTen() + "\"");
        }
    }
    
    /**
     * Getter cho tài khoản hiện tại
     */
    public database.TaiKhoan getTaiKhoanHienTai() {
        return taiKhoanHienTai;
    }
    
    
    private void moHoSoNguoiDung() {
        HoSoNguoiDung hoSoDialog = new HoSoNguoiDung(this, taiKhoanHienTai);
        hoSoDialog.setVisible(true);
    }
    
    private void moCapNhatThongTin() {
        CapNhatThongTin capNhatDialog = new CapNhatThongTin(this, taiKhoanHienTai, ketNoi, this::capNhatThongTinTaiKhoan);
        capNhatDialog.setVisible(true);
    }
    
    private void moDoiMatKhau() {
        DoiMatKhau doiMatKhauDialog = new DoiMatKhau(this, taiKhoanHienTai, ketNoi);
        doiMatKhauDialog.setVisible(true);
    }
    
    private void dangXuat() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            thucHienDangXuat(true);
        }
    }
    
    private void dangXuatKhiDongCuaSo() {
        // Đăng xuất khi đóng cửa sổ mà không cần xác nhận
        thucHienDangXuat(false);
    }
    
    private void thucHienDangXuat(boolean hienThiThongBao) {
        // Dừng timer
        dungTimerLangNghe();
        
        // Gửi yêu cầu đăng xuất tới server
        if (ketNoi.dangXuat()) {
            if (hienThiThongBao) {
                JOptionPane.showMessageDialog(this, 
                    "Đã đăng xuất thành công", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        // Đóng kết nối và mở lại màn hình đăng nhập
        ketNoi.ngatketnoi();
        dispose();
        
        SwingUtilities.invokeLater(() -> {
            new ManHinhDangNhap().setVisible(true);
        });
    }
    
    /**
     * Tạo ảnh đại diện với hiệu ứng đẹp
     */
    private JLabel taoAnhDaiDien() {
        JLabel lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(80, 80));
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
                moQuanLyAnhDaiDien();
            }
        });
        
        // Load ảnh đại diện hiện tại
        taiAnhDaiDien(lblAvatar);
        
        // Force hiển thị ngay lập tức
        SwingUtilities.invokeLater(() -> {
            lblAvatar.repaint();
        });
        
        return lblAvatar;
    }
    
    /**
     * Tải ảnh đại diện hiện tại
     */
    private void taiAnhDaiDien(JLabel lblAvatar) {
        try {
            database.KetNoiDatabase db = database.KetNoiDatabase.getInstance();
            String avatarPath = db.layAnhDaiDien(taiKhoanHienTai.getId());
            
            if (avatarPath != null && !avatarPath.isEmpty() && !avatarPath.equals("default_avatar.png")) {
                java.io.File avatarFile = new java.io.File(avatarPath);
                if (avatarFile.exists()) {
                    java.awt.image.BufferedImage avatarImage = javax.imageio.ImageIO.read(avatarFile);
                    BufferedImage croppedImage = cropToSquare(avatarImage, 80);
                    ImageIcon icon = new ImageIcon(croppedImage);
                    lblAvatar.setIcon(icon);
                    lblAvatar.setText("");
                    lblAvatar.repaint(); // Force repaint
                } else {
                    hienThiAnhMacDinh(lblAvatar);
                }
            } else {
                hienThiAnhMacDinh(lblAvatar);
            }
        } catch (Exception e) {
            // Luôn hiển thị ảnh mặc định khi có lỗi
            hienThiAnhMacDinh(lblAvatar);
        }
    }
    
    /**
     * Hiển thị ảnh mặc định
     */
    private void hienThiAnhMacDinh(JLabel lblAvatar) {
        // Tạo ảnh mặc định với icon user
        BufferedImage defaultImage = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = defaultImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Vẽ nền tròn
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillOval(5, 5, 70, 70);
        
        // Vẽ chữ cái đầu của tên đăng nhập user
        g2d.setColor(new Color(100, 100, 100));
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "U"; // Default fallback
        if (taiKhoanHienTai != null && taiKhoanHienTai.getTenDangNhap() != null && !taiKhoanHienTai.getTenDangNhap().isEmpty()) {
            text = taiKhoanHienTai.getTenDangNhap().substring(0, 1).toUpperCase();
        }
        int x = (80 - fm.stringWidth(text)) / 2;
        int y = (80 - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        
        ImageIcon icon = new ImageIcon(defaultImage);
        lblAvatar.setIcon(icon);
        lblAvatar.setText("");
        lblAvatar.repaint(); // Force repaint
    }
    
    /**
     * Mở dialog quản lý ảnh đại diện
     */
    private void moQuanLyAnhDaiDien() {
        try {
            database.KetNoiDatabase db = database.KetNoiDatabase.getInstance();
            String currentAvatarPath = db.layAnhDaiDien(taiKhoanHienTai.getId());
            
            AvatarUploadDialog dialog = new AvatarUploadDialog(this, currentAvatarPath, () -> {
                // Callback khi ảnh được thay đổi
                capNhatAnhDaiDien();
            });
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi mở quản lý ảnh đại diện: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cắt ảnh thành hình vuông thông minh - tự động cắt để vừa khung
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
     * Cập nhật ảnh đại diện trong giao diện
     */
    private void capNhatAnhDaiDien() {
        // Tìm và cập nhật ảnh đại diện trong navigation panel
        JPanel backgroundPanel = (JPanel) getContentPane().getComponent(0);
        JPanel navigationPanel = (JPanel) backgroundPanel.getComponent(0);
        
        // Tìm JLabel avatar trong navigation panel (tìm label có kích thước 80x80)
        for (Component comp : navigationPanel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                Dimension size = label.getPreferredSize();
                if (size != null && size.width == 80 && size.height == 80) {
                    // Đây là avatar label
                    taiAnhDaiDien(label);
                    label.repaint(); // Force repaint
                    break;
                }
            }
        }
        
        // Force repaint toàn bộ navigation panel
        navigationPanel.repaint();
    }
}
