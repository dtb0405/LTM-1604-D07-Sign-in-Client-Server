package gui;

import client.KetNoiTCP;
import database.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog đổi mật khẩu
 */
public class DoiMatKhau extends JDialog {
    @SuppressWarnings("unused")
    private TaiKhoan taiKhoan;
    @SuppressWarnings("unused")
    private KetNoiTCP ketNoi;
    
    private JPasswordField txtMatKhauCu;
    private JPasswordField txtMatKhauMoi;
    private JPasswordField txtXacNhanMatKhau;
    
    public DoiMatKhau(JFrame parent, TaiKhoan taiKhoan, KetNoiTCP ketNoi) {
        super(parent, "Đổi Mật Khẩu", true);
        this.taiKhoan = taiKhoan;
        this.ketNoi = ketNoi;
        
        khoiTaoGiaoDien();
    }
    
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Thiết lập màu nền hồng nhạt cho toàn bộ dialog
        getContentPane().setBackground(new Color(255, 240, 245));
        
        // Tạo panel nền hồng cho toàn bộ dialog
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Nền hồng nhạt cho toàn bộ dialog
                g2d.setColor(new Color(255, 240, 245));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Viền hồng cho toàn bộ dialog
                g2d.setColor(new Color(255, 192, 203));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 15, 15);
            }
        };
        backgroundPanel.setOpaque(false);
        
        // Panel chính với màu hồng
        JPanel panelChinh = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Nền hồng nhạt với bo góc
                g2d.setColor(new Color(255, 248, 250));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Viền hồng
                g2d.setColor(new Color(255, 192, 203));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        panelChinh.setOpaque(false);
        panelChinh.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Tiêu đề với màu hồng
        JLabel lblTieuDe = new JLabel("ĐỔI MẬT KHẨU");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 22));
        lblTieuDe.setForeground(new Color(255, 105, 180)); // Hồng đậm
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelChinh.add(lblTieuDe, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Mật khẩu cũ
        gbc.gridx = 0; gbc.gridy = 1;
        panelChinh.add(new JLabel("Mật khẩu cũ:"), gbc);
        
        JPanel passwordPanel1 = PasswordFieldUtils.createPasswordFieldWithToggleNoPlaceholder(20);
        txtMatKhauCu = PasswordFieldUtils.getPasswordField(passwordPanel1);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(passwordPanel1, gbc);
        
        // Mật khẩu mới
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panelChinh.add(new JLabel("Mật khẩu mới:"), gbc);
        
        JPanel passwordPanel2 = PasswordFieldUtils.createPasswordFieldWithToggleNoPlaceholder(20);
        txtMatKhauMoi = PasswordFieldUtils.getPasswordField(passwordPanel2);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(passwordPanel2, gbc);
        
        // Xác nhận mật khẩu mới
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panelChinh.add(new JLabel("Xác nhận mật khẩu:"), gbc);
        
        JPanel passwordPanel3 = PasswordFieldUtils.createPasswordFieldWithToggleNoPlaceholder(20);
        txtXacNhanMatKhau = PasswordFieldUtils.getPasswordField(passwordPanel3);
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(passwordPanel3, gbc);
        
        // Hướng dẫn
        JLabel lblHuongDan = new JLabel("<html><i>Mật khẩu phải có ít nhất 6 ký tự</i></html>");
        lblHuongDan.setFont(lblHuongDan.getFont().deriveFont(11f));
        lblHuongDan.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelChinh.add(lblHuongDan, gbc);
        
        // Thêm panel chính vào backgroundPanel
        backgroundPanel.add(panelChinh, BorderLayout.CENTER);
        
        // Panel nút với màu hồng
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Nền hồng nhạt
                g2d.setColor(new Color(255, 248, 250));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelNut.setOpaque(false);
        panelNut.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JButton btnDoiMatKhau = taoNutHienDai("Đổi Mật Khẩu", new Color(255, 192, 203)); // Hồng thường
        btnDoiMatKhau.addActionListener(e -> doiMatKhau());
        panelNut.add(btnDoiMatKhau);
        
        JButton btnHuy = taoNutHienDai("Hủy", new Color(255, 105, 180)); // Hồng đậm
        btnHuy.addActionListener(e -> dispose());
        panelNut.add(btnHuy);
        
        backgroundPanel.add(panelNut, BorderLayout.SOUTH);
        
        // Thêm backgroundPanel vào dialog
        add(backgroundPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        // Focus vào trường đầu tiên
        txtMatKhauCu.requestFocus();
    }
    
    private JButton taoNutHienDai(String text, Color mauNen) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền màu hồng đơn giản
                g2d.setColor(mauNen);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Vẽ viền bo tròn đơn giản
                g2d.setColor(new Color(255, 105, 180)); // Hồng đậm hơn một chút
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
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
        
        button.setPreferredSize(new Dimension(160, 50));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }
    
    private void doiMatKhau() {
        // Lấy dữ liệu từ form
        String matKhauCu = new String(txtMatKhauCu.getPassword());
        String matKhauMoi = new String(txtMatKhauMoi.getPassword());
        String xacNhanMatKhau = new String(txtXacNhanMatKhau.getPassword());
        
        // Kiểm tra dữ liệu
        if (matKhauCu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu cũ", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMatKhauCu.requestFocus();
            return;
        }
        
        if (matKhauMoi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu mới", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMatKhauMoi.requestFocus();
            return;
        }
        
        if (matKhauMoi.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMatKhauMoi.requestFocus();
            return;
        }
        
        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtXacNhanMatKhau.requestFocus();
            return;
        }
        
        if (matKhauCu.equals(matKhauMoi)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải khác mật khẩu cũ", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMatKhauMoi.requestFocus();
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn đổi mật khẩu?",
            "Xác nhận", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // Cập nhật mật khẩu vào database
            try {
                database.KetNoiDatabase db = database.KetNoiDatabase.getInstance();
                if (db.doiMatKhau(taiKhoan.getId(), matKhauCu, matKhauMoi)) {
                    JOptionPane.showMessageDialog(this, 
                        "Đổi mật khẩu thành công!\nVui lòng đăng nhập lại với mật khẩu mới.", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Mật khẩu cũ không đúng hoặc có lỗi xảy ra. Vui lòng thử lại!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi kết nối database: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
