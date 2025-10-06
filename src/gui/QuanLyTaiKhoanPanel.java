package gui;

import client.KetNoiTCP;
import database.KetNoiDatabase;
import database.TaiKhoan;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Timer;

/**
 * Panel quản lý tài khoản cho admin
 */
public class QuanLyTaiKhoanPanel extends JPanel {
    @SuppressWarnings("unused")
    private KetNoiTCP ketNoi;
    private JTable tableTaiKhoan;
    private DefaultTableModel modelTaiKhoan;
    private JButton btnThemTaiKhoan;
    private JButton btnSuaTaiKhoan;
    private JButton btnXoaTaiKhoan;
    private JButton btnKhoaTaiKhoan;
    private JButton btnMoKhoaTaiKhoan;
    private Timer timerTuDongLamMoi;
    private boolean isAutoRefresh = false;
    
    // Thêm các component cho tìm kiếm và lọc
    private JTextField txtTimKiem;
    private JComboBox<String> comboLocTrangThai;
    private JButton btnTimKiem;
    private JButton btnLamMoiLoc;
    
    public QuanLyTaiKhoanPanel(KetNoiTCP ketNoi) {
        this.ketNoi = ketNoi;
        khoiTaoGiaoDien();
        khoiTaoTimerTuDongLamMoi();
        lamMoiDuLieu(); // Load dữ liệu ngay khi khởi tạo
    }
    
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Panel tiêu đề
        JPanel panelTieuDe = new JPanel(new BorderLayout());
        panelTieuDe.setBackground(new Color(255, 255, 255));
        panelTieuDe.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTieuDe = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        lblTieuDe.setForeground(new Color(25, 118, 210));
        panelTieuDe.add(lblTieuDe, BorderLayout.CENTER);
        
        
        add(panelTieuDe, BorderLayout.NORTH);
        
        // Panel bảng
        JPanel panelBang = taoPanelBang();
        add(panelBang, BorderLayout.CENTER);
        
        // Panel nút
        JPanel panelNut = taoPanelNut();
        add(panelNut, BorderLayout.SOUTH);
    }
    
    private JPanel taoPanelBang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        // Panel tìm kiếm và lọc
        JPanel panelTimKiem = taoPanelTimKiem();
        panel.add(panelTimKiem, BorderLayout.NORTH);
        
        // Tạo bảng
        String[] cot = {"ID", "Tên đăng nhập", "Họ tên", "Email", "Số điện thoại", "Ngày sinh",
                       "Vai trò", "Trạng thái", "Online/Offline", "Ngày tạo"};
        modelTaiKhoan = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp
            }
        };
        
        tableTaiKhoan = new JTable(modelTaiKhoan) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                // Lấy thông tin trạng thái từ dòng
                String trangThai = "";
                String onlineStatus = "";
                if (getModel().getRowCount() > row) {
                    trangThai = (String) getModel().getValueAt(row, 7); // Cột "Trạng thái"
                    onlineStatus = (String) getModel().getValueAt(row, 8); // Cột "Online/Offline"
                }
                
                // Màu nền dựa trên trạng thái
                if ("Bị khóa".equals(trangThai)) {
                    // Tài khoản bị khóa - màu đỏ nhạt
                    c.setBackground(new Color(255, 235, 238)); // Đỏ nhạt
                    c.setForeground(new Color(197, 17, 98)); // Đỏ đậm cho text
                } else if ("Online".equals(onlineStatus)) {
                    // Tài khoản đang online - màu xanh nhạt
                    c.setBackground(new Color(232, 245, 233)); // Xanh nhạt
                    c.setForeground(new Color(27, 94, 32)); // Xanh đậm cho text
                } else {
                    // Tài khoản bình thường - màu xen kẽ
                    if (row % 2 == 0) {
                        c.setBackground(new Color(248, 250, 252)); // Màu xanh nhạt
                    } else {
                        c.setBackground(Color.WHITE); // Màu trắng
                    }
                    c.setForeground(Color.BLACK);
                }
                
                // Màu khi được chọn
                if (isRowSelected(row)) {
                    c.setBackground(new Color(25, 118, 210)); // Màu xanh dương
                    c.setForeground(Color.WHITE);
                }
                
                return c;
            }
        };
        tableTaiKhoan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableTaiKhoan.setRowHeight(25);
        tableTaiKhoan.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableTaiKhoan.getTableHeader().setReorderingAllowed(false); // Không cho phép di chuyển cột
        
        // Thêm MouseListener để xử lý double-click
        tableTaiKhoan.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { // Double-click
                    hienThiLichSuDangNhap();
                }
            }
        });
        
        // Bo góc cho bảng
        tableTaiKhoan.setBorder(BorderFactory.createEmptyBorder());
        tableTaiKhoan.setShowGrid(true);
        tableTaiKhoan.setGridColor(new Color(200, 200, 200));
        
        JScrollPane scrollPane = new JScrollPane(tableTaiKhoan) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền trắng với bo góc
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Vẽ border
                g2d.setColor(new Color(220, 220, 220));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(0, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton taoNutHienDai(String text, Color mauNen) {
        return ButtonUtils.createElevatedButton(text, mauNen);
    }
    
    private JPanel taoPanelTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Label tìm kiếm
        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTimKiem);
        
        // TextField tìm kiếm
        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 12));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        // Thêm DocumentListener để lọc tự động
        txtTimKiem.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                timKiemTuDong();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                timKiemTuDong();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                timKiemTuDong();
            }
        });
        
        panel.add(txtTimKiem);
        
        // Label lọc trạng thái
        JLabel lblLoc = new JLabel("Lọc theo trạng thái:");
        lblLoc.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblLoc);
        
        // ComboBox lọc trạng thái
        comboLocTrangThai = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Bị khóa"});
        comboLocTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        comboLocTrangThai.setPreferredSize(new Dimension(100, 30));
        comboLocTrangThai.addActionListener(e -> timKiemTuDong());
        panel.add(comboLocTrangThai);
        
        // Nút tìm kiếm
        btnTimKiem = ButtonUtils.createSmallElevatedButton("Tìm kiếm", new Color(33, 150, 243));
        btnTimKiem.addActionListener(e -> timKiemTaiKhoan());
        panel.add(btnTimKiem);
        
        // Nút làm mới lọc
        btnLamMoiLoc = ButtonUtils.createSmallElevatedButton("Làm mới", new Color(76, 175, 80));
        btnLamMoiLoc.addActionListener(e -> lamMoiLoc());
        panel.add(btnLamMoiLoc);
        
        // Thêm sự kiện Enter cho textfield
        txtTimKiem.addActionListener(e -> timKiemTaiKhoan());
        
        return panel;
    }
    
    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(1000, 120)); // Đảm bảo đủ không gian cho tất cả nút
        
        btnThemTaiKhoan = ButtonUtils.createGreenCoolButton("Thêm Tài Khoản");
        btnThemTaiKhoan.addActionListener(e -> themTaiKhoan());
        panel.add(btnThemTaiKhoan);
        
        btnSuaTaiKhoan = ButtonUtils.createOrangeCoolButton("Sửa Thông Tin");
        btnSuaTaiKhoan.addActionListener(e -> suaTaiKhoan());
        panel.add(btnSuaTaiKhoan);
        
        btnXoaTaiKhoan = ButtonUtils.createRedCoolButton("Xóa Tài Khoản");
        btnXoaTaiKhoan.addActionListener(e -> xoaTaiKhoan());
        panel.add(btnXoaTaiKhoan);
        
        btnKhoaTaiKhoan = ButtonUtils.createPurpleCoolButton("Khóa Tài Khoản");
        btnKhoaTaiKhoan.addActionListener(e -> khoaTaiKhoan());
        panel.add(btnKhoaTaiKhoan);
        
        btnMoKhoaTaiKhoan = ButtonUtils.createCyanCoolButton("Mở Khóa");
        btnMoKhoaTaiKhoan.addActionListener(e -> moKhoaTaiKhoan());
        panel.add(btnMoKhoaTaiKhoan);
        
        
        
        return panel;
    }
    
    public void lamMoiDuLieu() {
        lamMoiDuLieu(false);
    }
    
    public void lamMoiDuLieu(boolean isAuto) {
        isAutoRefresh = isAuto;
        SwingWorker<List<TaiKhoan>, Void> worker = new SwingWorker<List<TaiKhoan>, Void>() {
            @Override
            protected List<TaiKhoan> doInBackground() throws Exception {
                // Lấy danh sách tài khoản thực từ database
                KetNoiDatabase db = KetNoiDatabase.getInstance();
                if (db.getConnection() != null) {
                    return db.layDanhSachTaiKhoan();
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    List<TaiKhoan> danhSachTaiKhoan = get();
                    
                    // Xóa dữ liệu cũ
                    modelTaiKhoan.setRowCount(0);
                    
                    if (danhSachTaiKhoan != null && !danhSachTaiKhoan.isEmpty()) {
                        // Thêm dữ liệu thực từ database
                        for (TaiKhoan taiKhoan : danhSachTaiKhoan) {
                            
                            Object[] row = {
                                taiKhoan.getId(),
                                taiKhoan.getTenDangNhap(),
                                taiKhoan.getHoTen(),
                                taiKhoan.getEmail(),
                                taiKhoan.getSoDienThoai(),
                                taiKhoan.getNgaySinh() != null ? 
                                    new java.text.SimpleDateFormat("dd/MM/yyyy").format(taiKhoan.getNgaySinh()) : "Chưa cập nhật",
                                taiKhoan.getVaiTro(),
                                taiKhoan.isBiKhoa() ? "Bị khóa" : "Hoạt động",
                                taiKhoan.isOnline() ? "Online" : "Offline",
                                taiKhoan.getNgayTao() != null ? 
                                    new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(taiKhoan.getNgayTao()) : "Không xác định"
                            };
                            modelTaiKhoan.addRow(row);
                        }
                        // Không hiển thị thông báo khi tự động làm mới
                    } else {
                        // Chỉ hiển thị thông báo khi không có dữ liệu (không phải tự động làm mới)
                        if (!isAutoRefresh) {
                            JOptionPane.showMessageDialog(QuanLyTaiKhoanPanel.this, 
                                "Không có dữ liệu tài khoản trong database!", 
                                "Thông báo", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(QuanLyTaiKhoanPanel.this, 
                        "Lỗi khi tải dữ liệu từ database: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void khoiTaoTimerTuDongLamMoi() {
        // Không sử dụng timer tự động nữa
        // Dữ liệu sẽ được cập nhật khi cần thiết
    }
    
    public void dungTimerTuDongLamMoi() {
        if (timerTuDongLamMoi != null) {
            timerTuDongLamMoi.cancel();
        }
    }
    
    private void themTaiKhoan() {
        // Dialog thêm tài khoản mới
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm Tài Khoản Mới", true);
        dialog.setSize(400, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Thiết lập nền xanh dương với hoa văn chìm
        dialog.getContentPane().setBackground(new Color(240, 248, 255));
        
        // Tạo panel nền với hoa văn chìm
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Nền xanh dương nhạt
                g2d.setColor(new Color(240, 248, 255));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Hoa văn chìm - hình tròn nhỏ
                g2d.setColor(new Color(173, 216, 230, 30));
                for (int i = 0; i < 10; i++) {
                    int x = (int) (Math.random() * getWidth());
                    int y = (int) (Math.random() * getHeight());
                    int size = 15 + (int) (Math.random() * 25);
                    g2d.fillOval(x, y, size, size);
                }
                
                // Viền xanh dương
                g2d.setColor(new Color(25, 118, 210));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 15, 15);
            }
        };
        backgroundPanel.setOpaque(false);
        
        // Panel thông tin với nền xanh dương
        JPanel panelThongTin = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Nền xanh dương nhạt
                g2d.setColor(new Color(248, 250, 255));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Viền xanh dương
                g2d.setColor(new Color(25, 118, 210));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        panelThongTin.setOpaque(false);
        panelThongTin.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Tên đăng nhập
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelThongTin.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        JTextField txtTenDangNhap = new JTextField(20);
        panelThongTin.add(txtTenDangNhap, gbc);
        
        // Họ tên
        gbc.gridx = 0; gbc.gridy = 1;
        panelThongTin.add(new JLabel("Họ và tên:"), gbc);
        gbc.gridx = 1;
        JTextField txtHoTen = new JTextField(20);
        panelThongTin.add(txtHoTen, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        panelThongTin.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(20);
        panelThongTin.add(txtEmail, gbc);
        
        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = 3;
        panelThongTin.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        JTextField txtSoDienThoai = new JTextField(20);
        panelThongTin.add(txtSoDienThoai, gbc);
        
        // Mật khẩu
        gbc.gridx = 0; gbc.gridy = 4;
        panelThongTin.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        
        // Tạo custom password field với icon con mắt bên trong
        JPasswordField txtMatKhau = new JPasswordField(20) {
            private boolean showPassword = false;
            private JButton toggleButton;
            
            @Override
            public void addNotify() {
                super.addNotify();
                // Tạo nút toggle với icon con mắt nhỏ hơn
                toggleButton = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                        
                        int width = getWidth();
                        int height = getHeight();
                        int centerX = width / 2;
                        int centerY = height / 2;
                        int eyeSize = Math.min(width, height) / 2; // Nhỏ hơn
                        
                        // Vẽ nền trong suốt
                        g2d.setColor(new Color(0, 0, 0, 0));
                        g2d.fillRect(0, 0, width, height);
                        
                        if (showPassword) {
                            // Mắt mở - vẽ con mắt nhỏ hơn
                            g2d.setColor(new Color(25, 118, 210));
                            g2d.setStroke(new BasicStroke(1.5f));
                            
                            // Vẽ hình oval cho mắt
                            g2d.drawOval(centerX - eyeSize/2, centerY - eyeSize/2, eyeSize, eyeSize);
                            
                            // Vẽ con ngươi
                            g2d.setColor(new Color(25, 118, 210));
                            g2d.fillOval(centerX - eyeSize/3, centerY - eyeSize/3, eyeSize*2/3, eyeSize*2/3);
                            
                            // Vẽ highlight
                            g2d.setColor(new Color(255, 255, 255, 180));
                            g2d.fillOval(centerX - eyeSize/6, centerY - eyeSize/6, eyeSize/3, eyeSize/3);
                            
                        } else {
                            // Mắt nhắm - vẽ đường cong
                            g2d.setColor(new Color(25, 118, 210));
                            g2d.setStroke(new BasicStroke(2f));
                            
                            // Vẽ đường cong
                            g2d.drawArc(centerX - eyeSize/2, centerY - eyeSize/2, eyeSize, eyeSize, 0, 180);
                            
                            // Vẽ thêm đường cong nhỏ
                            g2d.setStroke(new BasicStroke(1f));
                            g2d.drawArc(centerX - eyeSize/3, centerY - eyeSize/3, eyeSize*2/3, eyeSize*2/3, 0, 180);
                        }
                    }
                };
                toggleButton.setOpaque(false);
                toggleButton.setBorderPainted(false);
                toggleButton.setFocusPainted(false);
                toggleButton.setContentAreaFilled(false);
                toggleButton.setPreferredSize(new Dimension(30, 25)); // Nhỏ hơn
                toggleButton.setToolTipText(showPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu");
                toggleButton.addActionListener(e -> {
                    showPassword = !showPassword;
                    toggleButton.setToolTipText(showPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu");
                    setEchoChar(showPassword ? (char) 0 : '•');
                    repaint();
                });
                
                // Đặt nút ở góc phải bên trong
                setLayout(new BorderLayout());
                add(toggleButton, BorderLayout.EAST);
            }
        };
        
        panelThongTin.add(txtMatKhau, gbc);
        
        // Vai trò
        gbc.gridx = 0; gbc.gridy = 5;
        panelThongTin.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> comboVaiTro = new JComboBox<>(new String[]{"user", "admin"});
        panelThongTin.add(comboVaiTro, gbc);
        
        // Trạng thái
        gbc.gridx = 0; gbc.gridy = 6;
        panelThongTin.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> comboTrangThai = new JComboBox<>(new String[]{"hoat_dong", "bi_khoa"});
        comboTrangThai.setSelectedItem("hoat_dong"); // Mặc định là hoạt động
        panelThongTin.add(comboTrangThai, gbc);
        
        backgroundPanel.add(panelThongTin, BorderLayout.CENTER);
        dialog.add(backgroundPanel, BorderLayout.CENTER);
        
        // Panel nút với nền xanh dương
        JPanel panelNut = new JPanel(new FlowLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Nền xanh dương nhạt
                g2d.setColor(new Color(248, 250, 255));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelNut.setOpaque(false);
        JButton btnThem = ButtonUtils.createGreenCoolButton("Thêm");
        btnThem.addActionListener(e -> {
            // Xử lý thêm tài khoản
            String tenDN = txtTenDangNhap.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            String email = txtEmail.getText().trim();
            String sdt = txtSoDienThoai.getText().trim();
            String matKhau = new String(txtMatKhau.getPassword());
            String vaiTro = (String) comboVaiTro.getSelectedItem();
            String trangThai = (String) comboTrangThai.getSelectedItem();
            
            if (tenDN.isEmpty() || hoTen.isEmpty() || email.isEmpty() || sdt.isEmpty() || matKhau.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Kiểm tra độ dài mật khẩu
            if (!PasswordValidator.validateAndShowError(matKhau, dialog)) {
                return;
            }
            
            // Kiểm tra tên đăng nhập trùng lặp
            KetNoiDatabase db = KetNoiDatabase.getInstance();
            if (db.kiemTraTenDangNhapTonTai(tenDN)) {
                JOptionPane.showMessageDialog(dialog, "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtTenDangNhap.requestFocus();
                return;
            }
            
            // Thêm vào database
            if (db.themTaiKhoan(tenDN, matKhau, hoTen, email, sdt, vaiTro, trangThai)) {
                JOptionPane.showMessageDialog(dialog, "Đã thêm tài khoản thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                // Làm mới dữ liệu
                lamMoiDuLieu();
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm tài khoản! Tên đăng nhập có thể đã tồn tại.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnHuy = ButtonUtils.createRedCoolButton("Hủy");
        btnHuy.addActionListener(e -> dialog.dispose());
        
        panelNut.add(btnThem);
        panelNut.add(btnHuy);
        dialog.add(panelNut, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void suaTaiKhoan() {
        int selectedRow = tableTaiKhoan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần sửa", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Lấy thông tin tài khoản hiện tại
        int taiKhoanId = (Integer) modelTaiKhoan.getValueAt(selectedRow, 0);
        String tenDangNhap = (String) modelTaiKhoan.getValueAt(selectedRow, 1);
        String hoTen = (String) modelTaiKhoan.getValueAt(selectedRow, 2);
        String email = (String) modelTaiKhoan.getValueAt(selectedRow, 3);
        String soDienThoai = (String) modelTaiKhoan.getValueAt(selectedRow, 4);
        String ngaySinhStr = (String) modelTaiKhoan.getValueAt(selectedRow, 5);
        String vaiTro = (String) modelTaiKhoan.getValueAt(selectedRow, 6);
        String trangThaiStr = (String) modelTaiKhoan.getValueAt(selectedRow, 7);
        
        // Chuyển đổi trạng thái từ hiển thị sang database format
        String trangThai = trangThaiStr.equals("Bị khóa") ? "bi_khoa" : "hoat_dong";
        
        // Lấy thông tin chi tiết từ database
        KetNoiDatabase db = KetNoiDatabase.getInstance();
        TaiKhoan taiKhoanChiTiet = null;
        // Tạm thời sử dụng thông tin từ bảng
        
        // Dialog sửa tài khoản
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Sửa Thông Tin Tài Khoản", true);
        dialog.setSize(580, 720);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);
        
        // Thiết lập nền xanh dương với hoa văn chìm
        dialog.getContentPane().setBackground(new Color(240, 248, 255));
        
        // Tạo panel nền với hoa văn chìm
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Nền xanh dương nhạt
                g2d.setColor(new Color(240, 248, 255));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Hoa văn chìm - hình tròn nhỏ
                g2d.setColor(new Color(173, 216, 230, 30));
                for (int i = 0; i < 15; i++) {
                    int x = (int) (Math.random() * getWidth());
                    int y = (int) (Math.random() * getHeight());
                    int size = 20 + (int) (Math.random() * 30);
                    g2d.fillOval(x, y, size, size);
                }
                
                // Hoa văn chìm - đường cong
                g2d.setColor(new Color(135, 206, 235, 25));
                g2d.setStroke(new BasicStroke(2));
                for (int i = 0; i < 8; i++) {
                    int x1 = (int) (Math.random() * getWidth());
                    int y1 = (int) (Math.random() * getHeight());
                    int x2 = (int) (Math.random() * getWidth());
                    int y2 = (int) (Math.random() * getHeight());
                    g2d.drawLine(x1, y1, x2, y2);
                }
                
                // Viền xanh dương
                g2d.setColor(new Color(25, 118, 210));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 15, 15);
            }
        };
        backgroundPanel.setOpaque(false);
        
        // Panel thông tin với nền xanh dương
        JPanel panelThongTin = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Nền xanh dương nhạt
                g2d.setColor(new Color(248, 250, 255));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Viền xanh dương
                g2d.setColor(new Color(25, 118, 210));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        panelThongTin.setOpaque(false);
        panelThongTin.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        
        // Tên đăng nhập (chỉ hiển thị, không sửa - đây là khóa chính)
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelThongTin.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        JLabel lblTenDangNhap = new JLabel(tenDangNhap);
        lblTenDangNhap.setForeground(Color.BLUE);
        lblTenDangNhap.setFont(new Font("Arial", Font.BOLD, 12));
        panelThongTin.add(lblTenDangNhap, gbc);
        
        // Họ tên
        gbc.gridx = 0; gbc.gridy = 1;
        panelThongTin.add(new JLabel("Họ và tên:"), gbc);
        gbc.gridx = 1;
        JTextField txtHoTen = new JTextField(hoTen, 25);
        txtHoTen.setMinimumSize(new Dimension(200, 32));
        txtHoTen.setPreferredSize(new Dimension(280, 32));
        txtHoTen.setMaximumSize(new Dimension(350, 32));
        txtHoTen.setFont(new Font("Arial", Font.PLAIN, 12));
        panelThongTin.add(txtHoTen, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        panelThongTin.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(email, 25);
        txtEmail.setMinimumSize(new Dimension(200, 32));
        txtEmail.setPreferredSize(new Dimension(280, 32));
        txtEmail.setMaximumSize(new Dimension(350, 32));
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 12));
        panelThongTin.add(txtEmail, gbc);
        
        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = 3;
        panelThongTin.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        JTextField txtSoDienThoai = new JTextField(soDienThoai, 25);
        txtSoDienThoai.setMinimumSize(new Dimension(200, 32));
        txtSoDienThoai.setPreferredSize(new Dimension(280, 32));
        txtSoDienThoai.setMaximumSize(new Dimension(350, 32));
        txtSoDienThoai.setFont(new Font("Arial", Font.PLAIN, 12));
        panelThongTin.add(txtSoDienThoai, gbc);
        
        // Mật khẩu hiện tại (hiển thị mật khẩu thật)
        gbc.gridx = 0; gbc.gridy = 4;
        panelThongTin.add(new JLabel("Mật khẩu hiện tại:"), gbc);
        gbc.gridx = 1;
        String matKhauThuc = db.layMatKhauThuc(taiKhoanId);
        JTextField txtMatKhauHienTai = new JTextField(matKhauThuc != null ? matKhauThuc : "Không có", 25);
        txtMatKhauHienTai.setMinimumSize(new Dimension(200, 32));
        txtMatKhauHienTai.setPreferredSize(new Dimension(280, 32));
        txtMatKhauHienTai.setMaximumSize(new Dimension(350, 32));
        txtMatKhauHienTai.setFont(new Font("Arial", Font.PLAIN, 12));
        txtMatKhauHienTai.setEditable(false);
        txtMatKhauHienTai.setBackground(new Color(240, 240, 240));
        panelThongTin.add(txtMatKhauHienTai, gbc);
        
        // Mật khẩu mới (tùy chọn)
        gbc.gridx = 0; gbc.gridy = 5;
        panelThongTin.add(new JLabel("Mật khẩu mới (tùy chọn):"), gbc);
        gbc.gridx = 1;
        
        // Tạo custom password field với icon con mắt bên trong
        JPasswordField txtMatKhauMoi = new JPasswordField(25) {
            private boolean showPassword = false;
            private JButton toggleButton;
            
            @Override
            public void addNotify() {
                super.addNotify();
                // Tạo nút toggle với icon con mắt nhỏ hơn
                toggleButton = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                        
                        int width = getWidth();
                        int height = getHeight();
                        int centerX = width / 2;
                        int centerY = height / 2;
                        int eyeSize = Math.min(width, height) / 2; // Nhỏ hơn
                        
                        // Vẽ nền trong suốt
                        g2d.setColor(new Color(0, 0, 0, 0));
                        g2d.fillRect(0, 0, width, height);
                        
                        if (showPassword) {
                            // Mắt mở - vẽ con mắt nhỏ hơn
                            g2d.setColor(new Color(25, 118, 210));
                            g2d.setStroke(new BasicStroke(1.5f));
                            
                            // Vẽ hình oval cho mắt
                            g2d.drawOval(centerX - eyeSize/2, centerY - eyeSize/2, eyeSize, eyeSize);
                            
                            // Vẽ con ngươi
                            g2d.setColor(new Color(25, 118, 210));
                            g2d.fillOval(centerX - eyeSize/3, centerY - eyeSize/3, eyeSize*2/3, eyeSize*2/3);
                            
                            // Vẽ highlight
                            g2d.setColor(new Color(255, 255, 255, 180));
                            g2d.fillOval(centerX - eyeSize/6, centerY - eyeSize/6, eyeSize/3, eyeSize/3);
                            
                        } else {
                            // Mắt nhắm - vẽ đường cong
                            g2d.setColor(new Color(25, 118, 210));
                            g2d.setStroke(new BasicStroke(2f));
                            
                            // Vẽ đường cong
                            g2d.drawArc(centerX - eyeSize/2, centerY - eyeSize/2, eyeSize, eyeSize, 0, 180);
                            
                            // Vẽ thêm đường cong nhỏ
                            g2d.setStroke(new BasicStroke(1f));
                            g2d.drawArc(centerX - eyeSize/3, centerY - eyeSize/3, eyeSize*2/3, eyeSize*2/3, 0, 180);
                        }
                    }
                };
                toggleButton.setOpaque(false);
                toggleButton.setBorderPainted(false);
                toggleButton.setFocusPainted(false);
                toggleButton.setContentAreaFilled(false);
                toggleButton.setPreferredSize(new Dimension(30, 25)); // Nhỏ hơn
                toggleButton.setToolTipText(showPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu");
                toggleButton.addActionListener(e -> {
                    showPassword = !showPassword;
                    toggleButton.setToolTipText(showPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu");
                    setEchoChar(showPassword ? (char) 0 : '•');
                    repaint();
                });
                
                // Đặt nút ở góc phải bên trong
                setLayout(new BorderLayout());
                add(toggleButton, BorderLayout.EAST);
            }
        };
        
        txtMatKhauMoi.setMinimumSize(new Dimension(200, 32));
        txtMatKhauMoi.setPreferredSize(new Dimension(280, 32));
        txtMatKhauMoi.setMaximumSize(new Dimension(350, 32));
        txtMatKhauMoi.setFont(new Font("Arial", Font.PLAIN, 12));
        panelThongTin.add(txtMatKhauMoi, gbc);
        
        // Vai trò
        gbc.gridx = 0; gbc.gridy = 6;
        panelThongTin.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> comboVaiTro = new JComboBox<>(new String[]{"user", "admin"});
        comboVaiTro.setSelectedItem(vaiTro);
        panelThongTin.add(comboVaiTro, gbc);
        
        // Trạng thái
        gbc.gridx = 0; gbc.gridy = 7;
        panelThongTin.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> comboTrangThai = new JComboBox<>(new String[]{"hoat_dong", "bi_khoa"});
        comboTrangThai.setSelectedItem(trangThai);
        panelThongTin.add(comboTrangThai, gbc);
        
        // Ngày sinh
        gbc.gridx = 0; gbc.gridy = 8;
        panelThongTin.add(new JLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE; // Không fill cho panel ngày sinh
        gbc.anchor = GridBagConstraints.WEST;
        
        JPanel panelNgaySinh = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panelNgaySinh.setMinimumSize(new Dimension(300, 45));
        panelNgaySinh.setPreferredSize(new Dimension(380, 45));
        panelNgaySinh.setMaximumSize(new Dimension(450, 45));
        
        JSpinner spinnerNgay = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        JSpinner spinnerThang = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        JSpinner spinnerNam = new JSpinner(new SpinnerNumberModel(2000, 1900, 2024, 1));
        
        // Parse ngày sinh từ string và set vào spinner
        try {
            if (ngaySinhStr != null && !ngaySinhStr.equals("Chưa cập nhật")) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                java.util.Date ngaySinhDate = sdf.parse(ngaySinhStr);
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(ngaySinhDate);
                
                spinnerNgay.setValue(cal.get(java.util.Calendar.DAY_OF_MONTH));
                spinnerThang.setValue(cal.get(java.util.Calendar.MONTH) + 1); // Calendar.MONTH bắt đầu từ 0
                spinnerNam.setValue(cal.get(java.util.Calendar.YEAR));
                
            }
        } catch (Exception e) {
            System.err.println("Lỗi parse ngày sinh: " + e.getMessage());
            // Giữ giá trị mặc định nếu parse lỗi
        }
        
        // Kích thước vừa phải cho các spinner
        spinnerNgay.setPreferredSize(new Dimension(70, 32));
        spinnerThang.setPreferredSize(new Dimension(70, 32));
        spinnerNam.setPreferredSize(new Dimension(90, 32));
        spinnerNgay.setFont(new Font("Arial", Font.PLAIN, 12));
        spinnerThang.setFont(new Font("Arial", Font.PLAIN, 12));
        spinnerNam.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Thêm labels và spinners
        panelNgaySinh.add(new JLabel("Ngày:"));
        panelNgaySinh.add(spinnerNgay);
        panelNgaySinh.add(new JLabel("Tháng:"));
        panelNgaySinh.add(spinnerThang);
        panelNgaySinh.add(new JLabel("Năm:"));
        panelNgaySinh.add(spinnerNam);
        
        panelThongTin.add(panelNgaySinh, gbc);
        
        // Reset fill cho các thành phần khác
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Thêm scroll pane để responsive
        JScrollPane scrollPane = new JScrollPane(panelThongTin);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Panel nút
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelNut.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelNut.setMinimumSize(new Dimension(200, 60));
        panelNut.setPreferredSize(new Dimension(300, 60));
        JButton btnCapNhat = ButtonUtils.createGreenCoolButton("Cập Nhật");
        btnCapNhat.setMinimumSize(new Dimension(80, 35));
        btnCapNhat.setPreferredSize(new Dimension(120, 35));
        btnCapNhat.setMaximumSize(new Dimension(150, 35));
        btnCapNhat.setFont(new Font("Arial", Font.BOLD, 12));
        btnCapNhat.addActionListener(e -> {
            // Lấy dữ liệu từ form
            String hoTenMoi = txtHoTen.getText().trim();
            String emailMoi = txtEmail.getText().trim();
            String soDienThoaiMoi = txtSoDienThoai.getText().trim();
            String matKhauMoi = new String(txtMatKhauMoi.getPassword());
            String vaiTroMoi = (String) comboVaiTro.getSelectedItem();
            String trangThaiMoi = (String) comboTrangThai.getSelectedItem();
            
            if (hoTenMoi.isEmpty() || emailMoi.isEmpty() || soDienThoaiMoi.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin bắt buộc!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ngày sinh không hợp lệ", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Cập nhật vào database
            boolean capNhatThanhCong = true;
            
            // Cập nhật thông tin cơ bản
            if (!db.capNhatTaiKhoan(taiKhoanId, hoTenMoi, emailMoi, soDienThoaiMoi, vaiTroMoi, ngaySinh)) {
                capNhatThanhCong = false;
            }
            
            // Cập nhật mật khẩu (chỉ khi có nhập mật khẩu mới)
            if (capNhatThanhCong && !matKhauMoi.isEmpty()) {
                // Kiểm tra độ dài mật khẩu
                if (!PasswordValidator.validateAndShowError(matKhauMoi, dialog)) {
                    return;
                }
                
                if (!db.doiMatKhauAdmin(taiKhoanId, matKhauMoi)) {
                    capNhatThanhCong = false;
                }
            }
            
            // Cập nhật trạng thái
            if (capNhatThanhCong && !trangThaiMoi.equals(trangThai)) {
                if (!db.capNhatTrangThaiTaiKhoan(taiKhoanId, trangThaiMoi)) {
                    capNhatThanhCong = false;
                }
            }
            
            if (capNhatThanhCong) {
                JOptionPane.showMessageDialog(dialog, "Đã cập nhật thông tin tài khoản thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                // Làm mới dữ liệu
                lamMoiDuLieu();
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật thông tin tài khoản!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnHuy = ButtonUtils.createRedCoolButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(100, 35));
        btnHuy.setFont(new Font("Arial", Font.BOLD, 12));
        btnHuy.addActionListener(e -> dialog.dispose());
        
        panelNut.add(btnCapNhat);
        panelNut.add(btnHuy);
        dialog.add(panelNut, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void xoaTaiKhoan() {
        int selectedRow = tableTaiKhoan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String tenDangNhap = (String) modelTaiKhoan.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa tài khoản " + tenDangNhap + "?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // Lấy ID tài khoản từ bảng
            int taiKhoanId = (Integer) modelTaiKhoan.getValueAt(selectedRow, 0);
            
            // Xóa từ database
            KetNoiDatabase db = KetNoiDatabase.getInstance();
            if (db.xoaTaiKhoan(taiKhoanId)) {
                JOptionPane.showMessageDialog(this, "Đã xóa tài khoản thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                // Làm mới dữ liệu
                lamMoiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa tài khoản!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void khoaTaiKhoan() {
        int selectedRow = tableTaiKhoan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần khóa", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String tenDangNhap = (String) modelTaiKhoan.getValueAt(selectedRow, 1);
        int taiKhoanId = (Integer) modelTaiKhoan.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn khóa tài khoản " + tenDangNhap + "?\n" +
            "Người dùng sẽ bị đăng xuất khỏi hệ thống ngay lập tức.",
            "Xác nhận khóa", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            KetNoiDatabase db = KetNoiDatabase.getInstance();
            if (db.capNhatTrangThaiTaiKhoan(taiKhoanId, "bi_khoa")) {
                // Buộc đăng xuất user nếu đang online
                try {
                    // Gọi static method để buộc đăng xuất
                    boolean daDangXuat = server.MayChuTCP.buocDangXuatTaiKhoanStatic(tenDangNhap);
                    if (daDangXuat) {
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi buộc đăng xuất user: " + e.getMessage());
                }
                
                JOptionPane.showMessageDialog(this, "Đã khóa tài khoản và đăng xuất người dùng thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                // Làm mới dữ liệu
                lamMoiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi khóa tài khoản!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void moKhoaTaiKhoan() {
        int selectedRow = tableTaiKhoan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần mở khóa", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String tenDangNhap = (String) modelTaiKhoan.getValueAt(selectedRow, 1);
        int taiKhoanId = (Integer) modelTaiKhoan.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn mở khóa tài khoản " + tenDangNhap + "?",
            "Xác nhận mở khóa", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            KetNoiDatabase db = KetNoiDatabase.getInstance();
            if (db.capNhatTrangThaiTaiKhoan(taiKhoanId, "hoat_dong")) {
                JOptionPane.showMessageDialog(this, "Đã mở khóa tài khoản thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                // Làm mới dữ liệu
                lamMoiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi mở khóa tài khoản!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Tìm kiếm tài khoản
     */
    private void timKiemTaiKhoan() {
        String tuKhoa = txtTimKiem.getText().trim().toLowerCase();
        String trangThaiLoc = (String) comboLocTrangThai.getSelectedItem();
        
        // Lấy dữ liệu từ database
        KetNoiDatabase db = KetNoiDatabase.getInstance();
        List<TaiKhoan> danhSach = db.layDanhSachTaiKhoan();
        
        // Xóa dữ liệu cũ
        modelTaiKhoan.setRowCount(0);
        
        // Lọc và hiển thị
        for (TaiKhoan tk : danhSach) {
            boolean khopTuKhoa = tuKhoa.isEmpty() || 
                tk.getTenDangNhap().toLowerCase().contains(tuKhoa) ||
                tk.getHoTen().toLowerCase().contains(tuKhoa) ||
                (tk.getEmail() != null && tk.getEmail().toLowerCase().contains(tuKhoa));
            
            boolean khopTrangThai = true;
            if (trangThaiLoc != null) {
                if (trangThaiLoc.equals("Hoạt động")) {
                    khopTrangThai = tk.taiKhoanHoatDong();
                } else if (trangThaiLoc.equals("Bị khóa")) {
                    khopTrangThai = !tk.taiKhoanHoatDong();
                }
            }
            
            if (khopTuKhoa && khopTrangThai) {
                Object[] row = {
                    tk.getId(),
                    tk.getTenDangNhap(),
                    tk.getHoTen(),
                    tk.getEmail() != null ? tk.getEmail() : "",
                    tk.getSoDienThoai() != null ? tk.getSoDienThoai() : "",
                    tk.getNgaySinh() != null ? tk.getNgaySinh() : "",
                    tk.getVaiTro(),
                    tk.taiKhoanHoatDong() ? "Hoạt động" : "Bị khóa",
                    tk.isOnline() ? "Online" : "Offline",
                    tk.getNgayTao()
                };
                modelTaiKhoan.addRow(row);
            }
        }
        
        // Hiển thị kết quả
        int soLuong = modelTaiKhoan.getRowCount();
        if (soLuong == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản nào phù hợp!", 
                "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Làm mới lọc
     */
    private void lamMoiLoc() {
        txtTimKiem.setText("");
        comboLocTrangThai.setSelectedItem("Tất cả");
        lamMoiDuLieu();
    }
    
    /**
     * Tìm kiếm tự động khi nhập hoặc thay đổi lọc
     */
    private void timKiemTuDong() {
        String tuKhoa = txtTimKiem.getText().trim().toLowerCase();
        String trangThaiLoc = (String) comboLocTrangThai.getSelectedItem();
        
        // Lấy dữ liệu từ database
        KetNoiDatabase db = KetNoiDatabase.getInstance();
        List<TaiKhoan> danhSach = db.layDanhSachTaiKhoan();
        
        // Xóa dữ liệu cũ
        modelTaiKhoan.setRowCount(0);
        
        // Lọc và hiển thị
        for (TaiKhoan tk : danhSach) {
            boolean khopTuKhoa = tuKhoa.isEmpty() || 
                tk.getTenDangNhap().toLowerCase().contains(tuKhoa) ||
                tk.getHoTen().toLowerCase().contains(tuKhoa) ||
                (tk.getEmail() != null && tk.getEmail().toLowerCase().contains(tuKhoa));
            
            boolean khopTrangThai = true;
            if (trangThaiLoc != null) {
                if (trangThaiLoc.equals("Hoạt động")) {
                    khopTrangThai = tk.taiKhoanHoatDong();
                } else if (trangThaiLoc.equals("Bị khóa")) {
                    khopTrangThai = !tk.taiKhoanHoatDong();
                }
            }
            
            if (khopTuKhoa && khopTrangThai) {
                Object[] row = {
                    tk.getId(),
                    tk.getTenDangNhap(),
                    tk.getHoTen(),
                    tk.getEmail() != null ? tk.getEmail() : "",
                    tk.getSoDienThoai() != null ? tk.getSoDienThoai() : "",
                    tk.getNgaySinh() != null ? tk.getNgaySinh() : "",
                    tk.getVaiTro(),
                    tk.taiKhoanHoatDong() ? "Hoạt động" : "Bị khóa",
                    tk.isOnline() ? "Online" : "Offline",
                    tk.getNgayTao()
                };
                modelTaiKhoan.addRow(row);
            }
        }
    }
    
    /**
     * Hiển thị lịch sử đăng nhập của tài khoản được chọn
     */
    private void hienThiLichSuDangNhap() {
        int selectedRow = tableTaiKhoan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để xem lịch sử đăng nhập", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Lấy thông tin tài khoản
        String tenDangNhap = (String) modelTaiKhoan.getValueAt(selectedRow, 1);
        String hoTen = (String) modelTaiKhoan.getValueAt(selectedRow, 2);
        
        // Tạo dialog hiển thị lịch sử đăng nhập
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "Lịch sử đăng nhập - " + hoTen, true);
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Panel tiêu đề
        JPanel panelTieuDe = new JPanel(new BorderLayout());
        panelTieuDe.setBackground(new Color(33, 150, 243));
        panelTieuDe.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTieuDe = new JLabel("Lịch sử đăng nhập - " + hoTen + " (" + tenDangNhap + ")");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDe.setForeground(Color.WHITE);
        panelTieuDe.add(lblTieuDe, BorderLayout.CENTER);
        
        dialog.add(panelTieuDe, BorderLayout.NORTH);
        
        // Panel chính chứa bảng lịch sử
        JPanel panelChinh = new JPanel(new BorderLayout());
        panelChinh.setBackground(Color.WHITE);
        panelChinh.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tạo bảng lịch sử đăng nhập
        String[] columns = {"STT", "Tên đăng nhập", "Thời gian đăng nhập", "Lần đăng nhập cuối", 
                           "Địa chỉ IP", "Trạng thái", "Ghi chú"};
        DefaultTableModel modelLichSu = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tableLichSu = new JTable(modelLichSu) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                // Màu xen kẽ
                if (row % 2 == 0) {
                    c.setBackground(new Color(248, 250, 252));
                } else {
                    c.setBackground(Color.WHITE);
                }
                c.setForeground(Color.BLACK);
                
                // Màu khi được chọn
                if (isRowSelected(row)) {
                    c.setBackground(new Color(25, 118, 210));
                    c.setForeground(Color.WHITE);
                }
                
                return c;
            }
        };
        
        tableLichSu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableLichSu.setRowHeight(25);
        tableLichSu.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableLichSu.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tableLichSu);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panelChinh.add(scrollPane, BorderLayout.CENTER);
        
        dialog.add(panelChinh, BorderLayout.CENTER);
        
        // Panel nút
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelNut.setBackground(Color.WHITE);
        panelNut.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton btnLamMoi = ButtonUtils.createSmallElevatedButton("Làm mới", new Color(76, 175, 80));
        btnLamMoi.addActionListener(e -> {
            // Làm mới dữ liệu
            lamMoiLichSuDangNhap(modelLichSu, tenDangNhap);
        });
        
        JButton btnDong = ButtonUtils.createSmallElevatedButton("Đóng", new Color(244, 67, 54));
        btnDong.addActionListener(e -> dialog.dispose());
        
        panelNut.add(btnLamMoi);
        panelNut.add(btnDong);
        dialog.add(panelNut, BorderLayout.SOUTH);
        
        // Load dữ liệu lịch sử đăng nhập
        lamMoiLichSuDangNhap(modelLichSu, tenDangNhap);
        
        dialog.setVisible(true);
    }
    
    /**
     * Làm mới dữ liệu lịch sử đăng nhập
     */
    private void lamMoiLichSuDangNhap(DefaultTableModel modelLichSu, String tenDangNhap) {
        try {
            // Xóa dữ liệu cũ
            modelLichSu.setRowCount(0);
            
            // Lấy dữ liệu từ database
            KetNoiDatabase db = KetNoiDatabase.getInstance();
            
            List<String[]> lichSu = db.layLichSuDangNhap();
            
            int stt = 1;
            boolean coDuLieu = false;
            
            if (lichSu != null && !lichSu.isEmpty()) {
                for (String[] record : lichSu) {
                    // Chỉ hiển thị lịch sử của tài khoản được chọn
                    if (record[1] != null && record[1].equals(tenDangNhap)) {
                        Object[] row = {
                            stt,
                            record[1], // Tên đăng nhập
                            record[2], // Thời gian đăng nhập
                            record[3], // Lần đăng nhập cuối
                            record[4], // Địa chỉ IP
                            record[5], // Trạng thái
                            record[6]  // Ghi chú
                        };
                        modelLichSu.addRow(row);
                        stt++;
                        coDuLieu = true;
                    }
                }
            }
            
            if (!coDuLieu) {
                // Không có dữ liệu
                Object[] row = {"", "", "Không có dữ liệu lịch sử đăng nhập", "", "", "", ""};
                modelLichSu.addRow(row);
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy lịch sử đăng nhập: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu lịch sử đăng nhập: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
