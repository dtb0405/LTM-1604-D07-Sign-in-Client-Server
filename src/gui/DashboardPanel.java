package gui;

import client.KetNoiTCP;
import database.KetNoiDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.BasicStroke;
import java.util.Timer;

/**
 * Panel Dashboard hiển thị thống kê tổng quan
 */
public class DashboardPanel extends JPanel {
    @SuppressWarnings("unused")
    private KetNoiTCP ketNoi;
    
    private JLabel lblTongTaiKhoan;
    private JLabel lblTaiKhoanOnline;
    private JLabel lblTaiKhoanOffline;
    private JLabel lblTaiKhoanBiKhoa;
    private JLabel lblTaiKhoanHoatDong;
    private JLabel lblLuotDangNhapTrongNgay;
    
    private Timer timerCapNhat;
    private JButton btnXemBieuDo;
    
    public DashboardPanel(KetNoiTCP ketNoi) {
        this.ketNoi = ketNoi;
        
        khoiTaoGiaoDien();
        khoiTaoTimer();
        lamMoiDuLieu();
    }
    
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        
        // Tạo nền gradient đẹp cho toàn bộ panel
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient từ xanh nhạt đến trắng
                Color color1 = new Color(240, 248, 255); // Xanh rất nhạt
                Color color2 = new Color(255, 255, 255); // Trắng
                
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
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(-80, -80, 200, 200);
                g2d.fillOval(getWidth() - 120, getHeight() - 100, 180, 180);
            }
        };
        
        // Thêm tất cả component vào backgroundPanel
        add(backgroundPanel);
        
        // Panel tiêu đề
        JPanel panelTieuDe = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền trắng với bo góc
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Vẽ shadow
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(3, 3, getWidth(), getHeight(), 15, 15);
                
                // Vẽ border
                g2d.setColor(new Color(220, 220, 220));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        panelTieuDe.setOpaque(false);
        panelTieuDe.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel lblTieuDe = new JLabel("DASHBOARD - TỔNG QUAN HỆ THỐNG", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        lblTieuDe.setForeground(new Color(33, 150, 243));
        panelTieuDe.add(lblTieuDe, BorderLayout.CENTER);
        
        // Nút xem biểu đồ với tông màu lạnh
        btnXemBieuDo = ButtonUtils.createOrangeCoolButton("Xem Biểu Đồ");
        btnXemBieuDo.addActionListener(e -> moBieuDo());
        panelTieuDe.add(btnXemBieuDo, BorderLayout.EAST);
        
        backgroundPanel.add(panelTieuDe, BorderLayout.NORTH);
        
        // Panel thống kê
        JPanel panelThongKe = taoPanelThongKe();
        backgroundPanel.add(panelThongKe, BorderLayout.CENTER);
    }
    
    private JPanel taoPanelThongKe() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);
        
        // Tổng số tài khoản - Pastel hồng nhạt
        JPanel cardTongTaiKhoan = taoCardThongKe("TỔNG SỐ TÀI KHOẢN", "0", 
            new Color(255, 160, 170), "");
        lblTongTaiKhoan = (JLabel) ((JPanel) cardTongTaiKhoan.getComponent(1)).getComponent(0);
        panel.add(cardTongTaiKhoan);
        
        // Tài khoản online - Pastel hồng rất nhạt
        JPanel cardOnline = taoCardThongKe("TÀI KHOẢN ONLINE", "0", 
            new Color(255, 190, 190), "");
        lblTaiKhoanOnline = (JLabel) ((JPanel) cardOnline.getComponent(1)).getComponent(0);
        panel.add(cardOnline);
        
        // Tài khoản offline - Pastel cam đào
        JPanel cardOffline = taoCardThongKe("TÀI KHOẢN OFFLINE", "0", 
            new Color(255, 220, 190), "");
        lblTaiKhoanOffline = (JLabel) ((JPanel) cardOffline.getComponent(1)).getComponent(0);
        panel.add(cardOffline);
        
        // Tài khoản bị khóa - Pastel xanh lá nhạt
        JPanel cardBiKhoa = taoCardThongKe("TÀI KHOẢN BỊ KHÓA", "0", 
            new Color(220, 255, 220), "");
        lblTaiKhoanBiKhoa = (JLabel) ((JPanel) cardBiKhoa.getComponent(1)).getComponent(0);
        panel.add(cardBiKhoa);
        
        // Tài khoản hoạt động - Pastel xanh mint
        JPanel cardHoatDong = taoCardThongKe("TÀI KHOẢN HOẠT ĐỘNG", "0", 
            new Color(190, 255, 220), "");
        lblTaiKhoanHoatDong = (JLabel) ((JPanel) cardHoatDong.getComponent(1)).getComponent(0);
        panel.add(cardHoatDong);
        
        // Lượt đăng nhập trong ngày - Pastel xanh aqua
        JPanel cardDangNhapTrongNgay = taoCardThongKe("LƯỢT ĐĂNG NHẬP HÔM NAY", "0", 
            new Color(190, 240, 240), "");
        lblLuotDangNhapTrongNgay = (JLabel) ((JPanel) cardDangNhapTrongNgay.getComponent(1)).getComponent(0);
        panel.add(cardDangNhapTrongNgay);
        
        return panel;
    }
    
    private JPanel taoCardThongKe(String tieuDe, String giaTri, Color mauNen, String icon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                
                int width = getWidth();
                int height = getHeight();
                int arcRadius = 20;
                
                // Vẽ shadow đa lớp cho hiệu ứng đẹp hơn
                // Shadow 1 - đậm nhất
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(4, 4, width - 4, height - 4, arcRadius, arcRadius);
                
                // Shadow 2 - trung bình
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(3, 3, width - 3, height - 3, arcRadius, arcRadius);
                
                // Shadow 3 - nhạt nhất
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(2, 2, width - 2, height - 2, arcRadius, arcRadius);
                
                // Vẽ background chính với gradient nhẹ
                g2d.setColor(mauNen);
                g2d.fillRoundRect(0, 0, width - 2, height - 2, arcRadius, arcRadius);
                
                // Vẽ highlight trên cùng
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, width - 2, height - 2, arcRadius, arcRadius);
                
                // Vẽ border tinh tế
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(1, 1, width - 4, height - 4, arcRadius - 2, arcRadius - 2);
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(200, 120));
        
        // Panel tiêu đề - căn chính giữa
        JPanel panelTieuDe = new JPanel(new GridBagLayout());
        panelTieuDe.setOpaque(false);
        
        JLabel lblTieuDe = new JLabel(tieuDe) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ shadow cho text
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, x + 1, y + 1);
                
                // Vẽ text chính
                g2d.setColor(Color.BLACK);
                g2d.drawString(text, x, y);
                
                g2d.dispose();
            }
        };
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16)); // Tăng từ 14 lên 16
        lblTieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 10, 5, 10); // Thêm padding: top, left, bottom, right
        panelTieuDe.add(lblTieuDe, gbc);
        
        // Panel giá trị - căn chính giữa
        JPanel panelGiaTri = new JPanel(new GridBagLayout());
        panelGiaTri.setOpaque(false);
        
        JLabel lblGiaTri = new JLabel(giaTri) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ shadow cho text
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                
                // Vẽ shadow ở 4 hướng
                g2d.drawString(text, x + 1, y + 1);
                g2d.drawString(text, x - 1, y - 1);
                g2d.drawString(text, x + 1, y - 1);
                g2d.drawString(text, x - 1, y + 1);
                
                // Vẽ text đen
                g2d.setColor(Color.BLACK);
                g2d.drawString(text, x, y);
                
                g2d.dispose();
            }
        };
        lblGiaTri.setFont(new Font("Arial", Font.BOLD, 42)); // Tăng từ 36 lên 42
        lblGiaTri.setHorizontalAlignment(SwingConstants.CENTER);
        
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.anchor = GridBagConstraints.CENTER;
        gbc2.insets = new Insets(5, 10, 15, 10); // Thêm padding: top, left, bottom, right
        panelGiaTri.add(lblGiaTri, gbc2);
        
        card.add(panelTieuDe, BorderLayout.NORTH);
        card.add(panelGiaTri, BorderLayout.CENTER);
        
        return card;
    }
    
    private void khoiTaoTimer() {
        // Không sử dụng timer tự động nữa
        // Dữ liệu sẽ được cập nhật khi cần thiết
    }
    
    public void lamMoiDuLieu() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int tongTaiKhoan = 0;
            private int taiKhoanOnline = 0;
            private int taiKhoanBiKhoa = 0;
            private int taiKhoanHoatDong = 0;
            private int luotDangNhapTrongNgay = 0;
            private boolean thanhCong = false;
            
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Lấy dữ liệu thực tế từ database
                    KetNoiDatabase db = KetNoiDatabase.getInstance();
                    if (db.getConnection() != null) {
                        // Lấy dữ liệu thực tế từ database
                        tongTaiKhoan = db.demTongTaiKhoan();
                        taiKhoanBiKhoa = db.demTaiKhoanBiKhoa();
                        taiKhoanHoatDong = db.demTaiKhoanHoatDong();
                        luotDangNhapTrongNgay = db.demLuotDangNhapTrongNgay();
                        
                        // Số người online thực tế từ database
                        taiKhoanOnline = db.demTaiKhoanOnline();
                        
                        thanhCong = true;
                    } else {
                        // Dữ liệu mặc định khi không có kết nối database
                        tongTaiKhoan = 0;
                        taiKhoanOnline = 0;
                        taiKhoanBiKhoa = 0;
                        taiKhoanHoatDong = 0;
                        luotDangNhapTrongNgay = 0;
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi lấy dữ liệu thống kê từ database: " + e.getMessage());
                    thanhCong = false;
                }
                
                return null;
            }
            
            @Override
            protected void done() {
                if (thanhCong) {
                    // Cập nhật giao diện với dữ liệu thực tế
                    lblTongTaiKhoan.setText(String.valueOf(tongTaiKhoan));
                    lblTaiKhoanOnline.setText(String.valueOf(taiKhoanOnline));
                    lblTaiKhoanOffline.setText(String.valueOf(tongTaiKhoan - taiKhoanOnline));
                    lblTaiKhoanBiKhoa.setText(String.valueOf(taiKhoanBiKhoa));
                    lblTaiKhoanHoatDong.setText(String.valueOf(taiKhoanHoatDong));
                    lblLuotDangNhapTrongNgay.setText(String.valueOf(luotDangNhapTrongNgay));
                    
                    // Thêm hiệu ứng cập nhật
                    for (JLabel label : new JLabel[]{lblTongTaiKhoan, lblTaiKhoanOnline, 
                            lblTaiKhoanOffline, lblTaiKhoanBiKhoa, lblTaiKhoanHoatDong, lblLuotDangNhapTrongNgay}) {
                        label.setForeground(Color.WHITE);
                        label.setFont(new Font("Arial", Font.BOLD, 32));
                    }
                } else {
                    // Hiển thị lỗi
                    lblTongTaiKhoan.setText("--");
                    lblTaiKhoanOnline.setText("--");
                    lblTaiKhoanOffline.setText("--");
                    lblTaiKhoanBiKhoa.setText("--");
                    lblTaiKhoanHoatDong.setText("--");
                    lblLuotDangNhapTrongNgay.setText("--");
                }
                
                repaint();
            }
        };
        
        worker.execute();
    }
    
    public void dungTimer() {
        if (timerCapNhat != null) {
            timerCapNhat.cancel();
        }
    }
    
    /**
     * Mở cửa sổ biểu đồ
     */
    private void moBieuDo() {
        JFrame frameBieuDo = new JFrame("Biểu Đồ Thống Kê");
        frameBieuDo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameBieuDo.setSize(1200, 800);
        frameBieuDo.setMinimumSize(new Dimension(800, 600));
        frameBieuDo.setMaximumSize(new Dimension(1600, 1200));
        frameBieuDo.setLocationRelativeTo(this);
        frameBieuDo.setResizable(true);
        
        BieuDoPanel bieuDoPanel = new BieuDoPanel();
        frameBieuDo.add(bieuDoPanel);
        
        // Thêm listener để dừng timer khi đóng cửa sổ
        frameBieuDo.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                bieuDoPanel.dungCapNhatRealTime();
            }
        });
        
        frameBieuDo.setVisible(true);
    }
}
