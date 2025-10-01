package gui;

import client.KetNoiTCP;
import database.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog cập nhật thông tin cá nhân
 */
public class CapNhatThongTin extends JDialog {
    private TaiKhoan taiKhoan;
    @SuppressWarnings("unused")
    private KetNoiTCP ketNoi;
    private Runnable onUpdateCallback;
    
    private JTextField txtHoTen;
    private JTextField txtEmail;
    private JTextField txtSoDienThoai;
    private JSpinner spinnerNgay;
    private JSpinner spinnerThang;
    private JSpinner spinnerNam;
    
    public CapNhatThongTin(JFrame parent, TaiKhoan taiKhoan, KetNoiTCP ketNoi) {
        this(parent, taiKhoan, ketNoi, null);
    }
    
    public CapNhatThongTin(JFrame parent, TaiKhoan taiKhoan, KetNoiTCP ketNoi, Runnable onUpdateCallback) {
        super(parent, "Cập Nhật Thông Tin", true);
        this.taiKhoan = taiKhoan;
        this.ketNoi = ketNoi;
        this.onUpdateCallback = onUpdateCallback;
        
        khoiTaoGiaoDien();
        dienThongTinHienTai();
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
        JLabel lblTieuDe = new JLabel("CẬP NHẬT THÔNG TIN CÁ NHÂN");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 22));
        lblTieuDe.setForeground(new Color(255, 105, 180)); // Hồng đậm
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelChinh.add(lblTieuDe, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Họ và tên
        gbc.gridx = 0; gbc.gridy = 1;
        panelChinh.add(new JLabel("Họ và tên:"), gbc);
        
        txtHoTen = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(txtHoTen, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panelChinh.add(new JLabel("Email:"), gbc);
        
        txtEmail = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(txtEmail, gbc);
        
        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panelChinh.add(new JLabel("Số điện thoại:"), gbc);
        
        txtSoDienThoai = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(txtSoDienThoai, gbc);
        
        // Ngày sinh
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        panelChinh.add(new JLabel("Ngày sinh:"), gbc);
        
        JPanel panelNgaySinh = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelNgaySinh.setBackground(Color.WHITE);
        
        spinnerNgay = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        spinnerNgay.setPreferredSize(new Dimension(60, 25));
        panelNgaySinh.add(spinnerNgay);
        panelNgaySinh.add(new JLabel("/"));
        
        spinnerThang = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        spinnerThang.setPreferredSize(new Dimension(60, 25));
        panelNgaySinh.add(spinnerThang);
        panelNgaySinh.add(new JLabel("/"));
        
        spinnerNam = new JSpinner(new SpinnerNumberModel(2000, 1900, 2025, 1));
        spinnerNam.setPreferredSize(new Dimension(80, 25));
        panelNgaySinh.add(spinnerNam);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelChinh.add(panelNgaySinh, gbc);
        
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
        
        JButton btnCapNhat = taoNutHienDai("Cập Nhật", new Color(255, 192, 203)); // Hồng thường
        btnCapNhat.addActionListener(e -> capNhatThongTin());
        panelNut.add(btnCapNhat);
        
        JButton btnHuy = taoNutHienDai("Hủy", new Color(255, 105, 180)); // Hồng đậm
        btnHuy.addActionListener(e -> dispose());
        panelNut.add(btnHuy);
        
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
    
    private void dienThongTinHienTai() {
        txtHoTen.setText(taiKhoan.getHoTen());
        
        if (taiKhoan.getEmail() != null) {
            txtEmail.setText(taiKhoan.getEmail());
        }
        
        if (taiKhoan.getSoDienThoai() != null) {
            txtSoDienThoai.setText(taiKhoan.getSoDienThoai());
        }
        
        if (taiKhoan.getNgaySinh() != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(taiKhoan.getNgaySinh());
            
            spinnerNgay.setValue(cal.get(java.util.Calendar.DAY_OF_MONTH));
            spinnerThang.setValue(cal.get(java.util.Calendar.MONTH) + 1);
            spinnerNam.setValue(cal.get(java.util.Calendar.YEAR));
        }
    }
    
    private void capNhatThongTin() {
        // Lấy dữ liệu từ form
        String hoTen = txtHoTen.getText().trim();
        String email = txtEmail.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        
        // Kiểm tra dữ liệu
        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ và tên", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }
        
        if (!email.isEmpty() && !isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        
        if (!soDienThoai.isEmpty() && !isValidPhoneNumber(soDienThoai)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtSoDienThoai.requestFocus();
            return;
        }
        
        // Tạo ngày sinh
        java.sql.Date ngaySinh = null;
        try {
            int ngay = (Integer) spinnerNgay.getValue();
            int thang = (Integer) spinnerThang.getValue();
            int nam = (Integer) spinnerNam.getValue();
            
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(nam, thang - 1, ngay);
            ngaySinh = new java.sql.Date(cal.getTimeInMillis());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Cập nhật thông tin tài khoản local
        taiKhoan.setHoTen(hoTen);
        taiKhoan.setEmail(email.isEmpty() ? null : email);
        taiKhoan.setSoDienThoai(soDienThoai.isEmpty() ? null : soDienThoai);
        taiKhoan.setNgaySinh(ngaySinh);
        
        // Chuẩn bị dữ liệu cho database
        String emailForDB = email.isEmpty() ? null : email;
        String soDienThoaiForDB = soDienThoai.isEmpty() ? null : soDienThoai;
        
        // Cập nhật vào database
        try {
            database.KetNoiDatabase db = database.KetNoiDatabase.getInstance();
            System.out.println("Đang cập nhật tài khoản ID: " + taiKhoan.getId());
            System.out.println("Họ tên: " + hoTen);
            System.out.println("Email: " + emailForDB);
            System.out.println("Số điện thoại: " + soDienThoaiForDB);
            System.out.println("Vai trò: " + taiKhoan.getVaiTro());
            System.out.println("Ngày sinh: " + ngaySinh);
            
            boolean result = db.capNhatTaiKhoan(taiKhoan.getId(), hoTen, emailForDB, soDienThoaiForDB, taiKhoan.getVaiTro(), ngaySinh);
            System.out.println("Kết quả cập nhật: " + result);
            
            if (result) {
                JOptionPane.showMessageDialog(this, 
                    "Cập nhật thông tin thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Gọi callback để cập nhật giao diện chính
                if (onUpdateCallback != null) {
                    onUpdateCallback.run();
                }
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi cập nhật thông tin. Vui lòng thử lại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật thông tin: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi kết nối database: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^[0-9]{10,11}$");
    }
}
