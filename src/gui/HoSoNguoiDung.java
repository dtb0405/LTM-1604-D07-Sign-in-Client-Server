package gui;

import database.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Dialog hiển thị hồ sơ người dùng chi tiết
 */
public class HoSoNguoiDung extends JDialog {
    private TaiKhoan taiKhoan;
    
    public HoSoNguoiDung(JFrame parent, TaiKhoan taiKhoan) {
        super(parent, "Hồ Sơ Người Dùng", true);
        this.taiKhoan = taiKhoan;
        
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
        gbc.anchor = GridBagConstraints.WEST;
        
        // Tiêu đề với màu hồng
        JLabel lblTieuDe = new JLabel("THÔNG TIN CHI TIẾT");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 22));
        lblTieuDe.setForeground(new Color(255, 105, 180)); // Hồng đậm
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelChinh.add(lblTieuDe, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // ID
        gbc.gridx = 0; gbc.gridy = 1;
        panelChinh.add(new JLabel("ID:"), gbc);
        
        JLabel lblId = new JLabel(String.valueOf(taiKhoan.getId()));
        lblId.setFont(lblId.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 1; gbc.gridy = 1;
        panelChinh.add(lblId, gbc);
        
        // Tên đăng nhập
        gbc.gridx = 0; gbc.gridy = 2;
        panelChinh.add(new JLabel("Tên đăng nhập:"), gbc);
        
        JLabel lblTenDangNhap = new JLabel(taiKhoan.getTenDangNhap());
        lblTenDangNhap.setFont(lblTenDangNhap.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 1; gbc.gridy = 2;
        panelChinh.add(lblTenDangNhap, gbc);
        
        // Họ và tên
        gbc.gridx = 0; gbc.gridy = 3;
        panelChinh.add(new JLabel("Họ và tên:"), gbc);
        
        JLabel lblHoTen = new JLabel(taiKhoan.getHoTen());
        lblHoTen.setFont(lblHoTen.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 1; gbc.gridy = 3;
        panelChinh.add(lblHoTen, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 4;
        panelChinh.add(new JLabel("Email:"), gbc);
        
        JLabel lblEmail = new JLabel(taiKhoan.getEmail() != null ? taiKhoan.getEmail() : "Chưa cập nhật");
        lblEmail.setFont(lblEmail.getFont().deriveFont(Font.BOLD));
        if (taiKhoan.getEmail() == null) {
            lblEmail.setForeground(Color.GRAY);
        }
        gbc.gridx = 1; gbc.gridy = 4;
        panelChinh.add(lblEmail, gbc);
        
        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = 5;
        panelChinh.add(new JLabel("Số điện thoại:"), gbc);
        
        JLabel lblSoDienThoai = new JLabel(taiKhoan.getSoDienThoai() != null ? taiKhoan.getSoDienThoai() : "Chưa cập nhật");
        lblSoDienThoai.setFont(lblSoDienThoai.getFont().deriveFont(Font.BOLD));
        if (taiKhoan.getSoDienThoai() == null) {
            lblSoDienThoai.setForeground(Color.GRAY);
        }
        gbc.gridx = 1; gbc.gridy = 5;
        panelChinh.add(lblSoDienThoai, gbc);
        
        // Ngày sinh
        gbc.gridx = 0; gbc.gridy = 6;
        panelChinh.add(new JLabel("Ngày sinh:"), gbc);
        
        String ngaySinhText = "Chưa cập nhật";
        if (taiKhoan.getNgaySinh() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            ngaySinhText = sdf.format(taiKhoan.getNgaySinh());
        }
        
        JLabel lblNgaySinh = new JLabel(ngaySinhText);
        lblNgaySinh.setFont(lblNgaySinh.getFont().deriveFont(Font.BOLD));
        if (taiKhoan.getNgaySinh() == null) {
            lblNgaySinh.setForeground(Color.GRAY);
        }
        gbc.gridx = 1; gbc.gridy = 6;
        panelChinh.add(lblNgaySinh, gbc);
        
        // Vai trò
        gbc.gridx = 0; gbc.gridy = 7;
        panelChinh.add(new JLabel("Vai trò:"), gbc);
        
        JLabel lblVaiTro = new JLabel(taiKhoan.laAdmin() ? "Quản trị viên" : "Người dùng");
        lblVaiTro.setFont(lblVaiTro.getFont().deriveFont(Font.BOLD));
        lblVaiTro.setForeground(taiKhoan.laAdmin() ? Color.RED : new Color(76, 175, 80));
        gbc.gridx = 1; gbc.gridy = 7;
        panelChinh.add(lblVaiTro, gbc);
        
        // Trạng thái tài khoản
        gbc.gridx = 0; gbc.gridy = 8;
        panelChinh.add(new JLabel("Trạng thái:"), gbc);
        
        JLabel lblTrangThai = new JLabel(taiKhoan.taiKhoanHoatDong() ? "Hoạt động" : "Bị khóa");
        lblTrangThai.setFont(lblTrangThai.getFont().deriveFont(Font.BOLD));
        lblTrangThai.setForeground(taiKhoan.taiKhoanHoatDong() ? new Color(76, 175, 80) : Color.RED);
        gbc.gridx = 1; gbc.gridy = 8;
        panelChinh.add(lblTrangThai, gbc);
        
        // Ngày tạo
        if (taiKhoan.getNgayTao() != null) {
            gbc.gridx = 0; gbc.gridy = 9;
            panelChinh.add(new JLabel("Ngày tạo:"), gbc);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            JLabel lblNgayTao = new JLabel(sdf.format(taiKhoan.getNgayTao()));
            lblNgayTao.setFont(lblNgayTao.getFont().deriveFont(Font.BOLD));
            gbc.gridx = 1; gbc.gridy = 9;
            panelChinh.add(lblNgayTao, gbc);
        }
        
        // Lần đăng nhập cuối
        if (taiKhoan.getLanDangNhapCuoi() != null) {
            gbc.gridx = 0; gbc.gridy = 10;
            panelChinh.add(new JLabel("Đăng nhập cuối:"), gbc);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            JLabel lblDangNhapCuoi = new JLabel(sdf.format(taiKhoan.getLanDangNhapCuoi()));
            lblDangNhapCuoi.setFont(lblDangNhapCuoi.getFont().deriveFont(Font.BOLD));
            gbc.gridx = 1; gbc.gridy = 10;
            panelChinh.add(lblDangNhapCuoi, gbc);
        }
        
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
        
        JButton btnDong = taoNutHienDai("Đóng", new Color(255, 192, 203)); // Hồng thường
        btnDong.addActionListener(e -> dispose());
        panelNut.add(btnDong);
        
        backgroundPanel.add(panelNut, BorderLayout.SOUTH);
        
        // Thêm backgroundPanel vào dialog
        add(backgroundPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
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
        
        button.setPreferredSize(new Dimension(140, 50));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }
}
