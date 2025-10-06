package gui;

import database.KetNoiDatabase;
import database.TaiKhoan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Panel hiển thị biểu đồ thống kê
 */
public class BieuDoPanel extends JPanel {
    private KetNoiDatabase database;
    private Timer updateTimer;
    private JPanel panelTrangThai, panelOnline, panelDangNhap, panelThongKe;
    
    public BieuDoPanel() {
        this.database = KetNoiDatabase.getInstance();
        khoiTaoGiaoDien();
        batDauCapNhatRealTime();
    }
    
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Panel tiêu đề
        JPanel panelTieuDe = new JPanel(new BorderLayout());
        panelTieuDe.setBackground(new Color(25, 118, 210));
        panelTieuDe.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTieuDe = new JLabel("BIỂU ĐỒ THỐNG KÊ", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        lblTieuDe.setForeground(Color.WHITE);
        panelTieuDe.add(lblTieuDe, BorderLayout.CENTER);
        
        add(panelTieuDe, BorderLayout.NORTH);
        
        // Panel chính chứa các biểu đồ với layout linh hoạt
        JPanel panelChinh = new JPanel(new GridBagLayout());
        panelChinh.setBackground(new Color(245, 247, 250));
        panelChinh.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Biểu đồ tròn - Trạng thái tài khoản
        panelTrangThai = taoPanelBieuDoTron("Trạng thái tài khoản", "Hoạt động", "Bị khóa");
        gbc.gridx = 0; gbc.gridy = 0;
        panelChinh.add(panelTrangThai, gbc);
        
        // Biểu đồ tròn - Trạng thái online
        panelOnline = taoPanelBieuDoTron("Trạng thái online", "Online", "Offline");
        gbc.gridx = 1; gbc.gridy = 0;
        panelChinh.add(panelOnline, gbc);
        
        // Biểu đồ cột - Đăng nhập theo ngày
        panelDangNhap = taoPanelBieuDoCot("Lượt đăng nhập theo ngày");
        gbc.gridx = 0; gbc.gridy = 1;
        panelChinh.add(panelDangNhap, gbc);
        
        // Panel thống kê tổng quan
        panelThongKe = taoPanelThongKe();
        gbc.gridx = 1; gbc.gridy = 1;
        panelChinh.add(panelThongKe, gbc);
        
        add(panelChinh, BorderLayout.CENTER);
    }
    
    private JPanel taoPanelBieuDoTron(String tieuDe, String label1, String label2) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền trắng với bo góc mềm mại
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Vẽ shadow mềm mại
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 20, 20);
                
                // Vẽ border mềm mại
                g2d.setColor(new Color(230, 235, 240));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Tiêu đề
        JLabel lblTieuDe = new JLabel(tieuDe, SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDe.setForeground(new Color(33, 150, 243));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTieuDe, BorderLayout.NORTH);
        
        // Biểu đồ tròn
        JPanel panelBieuDo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Lấy dữ liệu
                int soLuong1 = 0, soLuong2 = 0;
                if ("Trạng thái tài khoản".equals(tieuDe)) {
                    soLuong1 = laySoLuongTaiKhoanHoatDong();
                    soLuong2 = laySoLuongTaiKhoanBiKhoa();
                } else if ("Trạng thái online".equals(tieuDe)) {
                    soLuong1 = laySoLuongTaiKhoanOnline();
                    soLuong2 = laySoLuongTaiKhoanOffline();
                }
                
                int tong = soLuong1 + soLuong2;
                if (tong == 0) {
                    // Vẽ vòng tròn trống
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.fillOval(50, 50, 100, 100);
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(60, 60, 80, 80);
                    
                    // Vẽ text "Không có dữ liệu"
                    g2d.setColor(Color.GRAY);
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    FontMetrics fm = g2d.getFontMetrics();
                    String text = "Không có dữ liệu";
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = getHeight() / 2;
                    g2d.drawString(text, x, y);
                    return;
                }
                
                // Vẽ biểu đồ tròn - thích ứng với kích thước
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = Math.min(getWidth(), getHeight()) / 3;
                int x = centerX - radius;
                int y = centerY - radius;
                int width = radius * 2;
                int height = radius * 2;
                
                // Vẽ shadow cho biểu đồ
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillOval(x + 3, y + 3, width, height);
                
                // Phần 1
                if (soLuong1 > 0) {
                    g2d.setColor(new Color(165, 214, 167)); // Xanh lá nhạt
                    g2d.fillArc(x, y, width, height, 0, (int) (360 * soLuong1 / tong));
                }
                
                // Phần 2
                if (soLuong2 > 0) {
                    g2d.setColor(new Color(255, 138, 101)); // Đỏ nhạt
                    g2d.fillArc(x, y, width, height, (int) (360 * soLuong1 / tong), (int) (360 * soLuong2 / tong));
                }
                
                // Vẽ border mềm mại
                g2d.setColor(new Color(100, 100, 100));
                g2d.setStroke(new BasicStroke(2.5f));
                g2d.drawOval(x, y, width, height);
                
                // Vẽ text ở giữa
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                String text = String.valueOf(tong);
                int textX = x + (width - fm.stringWidth(text)) / 2;
                int textY = y + height / 2 + fm.getAscent() / 2;
                g2d.drawString(text, textX, textY);
            }
        };
        panelBieuDo.setPreferredSize(new Dimension(200, 200));
        panel.add(panelBieuDo, BorderLayout.CENTER);
        
        // Legend
        JPanel panelLegend = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelLegend.setBackground(Color.WHITE);
        
        // Legend item 1
        JPanel legendItem1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        legendItem1.setBackground(Color.WHITE);
        JLabel colorLabel1 = new JLabel("●");
        colorLabel1.setForeground(new Color(165, 214, 167)); // Xanh lá nhạt
        colorLabel1.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel textLabel1 = new JLabel();
        textLabel1.setFont(new Font("Arial", Font.PLAIN, 12));
        legendItem1.add(colorLabel1);
        legendItem1.add(textLabel1);
        panelLegend.add(legendItem1);
        
        // Legend item 2
        JPanel legendItem2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        legendItem2.setBackground(Color.WHITE);
        JLabel colorLabel2 = new JLabel("●");
        colorLabel2.setForeground(new Color(255, 138, 101)); // Đỏ nhạt
        colorLabel2.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel textLabel2 = new JLabel();
        textLabel2.setFont(new Font("Arial", Font.PLAIN, 12));
        legendItem2.add(colorLabel2);
        legendItem2.add(textLabel2);
        panelLegend.add(legendItem2);
        
        // Cập nhật text cho legend
        capNhatLegendText(textLabel1, textLabel2, tieuDe, label1, label2);
        
        panel.add(panelLegend, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel taoPanelBieuDoCot(String tieuDe) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền trắng với bo góc mềm mại
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Vẽ shadow mềm mại
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 20, 20);
                
                // Vẽ border mềm mại
                g2d.setColor(new Color(230, 235, 240));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Tiêu đề
        JLabel lblTieuDe = new JLabel(tieuDe, SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDe.setForeground(new Color(33, 150, 243));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTieuDe, BorderLayout.NORTH);
        
        // Biểu đồ cột
        JPanel panelBieuDo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Lấy dữ liệu 7 ngày gần nhất
                Map<String, Integer> data = layDuLieuDangNhap7Ngay();
                
                if (data.isEmpty()) {
                    // Vẽ text "Không có dữ liệu"
                    g2d.setColor(Color.GRAY);
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    FontMetrics fm = g2d.getFontMetrics();
                    String text = "Không có dữ liệu";
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = getHeight() / 2;
                    g2d.drawString(text, x, y);
                    return;
                }
                
                // Vẽ biểu đồ cột - thích ứng với kích thước
                int availableWidth = getWidth() - 50;
                int availableHeight = getHeight() - 80;
                int barCount = data.size();
                int barWidth = Math.max(25, (availableWidth - (barCount - 1) * 15) / barCount);
                int spacing = 15;
                int startX = (getWidth() - (barCount * barWidth + (barCount - 1) * spacing)) / 2;
                int maxHeight = availableHeight;
                
                // Tìm giá trị lớn nhất để scale
                int maxValue = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);
                
                int x = startX;
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    int value = entry.getValue();
                    int barHeight = (int) ((double) value / maxValue * maxHeight);
                    
                    int baseY = getHeight() - 40;
                    
                    // Vẽ shadow cho cột
                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.fillRoundRect(x + 2, baseY - barHeight + 2, barWidth, barHeight, 5, 5);
                    
                    // Vẽ cột với bo góc - màu nhạt hơn
                    g2d.setColor(new Color(144, 202, 249)); // Xanh dương nhạt
                    g2d.fillRoundRect(x, baseY - barHeight, barWidth, barHeight, 5, 5);
                    
                    // Vẽ border mềm mại
                    g2d.setColor(new Color(100, 181, 246)); // Xanh dương nhạt hơn
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawRoundRect(x, baseY - barHeight, barWidth, barHeight, 5, 5);
                    
                    // Vẽ giá trị trực tiếp trên cột
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    FontMetrics fm = g2d.getFontMetrics();
                    String valueText = String.valueOf(value);
                    
                    // Tính toán vị trí chính giữa cột
                    int centerX = x + barWidth / 2;
                    int textX = centerX - fm.stringWidth(valueText) / 2;
                    int textY = baseY - barHeight / 2 + fm.getAscent() / 2; // Đặt text ở giữa cột
                    
                    // Vẽ text trực tiếp với màu trắng
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(valueText, textX, textY);
                    
                    // Vẽ ngày
                    g2d.setColor(new Color(55, 71, 79));
                    g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                    fm = g2d.getFontMetrics();
                    String dateText = entry.getKey();
                    int dateX = x + (barWidth - fm.stringWidth(dateText)) / 2;
                    int dateY = baseY + 15;
                    g2d.drawString(dateText, dateX, dateY);
                    
                    x += barWidth + spacing;
                }
            }
        };
        panelBieuDo.setPreferredSize(new Dimension(300, 200));
        panel.add(panelBieuDo, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel taoPanelThongKe() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền trắng với bo góc mềm mại
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Vẽ shadow mềm mại
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 20, 20);
                
                // Vẽ border mềm mại
                g2d.setColor(new Color(230, 235, 240));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Tiêu đề
        JLabel lblTieuDe = new JLabel("Thống kê tổng quan", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDe.setForeground(new Color(33, 150, 243));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTieuDe, BorderLayout.NORTH);
        
        // Nội dung thống kê với layout linh hoạt
        JPanel panelNoiDung = new JPanel(new GridBagLayout());
        panelNoiDung.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 15, 8, 15);
        
        // Tổng số tài khoản
        JLabel lblTongTaiKhoan = new JLabel("Tổng số tài khoản: " + layTongSoTaiKhoan());
        lblTongTaiKhoan.setFont(new Font("Arial", Font.BOLD, 13));
        lblTongTaiKhoan.setForeground(new Color(55, 71, 79));
        gbc.gridx = 0; gbc.gridy = 0;
        panelNoiDung.add(lblTongTaiKhoan, gbc);
        
        // Tài khoản hoạt động
        JLabel lblTaiKhoanHoatDong = new JLabel("Tài khoản hoạt động: " + laySoLuongTaiKhoanHoatDong());
        lblTaiKhoanHoatDong.setFont(new Font("Arial", Font.BOLD, 13));
        lblTaiKhoanHoatDong.setForeground(new Color(165, 214, 167)); // Xanh lá nhạt
        gbc.gridx = 0; gbc.gridy = 1;
        panelNoiDung.add(lblTaiKhoanHoatDong, gbc);
        
        // Tài khoản bị khóa
        JLabel lblTaiKhoanBiKhoa = new JLabel("Tài khoản bị khóa: " + laySoLuongTaiKhoanBiKhoa());
        lblTaiKhoanBiKhoa.setFont(new Font("Arial", Font.BOLD, 13));
        lblTaiKhoanBiKhoa.setForeground(new Color(255, 138, 101)); // Đỏ nhạt
        gbc.gridx = 0; gbc.gridy = 2;
        panelNoiDung.add(lblTaiKhoanBiKhoa, gbc);
        
        // Tài khoản online
        JLabel lblTaiKhoanOnline = new JLabel("Tài khoản online: " + laySoLuongTaiKhoanOnline());
        lblTaiKhoanOnline.setFont(new Font("Arial", Font.BOLD, 13));
        lblTaiKhoanOnline.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0; gbc.gridy = 3;
        panelNoiDung.add(lblTaiKhoanOnline, gbc);
        
        // Lượt đăng nhập hôm nay
        JLabel lblLuotDangNhapHomNay = new JLabel("Lượt đăng nhập hôm nay: " + layLuotDangNhapHomNay());
        lblLuotDangNhapHomNay.setFont(new Font("Arial", Font.BOLD, 13));
        lblLuotDangNhapHomNay.setForeground(new Color(255, 152, 0)); // Cam
        gbc.gridx = 0; gbc.gridy = 4;
        panelNoiDung.add(lblLuotDangNhapHomNay, gbc);
        
        panel.add(panelNoiDung, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Các method lấy dữ liệu
    private int layTongSoTaiKhoan() {
        try {
            List<TaiKhoan> danhSach = database.layDanhSachTaiKhoan();
            return danhSach != null ? danhSach.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int laySoLuongTaiKhoanHoatDong() {
        try {
            List<TaiKhoan> danhSach = database.layDanhSachTaiKhoan();
            if (danhSach == null) return 0;
            return (int) danhSach.stream().filter(TaiKhoan::taiKhoanHoatDong).count();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int laySoLuongTaiKhoanBiKhoa() {
        try {
            List<TaiKhoan> danhSach = database.layDanhSachTaiKhoan();
            if (danhSach == null) return 0;
            return (int) danhSach.stream().filter(tk -> !tk.taiKhoanHoatDong()).count();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int laySoLuongTaiKhoanOnline() {
        try {
            List<TaiKhoan> danhSach = database.layDanhSachTaiKhoan();
            if (danhSach == null) return 0;
            return (int) danhSach.stream().filter(TaiKhoan::isOnline).count();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int laySoLuongTaiKhoanOffline() {
        try {
            List<TaiKhoan> danhSach = database.layDanhSachTaiKhoan();
            if (danhSach == null) return 0;
            return (int) danhSach.stream().filter(tk -> !tk.isOnline()).count();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private Map<String, Integer> layDuLieuDangNhap7Ngay() {
        Map<String, Integer> data = new LinkedHashMap<>();
        try {
            if (database == null) {
                return data;
            }
            
            // Lấy dữ liệu thực từ database cho 7 ngày gần nhất
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            // Khởi tạo tất cả 7 ngày với giá trị 0
            for (int i = 6; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_MONTH, -i);
                String dateStr = sdf.format(cal.getTime());
                data.put(dateStr, 0);
            }
            
            // Lấy dữ liệu thực từ database - chỉ đếm đăng nhập thành công
            String sql = "SELECT DATE(thoi_gian_dang_nhap) as ngay, COUNT(*) as so_luong " +
                        "FROM lich_su_dang_nhap " +
                        "WHERE thoi_gian_dang_nhap >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                        "AND trang_thai = 'thanh_cong' " +
                        "GROUP BY DATE(thoi_gian_dang_nhap) " +
                        "ORDER BY ngay";
            
            java.sql.PreparedStatement stmt = database.getConnection().prepareStatement(sql);
            java.sql.ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String dbDate = rs.getString("ngay");
                int soLuong = rs.getInt("so_luong");
                
                // Chuyển đổi từ yyyy-MM-dd sang dd/MM
                try {
                    java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dbDate);
                    String dateStr = sdf.format(date);
                    data.put(dateStr, soLuong);
                } catch (Exception e) {
                    // Bỏ qua nếu không parse được
                }
            }
            
            rs.close();
            stmt.close();
            
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy dữ liệu đăng nhập 7 ngày: " + e.getMessage());
            // Trả về dữ liệu mặc định nếu có lỗi
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
            for (int i = 6; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_MONTH, -i);
                String dateStr = sdf.format(cal.getTime());
                data.put(dateStr, 0);
            }
        }
        return data;
    }
    
    /**
     * Bắt đầu cập nhật dữ liệu real-time
     */
    private void batDauCapNhatRealTime() {
        updateTimer = new Timer("ChartUpdateTimer", true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    capNhatDuLieu();
                });
            }
        }, 0, 5000); // Cập nhật mỗi 5 giây
    }
    
    /**
     * Cập nhật dữ liệu cho tất cả biểu đồ
     */
    private void capNhatDuLieu() {
        if (panelTrangThai != null) {
            panelTrangThai.repaint();
            capNhatLegendTrongPanel(panelTrangThai, "Trạng thái tài khoản", "Hoạt động", "Bị khóa");
        }
        if (panelOnline != null) {
            panelOnline.repaint();
            capNhatLegendTrongPanel(panelOnline, "Trạng thái online", "Online", "Offline");
        }
        if (panelDangNhap != null) {
            panelDangNhap.repaint();
        }
        if (panelThongKe != null) {
            capNhatPanelThongKe();
        }
    }
    
    /**
     * Cập nhật legend trong panel
     */
    private void capNhatLegendTrongPanel(JPanel panel, String tieuDe, String label1, String label2) {
        // Tìm panel legend và cập nhật text
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel subPanel = (JPanel) comp;
                Component[] subComponents = subPanel.getComponents();
                for (Component subComp : subComponents) {
                    if (subComp instanceof JLabel) {
                        JLabel label = (JLabel) subComp;
                        String text = label.getText();
                        if (text.contains(label1 + ":")) {
                            int soLuong = tieuDe.equals("Trạng thái tài khoản") ? 
                                laySoLuongTaiKhoanHoatDong() : laySoLuongTaiKhoanOnline();
                            label.setText(label1 + ": " + soLuong);
                        } else if (text.contains(label2 + ":")) {
                            int soLuong = tieuDe.equals("Trạng thái tài khoản") ? 
                                laySoLuongTaiKhoanBiKhoa() : laySoLuongTaiKhoanOffline();
                            label.setText(label2 + ": " + soLuong);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Cập nhật panel thống kê với dữ liệu mới nhất
     */
    private void capNhatPanelThongKe() {
        if (panelThongKe == null) return;
        
        // Tìm và cập nhật các label trong panel thống kê
        Component[] components = panelThongKe.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] subComponents = panel.getComponents();
                for (Component subComp : subComponents) {
                    if (subComp instanceof JLabel) {
                        JLabel label = (JLabel) subComp;
                        String text = label.getText();
                        
                        // Cập nhật dữ liệu dựa trên text hiện tại
                        if (text.contains("Tổng số tài khoản:")) {
                            label.setText("Tổng số tài khoản: " + layTongSoTaiKhoan());
                        } else if (text.contains("Tài khoản hoạt động:")) {
                            label.setText("Tài khoản hoạt động: " + laySoLuongTaiKhoanHoatDong());
                        } else if (text.contains("Tài khoản bị khóa:")) {
                            label.setText("Tài khoản bị khóa: " + laySoLuongTaiKhoanBiKhoa());
                        } else if (text.contains("Tài khoản online:")) {
                            label.setText("Tài khoản online: " + laySoLuongTaiKhoanOnline());
                        } else if (text.contains("Lượt đăng nhập hôm nay:")) {
                            label.setText("Lượt đăng nhập hôm nay: " + layLuotDangNhapHomNay());
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Dừng cập nhật real-time
     */
    public void dungCapNhatRealTime() {
        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }
    }
    
    /**
     * Làm mới dữ liệu ngay lập tức
     */
    public void lamMoiDuLieu() {
        capNhatDuLieu();
    }
    
    /**
     * Lấy số lượt đăng nhập hôm nay (đồng bộ với dashboard)
     */
    public int layLuotDangNhapHomNay() {
        try {
            if (database == null) return 0;
            return database.demLuotDangNhapTrongNgay();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Cập nhật text cho legend
     */
    private void capNhatLegendText(JLabel textLabel1, JLabel textLabel2, String tieuDe, String label1, String label2) {
        int soLuong1 = 0, soLuong2 = 0;
        if ("Trạng thái tài khoản".equals(tieuDe)) {
            soLuong1 = laySoLuongTaiKhoanHoatDong();
            soLuong2 = laySoLuongTaiKhoanBiKhoa();
        } else if ("Trạng thái online".equals(tieuDe)) {
            soLuong1 = laySoLuongTaiKhoanOnline();
            soLuong2 = laySoLuongTaiKhoanOffline();
        }
        
        textLabel1.setText(label1 + ": " + soLuong1);
        textLabel2.setText(label2 + ": " + soLuong2);
    }
}
