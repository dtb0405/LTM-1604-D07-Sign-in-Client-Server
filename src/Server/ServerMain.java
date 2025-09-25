package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

@SuppressWarnings("serial")
public class ServerMain extends JFrame {
    private JTextArea logArea;
    private JButton btnStart, btnStop;
    private JLabel lblStatus;
    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private List<XuLyClient> clients = new ArrayList<>();
    
    // Quản lý user online
    private DefaultListModel<String> onlineUsersModel;
    private DefaultListModel<String> offlineUsersModel;
    private JList<String> onlineUsersList;
    private JList<String> offlineUsersList;
    private JButton btnLogout, btnAdd, btnEdit, btnDelete;
    
    // Dashboard components
    private JLabel lblTotalUsers, lblOnlineUsers, lblOfflineUsers, lblTodayLogins;
    // Bỏ progressBar theo yêu cầu
    private boolean isDarkMode = false;
    private String currentLanguage = "VI";
    
    // Login history table
    private JTable loginHistoryTable;
    
    // UI components for language switching
    private JTabbedPane tabbedPane;

    public ServerMain() {
        setTitle("Server Dang Nhap - Quan Ly Ket Noi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setResizable(true);

        initComponents();
        setupEventHandlers();
    }

    private void initComponents() {
        // Panel chính với tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Tab 1: Dashboard
        JPanel dashboardPanel = createDashboardPanel();
        tabbedPane.addTab("Dashboard", dashboardPanel);
        
        // Tab 2: Trang chủ Server
        JPanel homePanel = createHomePanel();
        tabbedPane.addTab("Trang Chủ", homePanel);
        
        // Tab 3: Quản lý tài khoản
        JPanel userPanel = createUserManagementPanel();
        tabbedPane.addTab("Quản Lý Tài Khoản", userPanel);
        
        // Tab 4: Lịch sử đăng nhập
        JPanel historyPanel = createLoginHistoryPanel();
        tabbedPane.addTab("Lịch Sử Đăng Nhập", historyPanel);
        
        add(tabbedPane);
    }
    
    private JPanel createDashboardPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(248, 249, 250));

        // Panel header với controls
        JPanel headerPanel = createDashboardHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel thống kê chính
        JPanel statsPanel = createStatsPanel();
        mainPanel.add(statsPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createDashboardHeader() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(248, 249, 250));
        
        // Tiêu đề
        JLabel lblTitle = new JLabel("Dashboard - Tổng quan hệ thống", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 150, 243));
        panel.add(lblTitle, BorderLayout.CENTER);
        
        // Panel controls - chỉ giữ Dark Mode và Refresh
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        controlsPanel.setBackground(new Color(248, 249, 250));
        
        // Nút Dark Mode
        JButton btnDarkMode = createButton("Dark Mode", new Color(80, 80, 80));
        btnDarkMode.addActionListener(e -> toggleDarkMode());
        
        // Nút Refresh
        JButton btnRefresh = createButton("Refresh", new Color(50, 170, 250));
        btnRefresh.addActionListener(e -> refreshDashboard());
        
        controlsPanel.add(btnDarkMode);
        controlsPanel.add(btnRefresh);
        
        panel.add(controlsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBackground(new Color(248, 249, 250));
        
        // Card 1: Tổng số user
        JPanel card1 = createStatCard("Tổng số tài khoản", "0", new Color(33, 150, 243), "total");
        panel.add(card1);
        
        // Card 2: User online
        JPanel card2 = createStatCard("Đang online", "0", new Color(76, 175, 80), "online");
        panel.add(card2);
        
        // Card 3: User offline
        JPanel card3 = createStatCard("Đã offline", "0", new Color(244, 67, 54), "offline");
        panel.add(card3);
        
        // Card 4: Đăng nhập hôm nay
        JPanel card4 = createStatCard("Đăng nhập hôm nay", "0", new Color(255, 152, 0), "today");
        panel.add(card4);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color, String type) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Tiêu đề
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(100, 100, 100));
        card.add(lblTitle, BorderLayout.NORTH);
        
        // Giá trị
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(color);
        card.add(lblValue, BorderLayout.CENTER);
        
        // Bỏ progress bar theo yêu cầu
        
        // Lưu reference cho việc cập nhật
        switch (type) {
            case "total":
                lblTotalUsers = lblValue;
                break;
            case "online":
                lblOnlineUsers = lblValue;
                break;
            case "offline":
                lblOfflineUsers = lblValue;
                break;
            case "today":
                lblTodayLogins = lblValue;
                break;
        }
        
        return card;
    }
    
    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(248, 249, 250));
        
        // Tiêu đề
        JLabel lblTitle = new JLabel("Biểu đồ hoạt động", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(50, 50, 50));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Panel biểu đồ (simplified)
        JPanel chartArea = new JPanel(new GridLayout(1, 2, 10, 10));
        chartArea.setBackground(new Color(248, 249, 250));
        
        // Biểu đồ đăng nhập theo giờ
        JPanel loginChart = createSimpleChart("Đăng nhập theo giờ", new String[]{"00", "06", "12", "18"}, new int[]{5, 15, 25, 10});
        chartArea.add(loginChart);
        
        // Biểu đồ user theo vai trò
        JPanel roleChart = createSimpleChart("User theo vai trò", new String[]{"Admin", "User"}, new int[]{2, 18});
        chartArea.add(roleChart);
        
        panel.add(chartArea, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSimpleChart(String title, String[] labels, int[] values) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(50, 50, 50));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Simple bar chart representation
        JPanel chartArea = new JPanel(new GridLayout(labels.length, 1, 2, 2));
        chartArea.setBackground(Color.WHITE);
        
        int maxValue = 0;
        for (int value : values) {
            if (value > maxValue) maxValue = value;
        }
        
        for (int i = 0; i < labels.length; i++) {
            JPanel barPanel = new JPanel(new BorderLayout(5, 0));
            barPanel.setBackground(Color.WHITE);
            
            JLabel lblLabel = new JLabel(labels[i] + ": " + values[i]);
            lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            barPanel.add(lblLabel, BorderLayout.WEST);
            
            JProgressBar bar = new JProgressBar(0, maxValue);
            bar.setValue(values[i]);
            bar.setStringPainted(false);
            bar.setBackground(new Color(240, 240, 240));
            bar.setForeground(new Color(33, 150, 243));
            barPanel.add(bar, BorderLayout.CENTER);
            
            chartArea.add(barPanel);
        }
        
        panel.add(chartArea, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        
        // Áp dụng dark mode cho toàn bộ hệ thống
        if (isDarkMode) {
            // Dark theme colors - màu nền hợp lý hơn
            Color darkBg = new Color(25, 25, 25);           // Nền đen sâu
            Color darkCard = new Color(40, 40, 40);         // Card xám đậm
            Color darkText = new Color(230, 230, 230);     // Text sáng rõ ràng
            
            // Cập nhật background chính
            getContentPane().setBackground(darkBg);
            
            // Cập nhật tất cả panels trong hệ thống
            updateSystemTheme(getContentPane(), darkBg, darkCard, darkText);
            
            // Cập nhật tabbedPane
            if (tabbedPane != null) {
                tabbedPane.setBackground(darkBg);
                tabbedPane.setForeground(darkText);
            }
            
        } else {
            // Light theme colors - màu nền hợp lý hơn
            Color lightBg = new Color(240, 240, 240);      // Nền xám nhạt
            Color lightCard = new Color(255, 255, 255);    // Card trắng tinh
            Color lightText = new Color(30, 30, 30);       // Text đen sâu
            
            // Cập nhật background chính
            getContentPane().setBackground(lightBg);
            
            // Cập nhật tất cả panels trong hệ thống
            updateSystemTheme(getContentPane(), lightBg, lightCard, lightText);
            
            // Cập nhật tabbedPane
            if (tabbedPane != null) {
                tabbedPane.setBackground(lightBg);
                tabbedPane.setForeground(lightText);
            }
        }
    }
    
    private void updateSystemTheme(Container container, Color bgColor, Color cardColor, Color textColor) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(bgColor);
                updateSystemTheme(panel, bgColor, cardColor, textColor);
            } else if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                label.setForeground(textColor);
            } else if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                updateButtonTheme(button, textColor);
            } else if (comp instanceof JTabbedPane) {
                JTabbedPane tabbedPane = (JTabbedPane) comp;
                tabbedPane.setBackground(bgColor);
                tabbedPane.setForeground(textColor);
                updateSystemTheme(tabbedPane, bgColor, cardColor, textColor);
            } else if (comp instanceof JList) {
                JList<?> list = (JList<?>) comp;
                list.setBackground(cardColor);
                list.setForeground(textColor);
            } else if (comp instanceof JTable) {
                JTable table = (JTable) comp;
                table.setBackground(cardColor);
                table.setForeground(textColor);
                table.getTableHeader().setBackground(bgColor);
                table.getTableHeader().setForeground(textColor);
            } else if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                scrollPane.setBackground(bgColor);
                scrollPane.getViewport().setBackground(cardColor);
                updateSystemTheme(scrollPane.getViewport(), bgColor, cardColor, textColor);
            } else if (comp instanceof JProgressBar) {
                JProgressBar progressBar = (JProgressBar) comp;
                progressBar.setBackground(cardColor);
                progressBar.setForeground(textColor);
            }
        }
    }
    
    private void updateButtonTheme(JButton button, Color textColor) {
        String buttonText = button.getText();
        
        // Cải thiện màu sắc và bo góc cho từng loại button
        if (buttonText != null) {
            if (buttonText.contains("Dark Mode")) {
                // Dark Mode button - màu xám đậm với bo góc
                button.setBackground(new Color(70, 70, 70));
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
                ));
            } else if (buttonText.contains("VI/EN")) {
                // Language button - màu tím đậm với bo góc
                button.setBackground(new Color(103, 58, 183));
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(123, 78, 203), 1),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
                ));
            } else if (buttonText.contains("Refresh")) {
                // Refresh button - màu xanh đậm với bo góc
                button.setBackground(new Color(33, 150, 243));
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(53, 170, 255), 1),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
                ));
            } else {
                // Default button styling
                button.setBackground(new Color(100, 100, 100));
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(130, 130, 130), 1),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
                ));
            }
        }
        
        // Bo góc cho tất cả buttons
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color currentBg = button.getBackground();
                button.setBackground(new Color(
                    Math.min(255, currentBg.getRed() + 20),
                    Math.min(255, currentBg.getGreen() + 20),
                    Math.min(255, currentBg.getBlue() + 20)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Restore original color based on button type
                String text = button.getText();
                if (text != null) {
                    if (text.contains("Dark Mode")) {
                        button.setBackground(new Color(70, 70, 70));
                    } else if (text.contains("VI/EN")) {
                        button.setBackground(new Color(103, 58, 183));
                    } else if (text.contains("Refresh")) {
                        button.setBackground(new Color(33, 150, 243));
                    } else {
                        button.setBackground(new Color(100, 100, 100));
                    }
                }
            }
        });
    }
    
    private void toggleLanguage() {
        currentLanguage = currentLanguage.equals("VI") ? "EN" : "VI";
        
        // Cập nhật text theo ngôn ngữ cho toàn bộ hệ thống
        if (currentLanguage.equals("EN")) {
            updateSystemLanguageToEnglish();
        } else {
            updateSystemLanguageToVietnamese();
        }
    }
    
    private void updateSystemLanguageToEnglish() {
        // Cập nhật tab titles
        if (tabbedPane != null) {
            tabbedPane.setTitleAt(0, "Dashboard");
            tabbedPane.setTitleAt(1, "Home");
            tabbedPane.setTitleAt(2, "User Management");
            tabbedPane.setTitleAt(3, "Login History");
        }
        
        // Cập nhật các text khác trong hệ thống
        updateSystemTexts(getContentPane(), "EN");
    }
    
    private void updateSystemLanguageToVietnamese() {
        // Cập nhật tab titles
        if (tabbedPane != null) {
            tabbedPane.setTitleAt(0, "Dashboard");
            tabbedPane.setTitleAt(1, "Trang Chủ");
            tabbedPane.setTitleAt(2, "Quản Lý Tài Khoản");
            tabbedPane.setTitleAt(3, "Lịch Sử Đăng Nhập");
        }
        
        // Cập nhật các text khác trong hệ thống
        updateSystemTexts(getContentPane(), "VI");
    }
    
    private void updateSystemTexts(Container container, String language) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                String text = label.getText();
                if (text != null) {
                    // Cập nhật các text phổ biến
                    if (language.equals("EN")) {
                        text = text.replace("Dashboard - Tổng quan hệ thống", "Dashboard - System Overview")
                                 .replace("Tổng số tài khoản", "Total Accounts")
                                 .replace("Đang online", "Online")
                                 .replace("Đã offline", "Offline")
                                 .replace("Đăng nhập hôm nay", "Today's Logins")
                                 .replace("Quản Lý Tài Khoản", "User Management")
                                 .replace("Lịch Sử Đăng Nhập", "Login History")
                                 .replace("Tìm", "Search")
                                 .replace("Xóa", "Clear")
                                 .replace("Lọc", "Filter")
                                 .replace("Làm mới", "Refresh")
                                 .replace("Xuất file", "Export")
                                 .replace("Khởi Động Server", "Start Server")
                                 .replace("Dừng Server", "Stop Server")
                                 .replace("Xóa Log", "Clear Log")
                                 .replace("Quản Lý User", "Manage Users");
                    } else {
                        text = text.replace("Dashboard - System Overview", "Dashboard - Tổng quan hệ thống")
                                 .replace("Total Accounts", "Tổng số tài khoản")
                                 .replace("Online", "Đang online")
                                 .replace("Offline", "Đã offline")
                                 .replace("Today's Logins", "Đăng nhập hôm nay")
                                 .replace("User Management", "Quản Lý Tài Khoản")
                                 .replace("Login History", "Lịch Sử Đăng Nhập")
                                 .replace("Search", "Tìm")
                                 .replace("Clear", "Xóa")
                                 .replace("Filter", "Lọc")
                                 .replace("Refresh", "Làm mới")
                                 .replace("Export", "Xuất file")
                                 .replace("Start Server", "Khởi Động Server")
                                 .replace("Stop Server", "Dừng Server")
                                 .replace("Clear Log", "Xóa Log")
                                 .replace("Manage Users", "Quản Lý User");
                    }
                    label.setText(text);
                }
            } else if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String text = button.getText();
                if (text != null) {
                    if (language.equals("EN")) {
                        text = text.replace("Tìm", "Search")
                                 .replace("Xóa", "Clear")
                                 .replace("Lọc", "Filter")
                                 .replace("Làm mới", "Refresh")
                                 .replace("Xuất file", "Export")
                                 .replace("Khởi Động Server", "Start Server")
                                 .replace("Dừng Server", "Stop Server")
                                 .replace("Xóa Log", "Clear Log")
                                 .replace("Quản Lý User", "Manage Users");
                    } else {
                        text = text.replace("Search", "Tìm")
                                 .replace("Clear", "Xóa")
                                 .replace("Filter", "Lọc")
                                 .replace("Refresh", "Làm mới")
                                 .replace("Export", "Xuất file")
                                 .replace("Start Server", "Khởi Động Server")
                                 .replace("Stop Server", "Dừng Server")
                                 .replace("Clear Log", "Xóa Log")
                                 .replace("Manage Users", "Quản Lý User");
                    }
                    button.setText(text);
                }
            } else if (comp instanceof Container) {
                updateSystemTexts((Container) comp, language);
            }
        }
    }
    
    private void refreshDashboard() {
        // Refresh dashboard data
        updateDashboardStats();
        
        // Refresh user lists
        updateClientCount();
        
        // Refresh login history
        refreshLoginHistory();
        
        // Cập nhật giao diện
        revalidate();
        repaint();
    }
    
    private void updateDashboardStats() {
        try {
            QuanLyNguoiDung qlNguoiDung = new QuanLyNguoiDung();
            int[] stats = qlNguoiDung.layThongKeNguoiDung();
            int todayLogins = qlNguoiDung.laySoLanDangNhapHomNay();
            
            // Cập nhật labels với dữ liệu thực
            if (lblTotalUsers != null) lblTotalUsers.setText(String.valueOf(stats[0]));
            if (lblOnlineUsers != null) lblOnlineUsers.setText(String.valueOf(stats[1]));
            if (lblOfflineUsers != null) lblOfflineUsers.setText(String.valueOf(stats[0] - stats[1]));
            if (lblTodayLogins != null) lblTodayLogins.setText(String.valueOf(todayLogins));
            
            // Bỏ progress bar theo yêu cầu
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to default values
            if (lblTotalUsers != null) lblTotalUsers.setText("0");
            if (lblOnlineUsers != null) lblOnlineUsers.setText("0");
            if (lblOfflineUsers != null) lblOfflineUsers.setText("0");
            if (lblTodayLogins != null) lblTodayLogins.setText("0");
            // Bỏ progress bar theo yêu cầu
        }
    }
    
    private JPanel createHomePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(248, 249, 250));

        // Panel điều khiển server
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Panel log hoạt động
        JPanel logPanel = createLogPanel();
        mainPanel.add(logPanel, BorderLayout.CENTER);

        // Panel trạng thái và thống kê
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        return mainPanel;
    }
    
    private JPanel createUserManagementPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(248, 249, 250));

        // Panel trên: Thống kê và tìm kiếm
        JPanel topPanel = createUserTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Panel giữa: Danh sách user với tab
        JPanel centerPanel = createUserListPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Panel dưới: Các nút chức năng
        JPanel buttonPanel = createUserActionPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createUserTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(248, 249, 250));
        
        // Tiêu đề
        JLabel lblTitle = new JLabel("Quản Lý Tài Khoản", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 150, 243));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Bỏ panel tìm kiếm và filter theo yêu cầu
        
        return panel;
    }
    
    private JPanel createSearchFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(248, 249, 250));
        
        // Panel tìm kiếm
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchRow.setBackground(new Color(248, 249, 250));
        
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        JTextField txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JButton btnSearch = createButton("Tìm", new Color(33, 150, 243));
        JButton btnClear = createButton("Xóa", new Color(156, 39, 176));
        
        btnSearch.addActionListener(e -> searchUsers(txtSearch.getText()));
        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            searchUsers("");
        });
        
        searchRow.add(lblSearch);
        searchRow.add(txtSearch);
        searchRow.add(btnSearch);
        searchRow.add(btnClear);
        
        // Panel filter
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterRow.setBackground(new Color(248, 249, 250));
        
        JLabel lblFilter = new JLabel("Lọc theo:");
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"Tất cả", "Admin", "User", "Moderator"});
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Tất cả", "Online", "Offline", "Khóa"});
        JButton btnFilter = createButton("Lọc", new Color(76, 175, 80));
        
        btnFilter.addActionListener(e -> filterUsers((String)cmbRole.getSelectedItem(), (String)cmbStatus.getSelectedItem()));
        
        filterRow.add(lblFilter);
        filterRow.add(cmbRole);
        filterRow.add(cmbStatus);
        filterRow.add(btnFilter);
        
        // Panel thống kê
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        statsRow.setBackground(new Color(248, 249, 250));
        
        JLabel lblOnline = new JLabel("Online: 0");
        JLabel lblOffline = new JLabel("Offline: 0");
        JLabel lblTotal = new JLabel("Tổng: 0");
        
        lblOnline.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblOffline.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        lblOnline.setForeground(new Color(76, 175, 80));
        lblOffline.setForeground(new Color(244, 67, 54));
        lblTotal.setForeground(new Color(33, 150, 243));
        
        statsRow.add(lblOnline);
        statsRow.add(lblOffline);
        statsRow.add(lblTotal);
        
        panel.add(searchRow, BorderLayout.NORTH);
        panel.add(filterRow, BorderLayout.CENTER);
        panel.add(statsRow, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void searchUsers(String keyword) {
        try {
            QuanLyNguoiDung qlNguoiDung = new QuanLyNguoiDung();
            java.util.List<String> results = qlNguoiDung.timKiemNguoiDung(keyword);
            
            StringBuilder message = new StringBuilder("Kết quả tìm kiếm cho: " + keyword + "\n\n");
            if (results.isEmpty()) {
                message.append("Không tìm thấy kết quả nào.");
            } else {
                for (String result : results) {
                    message.append("• ").append(result).append("\n");
                }
            }
            
            JOptionPane.showMessageDialog(this, message.toString(), 
                "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterUsers(String role, String status) {
        try {
            QuanLyNguoiDung qlNguoiDung = new QuanLyNguoiDung();
            java.util.List<String> results = qlNguoiDung.locNguoiDung(role, status);
            
            StringBuilder message = new StringBuilder("Kết quả lọc theo: " + role + " - " + status + "\n\n");
            if (results.isEmpty()) {
                message.append("Không có kết quả nào phù hợp.");
            } else {
                for (String result : results) {
                    message.append("• ").append(result).append("\n");
                }
            }
            
            JOptionPane.showMessageDialog(this, message.toString(), 
                "Kết quả lọc", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lọc: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createUserListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tabbed pane cho Online/Offline
        JTabbedPane userTabs = new JTabbedPane();
        userTabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Tab Online
        JPanel onlinePanel = new JPanel(new BorderLayout());
        onlineUsersModel = new DefaultListModel<>();
        onlineUsersList = new JList<>(onlineUsersModel);
        onlineUsersList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane onlineScroll = new JScrollPane(onlineUsersList);
        onlineScroll.setBorder(BorderFactory.createTitledBorder("Tài khoản đang online"));
        onlinePanel.add(onlineScroll, BorderLayout.CENTER);
        userTabs.addTab("Online", onlinePanel);
        
        // Tab Offline
        JPanel offlinePanel = new JPanel(new BorderLayout());
        offlineUsersModel = new DefaultListModel<>();
        offlineUsersList = new JList<>(offlineUsersModel);
        offlineUsersList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane offlineScroll = new JScrollPane(offlineUsersList);
        offlineScroll.setBorder(BorderFactory.createTitledBorder("Tài khoản đã offline"));
        offlinePanel.add(offlineScroll, BorderLayout.CENTER);
        userTabs.addTab("Offline", offlinePanel);
        
        panel.add(userTabs, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createUserActionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(BorderFactory.createTitledBorder("Chức năng quản lý"));
        
        // Hàng 1
        btnLogout = createUserButton("Đăng xuất");
        btnAdd = createUserButton("Thêm tài khoản");
        btnEdit = createUserButton("Sửa thông tin");
        btnDelete = createUserButton("Xóa tài khoản");
        
        // Hàng 2
        JButton btnLock = createUserButton("Khóa tài khoản");
        JButton btnUnlock = createUserButton("Mở khóa");
        JButton btnRole = createUserButton("Cập nhật vai trò");
        // Bỏ nút xem lịch sử theo yêu cầu
        
        panel.add(btnLogout);
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnLock);
        panel.add(btnUnlock);
        panel.add(btnRole);
        
        // Gắn sự kiện
        btnLogout.addActionListener(e -> logoutUser());
        btnAdd.addActionListener(e -> addUser());
        btnEdit.addActionListener(e -> editUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnLock.addActionListener(e -> lockOrUnlockSelectedUser(true));
        btnUnlock.addActionListener(e -> lockOrUnlockSelectedUser(false));
        btnRole.addActionListener(e -> updateRoleSelectedUser());

        return panel;
    }
    
    private JPanel createLoginHistoryPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(248, 249, 250));
        
        // Tiêu đề
        JLabel lblTitle = new JLabel("Lịch Sử Đăng Nhập", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 150, 243));
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        
        // Panel chỉ có nút làm mới
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filterPanel.setBackground(new Color(248, 249, 250));
        
        JButton btnRefresh = createButton("Làm mới", new Color(76, 175, 80));
        btnRefresh.addActionListener(e -> refreshLoginHistory());
        
        filterPanel.add(btnRefresh);
        
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Panel bảng lịch sử
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columns = {"Thông tin"};
        Object[][] data = loadLoginHistoryData(); // Load data từ database
        
        loginHistoryTable = new JTable(data, columns);
        loginHistoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginHistoryTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(loginHistoryTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Chi tiết lịch sử đăng nhập"));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private Object[][] loadLoginHistoryData() {
        try {
            QuanLyNguoiDung qlNguoiDung = new QuanLyNguoiDung();
            java.util.List<String> history = qlNguoiDung.layLichSuDangNhap(""); // Lấy tất cả
            
            Object[][] data = new Object[history.size()][5];
            for (int i = 0; i < history.size(); i++) {
                String[] parts = history.get(i).split(" - ");
                if (parts.length >= 3) {
                    data[i][0] = parts[0]; // Thời gian
                    data[i][1] = parts[1]; // Tên đăng nhập
                    data[i][2] = parts[2]; // IP
                    data[i][3] = parts.length > 3 ? parts[3] : "Success"; // Trạng thái
                    data[i][4] = "User"; // Vai trò (có thể lấy từ database)
                }
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[0][5];
        }
    }
    
    private void searchLoginHistory(String keyword) {
        // TODO: Implement search functionality
        JOptionPane.showMessageDialog(this, "Tìm kiếm lịch sử: " + keyword, 
            "Tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshLoginHistory() {
        // Cập nhật bảng lịch sử đăng nhập nếu đang hiển thị
        if (loginHistoryTable != null) {
            try {
                QuanLyNguoiDung qlNguoiDung = new QuanLyNguoiDung();
                java.util.List<String> historyData = qlNguoiDung.layLichSuDangNhap("");
                
                String[] columns = {"Thông tin"};
                Object[][] data = new Object[historyData.size()][1];
                
                for (int i = 0; i < historyData.size(); i++) {
                    data[i][0] = historyData.get(i);
                }
                
                // Cập nhật model của bảng
                javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columns);
                loginHistoryTable.setModel(model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder("Điều Khiển Server"));

        btnStart = createButton("Khởi Động Server", new Color(76, 175, 80));
        btnStop = createButton("Dừng Server", new Color(244, 67, 54));
        btnStop.setEnabled(false);

        JButton btnClearLog = createButton("Xóa Log", new Color(33, 150, 243));
        // Bỏ nút Quản Lý User theo yêu cầu

        panel.add(btnStart);
        panel.add(btnStop);
        panel.add(btnClearLog);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Log Hoạt Động"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(250, 250, 250));
        logArea.setForeground(new Color(50, 50, 50));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(245, 245, 245));

        JLabel lblStatusText = new JLabel("Trạng thái:");
        lblStatusText.setFont(new Font("Segoe UI", Font.BOLD, 12));

        lblStatus = new JLabel("Chưa khởi động");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(244, 67, 54));

        // Bỏ phần clients theo yêu cầu

        panel.add(lblStatusText);
        panel.add(lblStatus);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 35));
        
        // Bo góc và border đẹp hơn
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Hover effect với màu sáng hơn
                Color hoverColor = new Color(
                    Math.min(255, color.getRed() + 30),
                    Math.min(255, color.getGreen() + 30),
                    Math.min(255, color.getBlue() + 30)
                );
                btn.setBackground(hoverColor);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(hoverColor.darker(), 1),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color.darker(), 1),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
                ));
            }
        });

        return btn;
    }
    
    private JButton createUserButton(String text) {
        JButton btn = new JButton(text) {
            private Color hoverBackground = new Color(100, 149, 237);
            private Color normalBackground = new Color(70, 130, 180);

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getModel().isRollover() ? hoverBackground : normalBackground);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                super.paintComponent(g);
                g2.dispose();
            }
        };

        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setupEventHandlers() {
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });

        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });

        // Xóa log
        for (Component comp : ((JPanel) btnStart.getParent()).getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getText().equals("Xóa Log")) {
                    btn.addActionListener(e -> logArea.setText(""));
                }
            }
        }
        
        // Các sự kiện của panel quản lý tài khoản đã gắn trực tiếp khi tạo panel
    }

    private void startServer() {
        try {
            // Kiểm tra kết nối database trước và log lỗi chi tiết nếu có
            try {
            if (!KetNoiDB.testKetNoi()) {
                    logMessage("❌ Không thể kết nối DB: vui lòng kiểm tra URL/user/pass và MySQL Service");
                    JOptionPane.showMessageDialog(this,
                        "Không thể kết nối đến database!\nVui lòng kiểm tra cấu hình MySQL.",
                        "Lỗi Database",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                logMessage("❌ Lỗi DB: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Không thể kết nối đến database!\n" + ex.getMessage(),
                    "Lỗi Database",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            serverSocket = new ServerSocket(12345);
            isRunning = true;
            
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            lblStatus.setText("Đang chạy");
            lblStatus.setForeground(new Color(76, 175, 80));
            
            logMessage("Server đã khởi động thành công trên port 12345");
            logMessage("Kết nối database thành công");
            logMessage("Đang chờ kết nối từ client...");

            // Thread xử lý kết nối client
            Thread serverThread = new Thread(() -> {
                while (isRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        XuLyClient clientHandler = new XuLyClient(clientSocket, this);
                        clients.add(clientHandler);
                        clientHandler.start();
                        
                        logMessage("📱 Client mới kết nối: " + clientSocket.getInetAddress());
                        updateClientCount();
                    } catch (IOException e) {
                        if (isRunning) {
                            logMessage("❌ Lỗi chấp nhận kết nối: " + e.getMessage());
                        }
                    }
                }
            });
            serverThread.start();

        } catch (IOException e) {
            logMessage("❌ Lỗi khởi động server: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Không thể khởi động server!\n" + e.getMessage(), 
                "Lỗi Server", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopServer() {
        isRunning = false;
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logMessage("❌ Lỗi đóng server: " + e.getMessage());
        }

        // Đóng tất cả client
        for (XuLyClient client : clients) {
            try {
                client.interrupt();
            } catch (Exception e) {
                logMessage("❌ Lỗi đóng client: " + e.getMessage());
            }
        }
        clients.clear();

        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        lblStatus.setText("Đã dừng");
        lblStatus.setForeground(new Color(244, 67, 54));
        
        logMessage("🛑 Server đã dừng");
        updateClientCount();
    }

    // Phương thức quản lý user
    private void logoutUser() {
        String selectedUser = getSelectedUsername();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn user!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Loại khỏi danh sách online (server sẽ ngắt khi client thực sự rời)
        try {
            QuanLyNguoiDung.xoaUserOnline(selectedUser);
            updateClientCount();
            logMessage("User " + selectedUser + " đã bị đăng xuất (server-side)");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đăng xuất: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addUser() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField hoTen = new JTextField();
        JTextField tenDangNhap = new JTextField();
        JPasswordField matKhau = new JPasswordField();
        JTextField email = new JTextField();
        JTextField soDienThoai = new JTextField();
        panel.add(new JLabel("Họ và tên:")); panel.add(hoTen);
        panel.add(new JLabel("Tên đăng nhập:")); panel.add(tenDangNhap);
        panel.add(new JLabel("Mật khẩu:")); panel.add(matKhau);
        panel.add(new JLabel("Email:")); panel.add(email);
        panel.add(new JLabel("Số điện thoại:")); panel.add(soDienThoai);
        int res = JOptionPane.showConfirmDialog(this, panel, "Thêm tài khoản", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                QuanLyNguoiDung ql = new QuanLyNguoiDung();
                boolean ok = ql.dangKy(hoTen.getText().trim(), tenDangNhap.getText().trim(), new String(matKhau.getPassword()), email.getText().trim(), soDienThoai.getText().trim());
                if (ok) {
                    logMessage("Thêm user thành công: " + tenDangNhap.getText().trim());
                    updateClientCount();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể thêm user (trùng tên hoặc lỗi DB)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi DB: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editUser() {
        String selectedUser = getSelectedUsername();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn user!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            QuanLyNguoiDung ql = new QuanLyNguoiDung();
            String info = ql.layThongTinNguoiDung(selectedUser);
            String hoTen = extract(info, "Họ tên: ");
            String email = extract(info, "Email: ");
            String sdt = extract(info, "SĐT: ");

            JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
            JTextField hoTenField = new JTextField(hoTen);
            JTextField emailField = new JTextField("Chưa cập nhật".equals(email) ? "" : email);
            JTextField sdtField = new JTextField("Chưa cập nhật".equals(sdt) ? "" : sdt);
            panel.add(new JLabel("Họ và tên:")); panel.add(hoTenField);
            panel.add(new JLabel("Email:")); panel.add(emailField);
            panel.add(new JLabel("Số điện thoại:")); panel.add(sdtField);
            int res = JOptionPane.showConfirmDialog(this, panel, "Sửa thông tin", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                boolean ok = ql.capNhatThongTinNguoiDung(selectedUser, hoTenField.getText().trim(), emailField.getText().trim(), sdtField.getText().trim());
                if (ok) {
                    logMessage("Cập nhật thông tin thành công cho: " + selectedUser);
                    updateClientCount();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi DB: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteUser() {
        String selectedUser = getSelectedUsername();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn user!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa tài khoản '" + selectedUser + "'?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                QuanLyNguoiDung ql = new QuanLyNguoiDung();
                boolean ok = ql.xoaTaiKhoan(selectedUser);
                if (ok) {
                    logMessage("Đã xóa user: " + selectedUser);
                    QuanLyNguoiDung.xoaUserOnline(selectedUser);
                    updateClientCount();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa (có thể user không tồn tại)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi DB: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void lockOrUnlockSelectedUser(boolean lock) {
        String selectedUser = getSelectedUsername();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn user!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            QuanLyNguoiDung ql = new QuanLyNguoiDung();
            boolean ok = ql.khoaTaiKhoan(selectedUser, lock);
            if (ok) {
                if (lock) QuanLyNguoiDung.xoaUserOnline(selectedUser);
                updateClientCount();
                logMessage((lock ? "Đã khóa: " : "Đã mở khóa: ") + selectedUser);
            } else {
                JOptionPane.showMessageDialog(this, "Thao tác thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi DB: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRoleSelectedUser() {
        String selectedUser = getSelectedUsername();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn user!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String[] roles = new String[]{"Admin", "User"};
        String role = (String) JOptionPane.showInputDialog(this, "Chọn vai trò", "Cập nhật vai trò", JOptionPane.PLAIN_MESSAGE, null, roles, roles[1]);
        if (role != null) {
            try {
                QuanLyNguoiDung ql = new QuanLyNguoiDung();
                boolean ok = ql.capNhatVaiTro(selectedUser, role);
                if (ok) {
                    updateClientCount();
                    logMessage("Cập nhật vai trò cho " + selectedUser + ": " + role);
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật vai trò", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi DB: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getSelectedUsername() {
        String selected = onlineUsersList.getSelectedValue();
        if (selected == null) selected = offlineUsersList.getSelectedValue();
        if (selected == null) return null;
        // Chuỗi có thể ở dạng "username (Họ Tên) - Vai trò" hoặc chỉ username
        int spaceIdx = selected.indexOf(' ');
        return spaceIdx > 0 ? selected.substring(0, spaceIdx) : selected;
    }

    private String extract(String source, String key) {
        for (String part : source.split(";")) {
            if (part.startsWith(key)) {
                return part.substring(key.length());
            }
        }
        return "";
    }
    
    public void addUser(String username) {
        if (!onlineUsersModel.contains(username)) {
            onlineUsersModel.addElement(username);
            logMessage("User " + username + " đã online");
        }
    }
    
    public void removeUser(String username) {
        onlineUsersModel.removeElement(username);
        logMessage("User " + username + " đã offline");
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public void updateClientCount() {
        System.out.println("DEBUG: updateClientCount() được gọi");
        SwingUtilities.invokeLater(() -> {
            System.out.println("DEBUG: Bắt đầu cập nhật UI");
            // Cập nhật dashboard stats
            updateDashboardStats();
            
            // Cập nhật danh sách user online
            if (onlineUsersModel != null) {
                // Refresh online users từ danh sách thực sự online
                try {
                    QuanLyNguoiDung qlNguoiDung = new QuanLyNguoiDung();
                    java.util.List<String> onlineUsers = qlNguoiDung.layDanhSachNguoiDungOnlineChiTiet();
                    
                    System.out.println("DEBUG: Online users: " + onlineUsers);
                    onlineUsersModel.clear();
                    for (String user : onlineUsers) {
                        onlineUsersModel.addElement(user);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // Cập nhật danh sách user offline
            if (offlineUsersModel != null) {
                try {
                    QuanLyNguoiDung qlNguoiDung = new QuanLyNguoiDung();
                    java.util.List<String> onlineUsers = qlNguoiDung.layDanhSachNguoiDungOnline();
                    java.util.List<String> offlineUsers = new java.util.ArrayList<>();
                    
                    // Lấy tất cả user từ database và loại bỏ những user đang online
                    String sql = "SELECT tenDangNhap FROM NguoiDung";
                    try (Connection conn = KetNoiDB.ketNoi();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            String username = rs.getString("tenDangNhap");
                            if (!onlineUsers.contains(username)) {
                                offlineUsers.add(username);
                            }
                        }
                    }
                    
                    offlineUsersModel.clear();
                    for (String user : offlineUsers) {
                        offlineUsersModel.addElement(user);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // Cập nhật lịch sử đăng nhập
            refreshLoginHistory();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ServerMain().setVisible(true);
        });
    }
}
