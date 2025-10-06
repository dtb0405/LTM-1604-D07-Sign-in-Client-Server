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
        
        // Panel chính chứa tìm kiếm và bảng
        JPanel panelChinh = new JPanel(new BorderLayout());
        panelChinh.setBackground(Color.WHITE);
        
        // Panel tìm kiếm
        JPanel panelTimKiem = taoPanelTimKiem();
        panelChinh.add(panelTimKiem, BorderLayout.NORTH);
        
        // Panel bảng
        JPanel panelBang = taoPanelBang();
        panelChinh.add(panelBang, BorderLayout.CENTER);
        
        add(panelChinh, BorderLayout.CENTER);
        
        // Panel nút
        JPanel panelNut = taoPanelNut();
        add(panelNut, BorderLayout.SOUTH);
    }
    
    private JPanel taoPanelTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Label tìm kiếm
        JLabel lblTimKiem = new JLabel("Tìm kiếm theo tên đăng nhập:");
        lblTimKiem.setFont(new Font("Arial", Font.BOLD, 12));
        lblTimKiem.setForeground(new Color(55, 71, 79));
        panel.add(lblTimKiem);
        
        // TextField tìm kiếm
        txtTimKiem = new JTextField(25);
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
        
        // Nút tìm kiếm
        JButton btnTimKiem = ButtonUtils.createSmallElevatedButton("Tìm kiếm", new Color(33, 150, 243));
        btnTimKiem.addActionListener(e -> timKiem());
        panel.add(btnTimKiem);
        
        // Nút làm mới
        JButton btnLamMoi = ButtonUtils.createSmallElevatedButton("Làm mới", new Color(76, 175, 80));
        btnLamMoi.addActionListener(e -> lamMoiTimKiem());
        panel.add(btnLamMoi);
        
        // Thêm sự kiện Enter cho textfield
        txtTimKiem.addActionListener(e -> timKiem());
        
        return panel;
    }
    
    private JPanel taoPanelBang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        // Tạo bảng
        String[] cot = {"STT", "Tên đăng nhập", "Thời gian đăng nhập", "Thời gian đăng xuất", 
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setPreferredSize(new Dimension(500, 100)); // Tăng kích thước để đảm bảo đủ không gian
        
        btnXuatBaoCao = ButtonUtils.createGreenCoolButton("Xuất Báo Cáo");
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
        
        return ButtonUtils.createElevatedButton(text, mauNen);
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
                            // Tạo row mới với STT và thay đổi cột thời gian đăng xuất thành Thời gian đăng xuất
                            Object[] newRow = {
                                stt++, // STT
                                record[1], // Tên đăng nhập
                                record[2], // Thời gian đăng nhập
                                record[3], // Thời gian đăng xuất (thay vì thời gian đăng xuất)
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
        
        // Lấy dữ liệu từ database
        KetNoiDatabase db = KetNoiDatabase.getInstance();
        List<String[]> lichSu = db.layLichSuDangNhap();
        
        // Xóa dữ liệu cũ
        modelLichSu.setRowCount(0);
        
        int ketQuaTimKiem = 0;
        
        if (lichSu != null && !lichSu.isEmpty()) {
            for (String[] record : lichSu) {
                // Kiểm tra tên đăng nhập có chứa từ khóa không
                if (record[1] != null && record[1].toLowerCase().contains(tuKhoa.toLowerCase())) {
                    Object[] newRow = {
                        ketQuaTimKiem + 1, // STT
                        record[1], // Tên đăng nhập
                        record[2], // Thời gian đăng nhập
                        record[3], // Thời gian đăng xuất
                        record[4], // Địa chỉ IP
                        record[5], // Trạng thái
                        record[6]  // Ghi chú
                    };
                    modelLichSu.addRow(newRow);
                    ketQuaTimKiem++;
                }
            }
        }
        
        if (ketQuaTimKiem == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy lịch sử đăng nhập nào phù hợp!", 
                "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Làm mới tìm kiếm
     */
    private void lamMoiTimKiem() {
        txtTimKiem.setText("");
        lamMoiDuLieu();
    }
    
    /**
     * Tìm kiếm tự động khi nhập
     */
    private void timKiemTuDong() {
        String tuKhoa = txtTimKiem.getText().trim();
        
        if (tuKhoa.isEmpty()) {
            lamMoiDuLieu();
            return;
        }
        
        // Lấy dữ liệu từ database
        KetNoiDatabase db = KetNoiDatabase.getInstance();
        List<String[]> lichSu = db.layLichSuDangNhap();
        
        // Xóa dữ liệu cũ
        modelLichSu.setRowCount(0);
        
        int ketQuaTimKiem = 0;
        
        if (lichSu != null && !lichSu.isEmpty()) {
            for (String[] record : lichSu) {
                // Kiểm tra tên đăng nhập có chứa từ khóa không
                if (record[1] != null && record[1].toLowerCase().contains(tuKhoa.toLowerCase())) {
                    Object[] newRow = {
                        ketQuaTimKiem + 1, // STT
                        record[1], // Tên đăng nhập
                        record[2], // Thời gian đăng nhập
                        record[3], // Thời gian đăng xuất
                        record[4], // Địa chỉ IP
                        record[5], // Trạng thái
                        record[6]  // Ghi chú
                    };
                    modelLichSu.addRow(newRow);
                    ketQuaTimKiem++;
                }
            }
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
                    "STT", "Tên đăng nhập", "Thời gian đăng nhập", "Thời gian đăng xuất", 
                    "Địa chỉ IP", "Trạng thái"));
                writer.write("=".repeat(110) + "\n");
                
                // Ghi dữ liệu
                for (int i = 0; i < modelLichSu.getRowCount(); i++) {
                    writer.write(String.format("%-5s %-20s %-25s %-25s %-15s %-15s\n",
                        modelLichSu.getValueAt(i, 0), // STT
                        modelLichSu.getValueAt(i, 1), // Tên đăng nhập
                        modelLichSu.getValueAt(i, 2), // Thời gian đăng nhập
                        modelLichSu.getValueAt(i, 3), // Thời gian đăng xuất
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
