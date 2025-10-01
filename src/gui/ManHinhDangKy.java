package gui;

import client.KetNoiTCP;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Màn hình đăng ký tài khoản
 */
public class ManHinhDangKy extends JDialog {
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JPasswordField txtXacNhanMatKhau;
    private JTextField txtHoTen;
    private JButton btnDangKy;
    private JButton btnHuy;
    
    private KetNoiTCP ketNoi;
    @SuppressWarnings("unused")
    private ManHinhDangNhap manHinhCha;
    
    public ManHinhDangKy(ManHinhDangNhap parent, KetNoiTCP ketNoi) {
        super(parent, "Đăng Ký Tài Khoản", true);
        this.manHinhCha = parent;
        this.ketNoi = ketNoi;
        
        khoiTaoGiaoDien();
    }
    
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Thiết lập nền đồng nhất
        getContentPane().setBackground(new Color(255, 255, 255));
        
        // Panel chính với nền đồng nhất
        JPanel panelChinh = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Nền trắng đồng nhất
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Viền nhẹ
                g2d.setColor(new Color(220, 220, 220));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            }
        };
        panelChinh.setOpaque(false);
        panelChinh.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Tiêu đề
        JLabel lblTieuDe = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        lblTieuDe.setForeground(new Color(25, 118, 210));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelChinh.add(lblTieuDe, gbc);
        
        // Khoảng cách
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        panelChinh.add(Box.createVerticalStrut(20), gbc);
        
        // Họ và tên
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelChinh.add(new JLabel("Họ và tên:"), gbc);
        
        txtHoTen = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.setFont(getFont().deriveFont(Font.ITALIC));
                    g2d.drawString("Nhập họ và tên...", 10, getHeight()/2 + 5);
                    g2d.dispose();
                }
            }
        };
        
        // Thêm FocusListener để ẩn/hiện placeholder
        txtHoTen.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                repaint();
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                repaint();
            }
        });
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(txtHoTen, gbc);
        
        // Tên đăng nhập
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panelChinh.add(new JLabel("Tên đăng nhập:"), gbc);
        
        txtTenDangNhap = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.setFont(getFont().deriveFont(Font.ITALIC));
                    g2d.drawString("Nhập tên đăng nhập...", 10, getHeight()/2 + 5);
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
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(txtTenDangNhap, gbc);
        
        // Mật khẩu
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        panelChinh.add(new JLabel("Mật khẩu:"), gbc);
        
        JPanel passwordPanel1 = PasswordFieldUtils.createPasswordFieldWithToggle(20);
        txtMatKhau = PasswordFieldUtils.getPasswordField(passwordPanel1);
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(passwordPanel1, gbc);
        
        // Xác nhận mật khẩu
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        panelChinh.add(new JLabel("Xác nhận mật khẩu:"), gbc);
        
        JPanel passwordPanel2 = PasswordFieldUtils.createPasswordFieldWithToggle(20);
        txtXacNhanMatKhau = PasswordFieldUtils.getPasswordField(passwordPanel2);
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(passwordPanel2, gbc);
        
        // Ghi chú
        JLabel lblGhiChu = new JLabel("<html><i>Lưu ý: Thông tin email, số điện thoại và ngày sinh<br/>có thể cập nhật sau khi đăng nhập thành công</i></html>");
        lblGhiChu.setFont(lblGhiChu.getFont().deriveFont(11f));
        lblGhiChu.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelChinh.add(lblGhiChu, gbc);
        
        // Panel nút với nền đồng nhất
        JPanel panelNut = new JPanel(new FlowLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Nền trắng đồng nhất
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelNut.setOpaque(false);
        
        btnDangKy = taoNutHienDai("Đăng Ký", new Color(46, 125, 50));
        btnDangKy.addActionListener(e -> dangKy());
        panelNut.add(btnDangKy);
        
        btnHuy = taoNutHienDai("Hủy", new Color(244, 67, 54));
        btnHuy.addActionListener(e -> dispose());
        panelNut.add(btnHuy);
        
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(panelNut, gbc);
        
        add(panelChinh, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        // Focus vào trường đầu tiên
        txtHoTen.requestFocus();
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
        
        button.setPreferredSize(new Dimension(140, 50));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }
    
    private void dangKy() {
        // Lấy dữ liệu từ form
        String hoTen = txtHoTen.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());
        String xacNhanMatKhau = new String(txtXacNhanMatKhau.getPassword());
        
        // Kiểm tra dữ liệu đầu vào
        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ và tên", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }
        
        if (tenDangNhap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenDangNhap.requestFocus();
            return;
        }
        
        if (tenDangNhap.length() < 3) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập phải có ít nhất 3 ký tự", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenDangNhap.requestFocus();
            return;
        }
        
        // Kiểm tra tên đăng nhập trùng lặp
        database.KetNoiDatabase db = database.KetNoiDatabase.getInstance();
        if (db.kiemTraTenDangNhapTonTai(tenDangNhap)) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenDangNhap.requestFocus();
            return;
        }
        
        if (matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMatKhau.requestFocus();
            return;
        }
        
        if (matKhau.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMatKhau.requestFocus();
            return;
        }
        
        if (!matKhau.equals(xacNhanMatKhau)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtXacNhanMatKhau.requestFocus();
            return;
        }
        
        // Vô hiệu hóa nút trong khi đăng ký
        btnDangKy.setEnabled(false);
        btnDangKy.setText("Đang đăng ký...");
        
        // Đăng ký trong thread riêng
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return ketNoi.dangKy(tenDangNhap, matKhau, hoTen);
            }
            
            @Override
            protected void done() {
                try {
                    boolean thanhCong = get();
                    
                    if (thanhCong) {
                        JOptionPane.showMessageDialog(ManHinhDangKy.this, 
                            "Đăng ký tài khoản thành công!\nBạn có thể đăng nhập ngay bây giờ.", 
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ManHinhDangKy.this, 
                            "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.", 
                            "Đăng ký thất bại", JOptionPane.ERROR_MESSAGE);
                        txtTenDangNhap.requestFocus();
                        txtTenDangNhap.selectAll();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ManHinhDangKy.this, 
                        "Lỗi đăng ký: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                
                // Kích hoạt lại nút
                btnDangKy.setEnabled(true);
                btnDangKy.setText("Đăng Ký");
            }
        };
        
        worker.execute();
    }
}
