package gui;

import client.KetNoiTCP;
import database.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
        // Timer lắng nghe đã bị tắt
        timerLangNghe = null;
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
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Màu nền động dựa trên trạng thái
                Color backgroundColor = mauNen1;
                if (isPressed) {
                    backgroundColor = mauNen1.darker();
                } else if (isHovered) {
                    backgroundColor = mauNen1.brighter();
                }
                
                // Vẽ shadow với độ mờ động
                int shadowOffset = isPressed ? 1 : 3;
                g2d.setColor(new Color(0, 0, 0, isHovered ? 40 : 30));
                g2d.fillRoundRect(shadowOffset, shadowOffset, getWidth(), getHeight(), 25, 25);
                
                // Vẽ background chính
                g2d.setColor(backgroundColor);
                g2d.fillRoundRect(0, 0, getWidth() - shadowOffset, getHeight() - shadowOffset, 25, 25);
                
                // Vẽ border với độ dày động
                g2d.setColor(new Color(0, 0, 0, isHovered ? 30 : 20));
                g2d.setStroke(new BasicStroke(isHovered ? 2 : 1));
                g2d.drawRoundRect(0, 0, getWidth() - shadowOffset, getHeight() - shadowOffset, 25, 25);
                
                // Vẽ text với hiệu ứng
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight / 2) / 2;
                
                // Hiệu ứng text shadow khi hover
                if (isHovered) {
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.drawString(text, x + 1, y + 1);
                }
                g2d.setColor(Color.WHITE);
                g2d.drawString(text, x, y);
            }
            
            @Override
            public void addNotify() {
                super.addNotify();
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                    
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e) {
                        isPressed = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e) {
                        isPressed = false;
                        repaint();
                    }
                });
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(200, 55));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }
    
    private void capNhatThongTinTaiKhoan() {
        // Cập nhật label chào mừng với tên mới
        if (lblChaoMung != null && taiKhoanHienTai != null) {
            lblChaoMung.setText("Xin chào, \"" + taiKhoanHienTai.getHoTen() + "\"");
            System.out.println("Đã cập nhật tên hiển thị: " + taiKhoanHienTai.getHoTen());
        }
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
}
