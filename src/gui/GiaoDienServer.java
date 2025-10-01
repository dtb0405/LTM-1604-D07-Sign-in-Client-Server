package gui;

import server.MayChuTCP;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Giao diện quản lý server
 */
public class GiaoDienServer extends JFrame {
    private MayChuTCP server;
    private JButton btnBatServer;
    private JButton btnTatServer;
    private JTextArea txtAreaLog;
    private JLabel lblTrangThai;
    private Timer timerCapNhatLog;
    
    private static final int CONG_MAC_DINH = 2712;
    
    public GiaoDienServer() {
        server = new MayChuTCP();
        khoiTaoGiaoDien();
    }
    
    private void khoiTaoGiaoDien() {
        setTitle("Quản Lý Server - Hệ Thống Đăng Nhập");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Thiết lập nền gradient đẹp
        getContentPane().setBackground(new Color(248, 250, 252));
        
        // Tạo nền gradient cho toàn bộ frame
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient từ xanh lá nhạt đến trắng
                Color color1 = new Color(240, 255, 240); // Xanh lá rất nhạt
                Color color2 = new Color(255, 255, 255); // Trắng
                Color color3 = new Color(245, 255, 245); // Xanh lá cực nhạt
                
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
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillOval(-60, -60, 180, 180);
                g2d.fillOval(getWidth() - 120, getHeight() - 80, 150, 150);
            }
        };
        
        // Thêm tất cả component vào backgroundPanel
        add(backgroundPanel);
        
        // Panel điều khiển
        JPanel panelDieuKhien = taoPanelDieuKhien();
        backgroundPanel.add(panelDieuKhien, BorderLayout.NORTH);
        
        // Panel thông tin
        JPanel panelThongTin = taoPanelThongTin();
        backgroundPanel.add(panelThongTin, BorderLayout.CENTER);
        
        // Panel log
        JPanel panelLog = taoPanelLog();
        backgroundPanel.add(panelLog, BorderLayout.SOUTH);
        
        // Xử lý sự kiện đóng cửa sổ
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ghiLog("[Giao diện] Đang đóng cửa sổ giao diện server.");
                tatServer();
                ghiLog("[Giao diện] Đã đóng server và thoát ứng dụng.");
                System.exit(0);
            }
        });
        
        pack();
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Cập nhật trạng thái ban đầu
        capNhatTrangThai();
    }
    
    private JPanel taoPanelDieuKhien() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ background với gradient
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Vẽ shadow
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 15, 15);
                
                // Vẽ background chính
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 15, 15);
                
                // Vẽ border
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 15, 15);
            }
        };
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        
        // Nút bật server với thiết kế hiện đại
        btnBatServer = taoNutHienDai("Bật Server", new Color(46, 125, 50));
        btnBatServer.addActionListener(e -> batServer());
        panel.add(btnBatServer);
        
        // Nút tắt server với thiết kế hiện đại
        btnTatServer = taoNutHienDai("Tắt Server", new Color(244, 67, 54));
        btnTatServer.setEnabled(false);
        btnTatServer.addActionListener(e -> tatServer());
        panel.add(btnTatServer);
        
        // Nút xóa log với thiết kế hiện đại
        JButton btnXoaLog = taoNutHienDai("Xóa Log", new Color(255, 152, 0));
        btnXoaLog.addActionListener(e -> xoaLog());
        panel.add(btnXoaLog);
        
        return panel;
    }
    
    private JPanel taoPanelThongTin() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ background với gradient
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Vẽ shadow
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(3, 3, getWidth(), getHeight(), 20, 20);
                
                // Vẽ background chính
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);
                
                // Vẽ border
                g2d.setColor(new Color(220, 220, 220));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);
            }
        };
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setPreferredSize(new Dimension(0, 120));
        
        // Trạng thái server và thông tin trên cùng một dòng
        JLabel lblTrangThaiLabel = new JLabel("Trạng thái:");
        lblTrangThaiLabel.setFont(new Font("Arial", Font.BOLD, 12));
        lblTrangThaiLabel.setForeground(new Color(55, 71, 79));
        panel.add(lblTrangThaiLabel);
        
        lblTrangThai = new JLabel("Đã dừng");
        lblTrangThai.setFont(new Font("Arial", Font.BOLD, 14));
        lblTrangThai.setForeground(new Color(244, 67, 54));
        panel.add(lblTrangThai);
        
        // Thêm thông tin cổng trên cùng dòng
        JLabel lblCongLabel = new JLabel(" | Cổng:");
        lblCongLabel.setFont(new Font("Arial", Font.BOLD, 12));
        lblCongLabel.setForeground(new Color(55, 71, 79));
        panel.add(lblCongLabel);
        
        JLabel lblCongHienTai = new JLabel(String.valueOf(CONG_MAC_DINH));
        lblCongHienTai.setFont(new Font("Arial", Font.BOLD, 14));
        lblCongHienTai.setForeground(new Color(25, 118, 210));
        panel.add(lblCongHienTai);
        
        
        return panel;
    }
    
    private JPanel taoPanelLog() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ background với gradient
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Vẽ shadow
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 15, 15);
                
                // Vẽ background chính
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 15, 15);
                
                // Vẽ border
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 15, 15);
            }
        };
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        txtAreaLog = new JTextArea(15, 80);
        txtAreaLog.setEditable(false);
        txtAreaLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtAreaLog.setBackground(new Color(30, 30, 30)); // Nền đen nhẹ
        txtAreaLog.setForeground(new Color(0, 255, 0)); // Chữ xanh lá
        txtAreaLog.setCaretColor(new Color(0, 255, 0));
        txtAreaLog.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        JScrollPane scrollPane = new JScrollPane(txtAreaLog);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
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
        
        button.setPreferredSize(new Dimension(160, 50));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }
    
    
    private void batServer() {
        int cong = CONG_MAC_DINH;
        
        ghiLog("[Giao diện] Người dùng bấm nút Bật Server.");
        if (server.khoidongServer(cong)) {
            btnBatServer.setEnabled(false);
            btnTatServer.setEnabled(true);
            ghiLog("[Giao diện] Server đã được khởi động thành công trên cổng " + cong);
            JOptionPane.showMessageDialog(this, 
                "Server đã được khởi động thành công trên cổng " + cong,
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            ghiLog("[Giao diện] Không thể khởi động server. Vui lòng kiểm tra cổng và thử lại.");
            JOptionPane.showMessageDialog(this, 
                "Không thể khởi động server. Vui lòng kiểm tra cổng và thử lại.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        capNhatTrangThai();
        capNhatLog();
        khoiTaoTimer();
    }
    
    private void tatServer() {
        ghiLog("[Giao diện] Người dùng bấm nút Tắt Server.");
        if (server.isDangChay()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn tắt server?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                server.dungServer();
                btnBatServer.setEnabled(true);
                btnTatServer.setEnabled(false);
                ghiLog("[Giao diện] Server đã được tắt.");
                JOptionPane.showMessageDialog(this, 
                    "Server đã được tắt",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                ghiLog("[Giao diện] Hủy thao tác tắt server.");
            }
        } else {
            ghiLog("[Giao diện] Server không đang chạy, không thể tắt.");
        }
        capNhatTrangThai();
        capNhatLog();
        dungTimer();
    }
    
    private void xoaLog() {
        ghiLog("[Giao diện] Người dùng bấm nút Xóa Log.");
        if (txtAreaLog == null) {
            ghiLog("[Giao diện] Lỗi: Không thể truy cập vùng log!");
            JOptionPane.showMessageDialog(this, 
                "Lỗi: Không thể truy cập vùng log!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa tất cả log?",
            "Xác nhận xóa log", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                txtAreaLog.setText("");
                txtAreaLog.setCaretPosition(0);
                txtAreaLog.repaint();
                ghiLog("[Giao diện] Đã xóa log thành công!");
                JOptionPane.showMessageDialog(this, 
                    "Đã xóa log thành công!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                ghiLog("[Giao diện] Lỗi khi xóa log: " + e.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa log: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            ghiLog("[Giao diện] Hủy thao tác xóa log.");
        }
    }
    
    private void capNhatTrangThai() {
        if (server.isDangChay()) {
            lblTrangThai.setText("Đang chạy");
            lblTrangThai.setForeground(Color.GREEN);
        } else {
            lblTrangThai.setText("Đã dừng");
            lblTrangThai.setForeground(Color.RED);
        }
        
    }
    
    
    /**
     * Ghi log vào text area
     */
    public void ghiLog(String message) {
        if (txtAreaLog != null) {
            SwingUtilities.invokeLater(() -> {
                String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
                String logMessage = "[" + timestamp + "] " + message;
                
                txtAreaLog.append(logMessage + "\n");
                txtAreaLog.setCaretPosition(txtAreaLog.getDocument().getLength());
            });
        }
    }
    
    /**
     * Cập nhật log từ server
     */
    private void capNhatLog() {
        if (server != null && server.isDangChay()) {
            List<String> danhSachLog = server.layDanhSachLog();
            if (danhSachLog != null && !danhSachLog.isEmpty()) {
                StringBuilder logText = new StringBuilder();
                // Chỉ hiển thị 100 dòng log gần nhất
                int start = Math.max(0, danhSachLog.size() - 100);
                for (int i = start; i < danhSachLog.size(); i++) {
                    logText.append(danhSachLog.get(i)).append("\n");
                }
                
                String currentText = txtAreaLog.getText();
                String newText = logText.toString();
                
                if (!currentText.equals(newText)) {
                    txtAreaLog.setText(newText);
                    // Auto scroll to bottom
                    txtAreaLog.setCaretPosition(txtAreaLog.getDocument().getLength());
                }
            }
        }
    }
    
    /**
     * Khởi tạo timer để cập nhật log
     */
    private void khoiTaoTimer() {
        if (timerCapNhatLog != null) {
            timerCapNhatLog.cancel();
        }
        
        timerCapNhatLog = new Timer("LogUpdater", true);
        timerCapNhatLog.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    capNhatLog();
                });
            }
        }, 0, 1000); // Cập nhật mỗi 1 giây
    }
    
    /**
     * Dừng timer cập nhật log
     */
    private void dungTimer() {
        if (timerCapNhatLog != null) {
            timerCapNhatLog.cancel();
            timerCapNhatLog = null;
        }
    }
    
    public static void main(String[] args) {
        // Thiết lập Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new GiaoDienServer().setVisible(true);
        });
    }
}
