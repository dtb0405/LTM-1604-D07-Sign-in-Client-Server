package gui;

import client.KetNoiTCP;
import database.KetNoiDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;

/**
 * Panel lịch sử đăng nhập cho admin
 */
public class LichSuDangNhapPanel extends JPanel {
    @SuppressWarnings("unused")
    private KetNoiTCP ketNoi;
    private JTable tableLichSu;
    private DefaultTableModel modelLichSu;
    private JButton btnXuatBaoCao;
    private JComboBox<String> comboLoc;
    private JTextField txtTimKiem;
    private Timer timerTuDongLamMoi;
    private boolean isAutoRefresh = false;
    
    public LichSuDangNhapPanel(KetNoiTCP ketNoi) {
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
        
        JLabel lblTieuDe = new JLabel("LỊCH SỬ ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        lblTieuDe.setForeground(new Color(25, 118, 210));
        panelTieuDe.add(lblTieuDe, BorderLayout.CENTER);
        
        
        add(panelTieuDe, BorderLayout.NORTH);
        
        // Panel bộ lọc
        JPanel panelBoLoc = taoPanelBoLoc();
        add(panelBoLoc, BorderLayout.CENTER);
        
        // Panel bảng
        JPanel panelBang = taoPanelBang();
        add(panelBang, BorderLayout.CENTER);
        
        // Panel nút
        JPanel panelNut = taoPanelNut();
        add(panelNut, BorderLayout.SOUTH);
    }
    
    private JPanel taoPanelBoLoc() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Lọc theo:"));
        
        comboLoc = new JComboBox<>(new String[]{"Tất cả", "Hôm nay", "Tuần này", "Tháng này"});
        comboLoc.setPreferredSize(new Dimension(120, 30));
        comboLoc.addActionListener(e -> lamMoiDuLieu());
        panel.add(comboLoc);
        
        panel.add(new JLabel("Tìm kiếm:"));
        
        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(200, 30));
        panel.add(txtTimKiem);
        
        JButton btnTimKiem = taoNutHienDai("Tìm", new Color(33, 150, 243));
        btnTimKiem.setPreferredSize(new Dimension(80, 30));
        btnTimKiem.addActionListener(e -> timKiem());
        panel.add(btnTimKiem);
        
        return panel;
    }
    
    private JPanel taoPanelBang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        // Tạo bảng
        String[] cot = {"STT", "Tên đăng nhập", "Thời gian đăng nhập", "Lần đăng nhập cuối", 
                        "Địa chỉ IP", "Trạng thái", "Ghi chú"};
        modelLichSu = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp
            }
        };
        
        tableLichSu = new JTable(modelLichSu) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                // Màu xen kẽ cho các dòng
                if (row % 2 == 0) {
                    c.setBackground(new Color(248, 250, 252)); // Màu xanh nhạt
                } else {
                    c.setBackground(Color.WHITE); // Màu trắng
                }
                
                // Màu khi được chọn
                if (isRowSelected(row)) {
                    c.setBackground(new Color(25, 118, 210)); // Màu xanh dương
                    c.setForeground(Color.WHITE);
                } else {
                    c.setForeground(Color.BLACK);
                }
                
                return c;
            }
        };
        tableLichSu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableLichSu.setRowHeight(25);
        tableLichSu.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableLichSu.getTableHeader().setReorderingAllowed(false); // Không cho phép di chuyển cột
        
        // Bo góc cho bảng
        tableLichSu.setBorder(BorderFactory.createEmptyBorder());
        tableLichSu.setShowGrid(true);
        tableLichSu.setGridColor(new Color(200, 200, 200));
        
        // Thiết lập độ rộng cột
        tableLichSu.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableLichSu.getColumnModel().getColumn(1).setPreferredWidth(120);
        tableLichSu.getColumnModel().getColumn(2).setPreferredWidth(150);
        tableLichSu.getColumnModel().getColumn(3).setPreferredWidth(150);
        tableLichSu.getColumnModel().getColumn(4).setPreferredWidth(120);
        tableLichSu.getColumnModel().getColumn(5).setPreferredWidth(100);
        tableLichSu.getColumnModel().getColumn(6).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(tableLichSu) {
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
    
    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        btnXuatBaoCao = taoNutHienDai("Xuất Báo Cáo", new Color(46, 125, 50));
        btnXuatBaoCao.addActionListener(e -> xuatBaoCao());
        panel.add(btnXuatBaoCao);
        
        
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
        
        button.setPreferredSize(new Dimension(160, 50));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }
    
    public void lamMoiDuLieu() {
        lamMoiDuLieu(false);
    }
    
    public void lamMoiDuLieu(boolean isAuto) {
        isAutoRefresh = isAuto;
        SwingWorker<List<String[]>, Void> worker = new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() throws Exception {
                // Lấy lịch sử đăng nhập thực từ database
                KetNoiDatabase db = KetNoiDatabase.getInstance();
                if (db.getConnection() != null) {
                    return db.layLichSuDangNhap();
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    List<String[]> lichSu = get();
                    
                    // Xóa dữ liệu cũ
                    modelLichSu.setRowCount(0);
                    
                    if (lichSu != null && !lichSu.isEmpty()) {
                        // Thêm dữ liệu thực từ database với STT và thay đổi cột
                        int stt = 1;
                        for (String[] record : lichSu) {
                            // Tạo row mới với STT và thay đổi cột thời gian đăng xuất thành lần đăng nhập cuối
                            Object[] newRow = {
                                stt++, // STT
                                record[1], // Tên đăng nhập
                                record[2], // Thời gian đăng nhập
                                record[3], // Lần đăng nhập cuối (thay vì thời gian đăng xuất)
                                record[4], // Địa chỉ IP
                                record[5], // Trạng thái
                                record[6]  // Ghi chú
                            };
                            modelLichSu.addRow(newRow);
                        }
                        // Không hiển thị thông báo khi tự động làm mới
                    } else {
                        // Chỉ hiển thị thông báo khi không có dữ liệu (không phải tự động làm mới)
                        if (!isAutoRefresh) {
                            JOptionPane.showMessageDialog(LichSuDangNhapPanel.this, 
                                "Không có dữ liệu lịch sử đăng nhập trong database!", 
                                "Thông báo", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LichSuDangNhapPanel.this, 
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
    
    
    private void timKiem() {
        String tuKhoa = txtTimKiem.getText().trim();
        if (tuKhoa.isEmpty()) {
            lamMoiDuLieu();
            return;
        }
        
        // Tìm kiếm trong dữ liệu hiện tại
        modelLichSu.setRowCount(0);
        
        // Dữ liệu để tìm kiếm (giống như trong themDuLieuGia)
        String[] tenDangNhap = {"admin", "user001", "user002", "user003", "user004", "user005",
                               "user006", "user007", "user008", "user009", "user010", "user011",
                               "user012", "user013", "user014", "user015", "user016", "user017",
                               "user018", "user019", "user020", "user021", "user022", "user023",
                               "user024", "user025", "user026", "user027", "user028", "user029"};
        
        String[] ipAddress = {"127.0.0.1", "192.168.1.100", "192.168.1.101", "192.168.1.102", 
                             "192.168.1.103", "192.168.1.104", "192.168.1.105", "192.168.1.106",
                             "192.168.1.107", "192.168.1.108", "192.168.1.109", "192.168.1.110",
                             "192.168.1.111", "192.168.1.112", "192.168.1.113", "192.168.1.114",
                             "192.168.1.115", "192.168.1.116", "192.168.1.117", "192.168.1.118",
                             "192.168.1.119", "192.168.1.120", "192.168.1.121", "192.168.1.122",
                             "192.168.1.123", "192.168.1.124", "192.168.1.125", "192.168.1.126",
                             "192.168.1.127", "192.168.1.128"};
        
        String[] trangThai = {"Thành công", "Thành công", "Thành công", "Thất bại", "Thành công",
                             "Thành công", "Thất bại", "Thành công", "Thành công", "Thành công",
                             "Thành công", "Thành công", "Thất bại", "Thành công", "Thành công",
                             "Thành công", "Thành công", "Thành công", "Thất bại", "Thành công",
                             "Thành công", "Thành công", "Thành công", "Thành công", "Thành công",
                             "Thành công", "Thành công", "Thành công", "Thành công", "Thành công"};
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        
        int ketQuaTimKiem = 0;
        
        // Tìm kiếm trong 30 bản ghi
        for (int i = 0; i < 30; i++) {
            // Kiểm tra tên đăng nhập hoặc IP có chứa từ khóa không
            if (tenDangNhap[i].toLowerCase().contains(tuKhoa.toLowerCase()) ||
                ipAddress[i].contains(tuKhoa) ||
                trangThai[i].toLowerCase().contains(tuKhoa.toLowerCase())) {
                
                // Tạo dữ liệu cho bản ghi tìm thấy
                cal.setTime(new Date());
                cal.add(java.util.Calendar.DAY_OF_MONTH, -(i % 7));
                cal.set(java.util.Calendar.HOUR_OF_DAY, 8 + (i % 12));
                cal.set(java.util.Calendar.MINUTE, (i * 15) % 60);
                cal.set(java.util.Calendar.SECOND, (i * 30) % 60);
                String thoiGianDangNhap = sdf.format(cal.getTime());
                
                String thoiGianDangXuat;
                if (trangThai[i].equals("Thành công")) {
                    cal.add(java.util.Calendar.HOUR_OF_DAY, 1 + (i % 8));
                    cal.set(java.util.Calendar.MINUTE, (i * 20) % 60);
                    thoiGianDangXuat = sdf.format(cal.getTime());
                } else {
                    thoiGianDangXuat = "Lỗi kết nối";
                }
                
                Object[] row = {i + 1, tenDangNhap[i], thoiGianDangNhap, thoiGianDangXuat, 
                               ipAddress[i], trangThai[i]};
                
                modelLichSu.addRow(row);
                ketQuaTimKiem++;
            }
        }
        
        if (ketQuaTimKiem == 0) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy kết quả nào cho từ khóa: " + tuKhoa, 
                "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Tìm thấy " + ketQuaTimKiem + " kết quả cho từ khóa: " + tuKhoa, 
                "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void xuatBaoCao() {
        // Chọn nơi lưu file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu báo cáo");
        fileChooser.setSelectedFile(new java.io.File("BaoCaoDangNhap_" + 
            java.time.LocalDate.now().toString() + ".txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                // Ghi header
                writer.write("=== BÁO CÁO LỊCH SỬ ĐĂNG NHẬP ===\n");
                writer.write("Ngày xuất báo cáo: " + java.time.LocalDateTime.now().toString() + "\n");
                writer.write("Tổng số bản ghi: " + modelLichSu.getRowCount() + "\n\n");
                
                // Ghi tiêu đề cột
                writer.write(String.format("%-5s %-20s %-25s %-25s %-15s %-15s\n", 
                    "STT", "Tên đăng nhập", "Thời gian đăng nhập", "Lần đăng nhập cuối", 
                    "Địa chỉ IP", "Trạng thái"));
                writer.write("=".repeat(110) + "\n");
                
                // Ghi dữ liệu
                for (int i = 0; i < modelLichSu.getRowCount(); i++) {
                    writer.write(String.format("%-5s %-20s %-25s %-25s %-15s %-15s\n",
                        modelLichSu.getValueAt(i, 0), // STT
                        modelLichSu.getValueAt(i, 1), // Tên đăng nhập
                        modelLichSu.getValueAt(i, 2), // Thời gian đăng nhập
                        modelLichSu.getValueAt(i, 3), // Lần đăng nhập cuối
                        modelLichSu.getValueAt(i, 4), // Địa chỉ IP
                        modelLichSu.getValueAt(i, 5)  // Trạng thái
                    ));
                }
                
                writer.write("\n=== KẾT THÚC BÁO CÁO ===\n");
                
                JOptionPane.showMessageDialog(this, 
                    "Xuất báo cáo thành công!\nFile đã được lưu tại: " + file.getAbsolutePath(), 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xuất báo cáo: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
