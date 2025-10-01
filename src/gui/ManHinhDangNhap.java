package gui;

import client.KetNoiTCP;
import database.TaiKhoan;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Màn hình đăng nhập
 */
public class ManHinhDangNhap extends JFrame {
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private JButton btnDangKy;
    
    private KetNoiTCP ketNoi;
    private static final String DIA_CHI_MAC_DINH = "localhost";
    private static final int CONG_MAC_DINH = 2712;
    
    public ManHinhDangNhap() {
        khoiTaoGiaoDien();
    }
    
    private void khoiTaoGiaoDien() {
        setTitle("Đăng Nhập - Hệ Thống Quản Lý");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel chính với background gradient đẹp
        JPanel panelChinh = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient từ xanh nhạt đến trắng
                Color color1 = new Color(240, 248, 255); // Xanh rất nhạt
                Color color2 = new Color(255, 255, 255); // Trắng
                Color color3 = new Color(230, 240, 250); // Xanh nhạt
                
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
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillOval(-50, -50, 200, 200);
                g2d.fillOval(getWidth() - 150, getHeight() - 100, 150, 150);
            }
        };
        panelChinh.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Tiêu đề với shadow
        JLabel lblTieuDe = new JLabel("HỆ THỐNG ĐĂNG NHẬP") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(getText(), 2, fm.getAscent() + 2);
                
                // Vẽ text chính
                g2d.setColor(getForeground());
                g2d.drawString(getText(), 0, fm.getAscent());
                g2d.dispose();
            }
        };
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 32));
        lblTieuDe.setForeground(new Color(25, 118, 210)); // Xanh đậm hơn
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelChinh.add(lblTieuDe, gbc);
        
        // Khoảng cách
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        panelChinh.add(Box.createVerticalStrut(20), gbc);
        
        // Panel đăng nhập (không có phần kết nối server)
        JPanel panelDangNhap = taoPanelDangNhap();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(panelDangNhap, gbc);
        
        add(panelChinh, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Tự động kết nối server khi khởi động
        ketNoiServerTuDong();
    }
    
    private void ketNoiServerTuDong() {
        // Tự động kết nối với server mặc định
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    ketNoi = new KetNoiTCP(DIA_CHI_MAC_DINH, CONG_MAC_DINH);
                    return ketNoi.ketNoi();
                } catch (Exception e) {
                    System.err.println("Lỗi kết nối tự động: " + e.getMessage());
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean thanhCong = get();
                    capNhatTrangThaiKetNoi(thanhCong);
                    
                    if (thanhCong) {
                        // Hiển thị thông báo kết nối thành công
                        JOptionPane.showMessageDialog(ManHinhDangNhap.this, 
                            "✅ Đã kết nối thành công với server!\nBạn có thể đăng nhập ngay bây giờ.", 
                            "Kết nối thành công", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Hiển thị thông báo lỗi kết nối
                        JOptionPane.showMessageDialog(ManHinhDangNhap.this, 
                            "❌ Không thể kết nối với server!\nVui lòng kiểm tra:\n" +
                            "• Server đã được khởi động chưa?\n" +
                            "• Địa chỉ và cổng có đúng không?\n" +
                            "• Firewall có chặn kết nối không?", 
                            "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    capNhatTrangThaiKetNoi(false);
                    JOptionPane.showMessageDialog(ManHinhDangNhap.this, 
                        "❌ Lỗi kết nối: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    
    private JPanel taoPanelDangNhap() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ background với màu trắng
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Vẽ border với shadow
                g2d.setColor(new Color(200, 200, 200, 100));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);
                
                g2d.setColor(new Color(25, 118, 210));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        
        // Tạo border đẹp
        Border outerBorder = BorderFactory.createEmptyBorder(25, 25, 25, 25);
        Border innerBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(25, 118, 210), 2), 
            "Thông Tin Đăng Nhập", 
            0, 0, 
            new Font("Arial", Font.BOLD, 16), 
            new Color(25, 118, 210)
        );
        panel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Tên đăng nhập với màu sắc đẹp
        JLabel lblTenDN = new JLabel("Tên đăng nhập:");
        lblTenDN.setFont(new Font("Arial", Font.BOLD, 15));
        lblTenDN.setForeground(new Color(55, 71, 79)); // Màu xám đậm
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblTenDN, gbc);
        
        txtTenDangNhap = new JTextField(25) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.setFont(getFont().deriveFont(Font.ITALIC));
                    g2d.drawString("Nhập tên đăng nhập...", 15, getHeight()/2 + 5);
                    g2d.dispose();
                }
            }
        };
        
        // Thêm FocusListener để ẩn/hiện placeholder
        txtTenDangNhap.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                repaint();
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                repaint();
            }
        });
        txtTenDangNhap.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTenDangNhap.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 118, 210), 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        txtTenDangNhap.setBackground(new Color(250, 250, 250));
        txtTenDangNhap.setEnabled(true);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtTenDangNhap, gbc);
        
        // Mật khẩu với màu sắc đẹp
        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        lblMatKhau.setFont(new Font("Arial", Font.BOLD, 15));
        lblMatKhau.setForeground(new Color(55, 71, 79)); // Màu xám đậm
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(lblMatKhau, gbc);
        
        // Tạo password field với nút ẩn/hiện
        JPanel passwordPanel = PasswordFieldUtils.createPasswordFieldWithToggle(25);
        txtMatKhau = PasswordFieldUtils.getPasswordField(passwordPanel);
        txtMatKhau.setFont(new Font("Arial", Font.PLAIN, 14));
        txtMatKhau.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 118, 210), 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        txtMatKhau.setBackground(new Color(250, 250, 250));
        txtMatKhau.setEnabled(true);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(passwordPanel, gbc);
        
        // Khoảng cách
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(Box.createVerticalStrut(20), gbc);
        
        // Panel nút với layout đẹp hơn
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelNut.setBackground(Color.WHITE);
        
        // Nút thử kết nối lại với thiết kế hiện đại
        JButton btnKetNoiLai = taoNutHienDai("Thử Kết Nối Lại", new Color(255, 193, 7));
        btnKetNoiLai.addActionListener(e -> ketNoiServerTuDong());
        panelNut.add(btnKetNoiLai);
        
        // Nút đăng nhập với thiết kế hiện đại
        btnDangNhap = taoNutHienDai("Đăng Nhập", new Color(25, 118, 210));
        btnDangNhap.setEnabled(true);
        btnDangNhap.addActionListener(e -> dangNhap());
        panelNut.add(btnDangNhap);
        
        // Nút đăng ký với thiết kế hiện đại
        btnDangKy = taoNutHienDai("Đăng Ký", new Color(46, 125, 50));
        btnDangKy.setEnabled(true);
        btnDangKy.addActionListener(e -> moManHinhDangKy());
        panelNut.add(btnDangKy);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(panelNut, gbc);
        
        return panel;
    }
    
    private JButton taoNutHienDai(String text, Color mauNen) {
        JButton button = new JButton() {
            private boolean isHovered = false;
            private boolean isPressed = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Màu nền động dựa trên trạng thái
                Color backgroundColor = mauNen;
                if (isPressed) {
                    backgroundColor = mauNen.darker();
                } else if (isHovered) {
                    backgroundColor = mauNen.brighter();
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
        
        button.setPreferredSize(new Dimension(180, 55));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }
    
    private void capNhatTrangThaiKetNoi(boolean daKetNoi) {
        if (daKetNoi) {
            // Kích hoạt các trường đăng nhập
            txtTenDangNhap.setEnabled(true);
            txtMatKhau.setEnabled(true);
            btnDangNhap.setEnabled(true);
            btnDangKy.setEnabled(true);
            
            // Thêm hiệu ứng visual
            txtTenDangNhap.setBackground(Color.WHITE);
            txtMatKhau.setBackground(Color.WHITE);
            
            // Focus vào trường tên đăng nhập
            txtTenDangNhap.requestFocus();
            
        } else {
            // Vô hiệu hóa các trường đăng nhập
            txtTenDangNhap.setEnabled(false);
            txtMatKhau.setEnabled(false);
            btnDangNhap.setEnabled(false);
            btnDangKy.setEnabled(false);
            
            // Thêm hiệu ứng visual
            txtTenDangNhap.setBackground(new Color(240, 240, 240));
            txtMatKhau.setBackground(new Color(240, 240, 240));
        }
    }
    
    private void dangNhap() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());
        
        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin đăng nhập", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Kiểm tra kết nối server trước khi đăng nhập
        if (ketNoi == null || !ketNoi.isDaKetNoi()) {
            JOptionPane.showMessageDialog(this, 
                "Chưa kết nối với server!\nVui lòng đợi kết nối hoàn tất hoặc thử lại sau.", 
                "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        btnDangNhap.setEnabled(false);
        btnDangNhap.setText("Đang đăng nhập...");
        
        SwingWorker<TaiKhoan, Void> worker = new SwingWorker<TaiKhoan, Void>() {
            @Override
            protected TaiKhoan doInBackground() throws Exception {
                return ketNoi.dangNhap(tenDangNhap, matKhau);
            }
            
            @Override
            protected void done() {
                try {
                    TaiKhoan taiKhoan = get();
                    
                    if (taiKhoan != null) {
                        JOptionPane.showMessageDialog(ManHinhDangNhap.this, 
                            "Đăng nhập thành công! Chào mừng " + taiKhoan.getHoTen(), 
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Mở giao diện phù hợp theo vai trò
                        moGiaoDienChinh(taiKhoan);
                        dispose();
                        
                    } else {
                        JOptionPane.showMessageDialog(ManHinhDangNhap.this, 
                            "Tên đăng nhập hoặc mật khẩu không đúng", 
                            "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ManHinhDangNhap.this, 
                        "Lỗi đăng nhập: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                
                btnDangNhap.setEnabled(true);
                btnDangNhap.setText("Đăng Nhập");
            }
        };
        
        worker.execute();
    }
    
    private void moManHinhDangKy() {
        // Kiểm tra kết nối server trước khi mở đăng ký
        if (ketNoi == null) {
            JOptionPane.showMessageDialog(this, 
                "❌ Chưa kết nối với server!\nVui lòng đợi kết nối hoàn tất hoặc thử lại sau.", 
                "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ManHinhDangKy dangKy = new ManHinhDangKy(this, ketNoi);
        dangKy.setVisible(true);
    }
    
    private void moGiaoDienChinh(TaiKhoan taiKhoan) {
        if (taiKhoan.laAdmin()) {
            // Mở giao diện admin
            GiaoDienAdmin adminGUI = new GiaoDienAdmin(taiKhoan, ketNoi);
            adminGUI.setVisible(true);
        } else {
            // Mở giao diện user
            GiaoDienUser userGUI = new GiaoDienUser(taiKhoan, ketNoi);
            userGUI.setVisible(true);
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ManHinhDangNhap().setVisible(true);
        });
    }
}
