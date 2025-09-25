package Client;

import javax.swing.*;


import javax.swing.border.*;
import java.awt.*;

import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



@SuppressWarnings("serial")
public class ClientMain extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean isConnected = false;

    private String currentUser = "";

    public ClientMain() {

        setTitle("Hệ thống đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(550, 750); // Tăng kích thước để chứa form đăng ký đầy đủ
        setMinimumSize(new Dimension(500, 700));
        setLocationRelativeTo(null);


        setResizable(true);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "Login");
        mainPanel.add(createRegisterPanel(), "Register");
        mainPanel.add(createHomePanel(), "Home");

        add(mainPanel);

        cardLayout.show(mainPanel, "Login");
    }

    // Panel nền gradient xanh tím hiện đại
    private JPanel createGradientBackground() {
        return new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(63, 81, 181), getWidth(), getHeight(),
                        new Color(100, 181, 246));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    // Card trắng bo góc + đổ bóng
    private JPanel createCardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Đổ bóng
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);

                // Card trắng
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        panel.setPreferredSize(new Dimension(450, 600)); // Kích thước vừa phải
        panel.setMaximumSize(new Dimension(500, 700));
        panel.setMinimumSize(new Dimension(450, 650));
        return panel;
    }



    // Ô nhập có label + icon trong ô
    private JPanel createLabeledField(String label, boolean password, Icon icon) {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        lbl.setForeground(new Color(0x444444));

        JTextField field = password ? new JPasswordField() : new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Sửa padding để không bị chèn khuất
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xCCCCCC), 1, true),
                new EmptyBorder(12, 15, 12, 15) // Giảm padding trái từ 45 xuống 15
        ));
        field.setBackground(new Color(0xFAFAFA));
        field.setForeground(new Color(0x333333));

        // Thêm focus effect
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(new CompoundBorder(
                        new LineBorder(new Color(33, 150, 243), 2, true),
                        new EmptyBorder(11, 14, 11, 14) // Giảm padding trái từ 44 xuống 14
                ));
                field.setBackground(Color.WHITE);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(new CompoundBorder(
                        new LineBorder(new Color(0xCCCCCC), 1, true),
                        new EmptyBorder(12, 15, 12, 15) // Giảm padding trái từ 45 xuống 15
                ));
                field.setBackground(new Color(0xFAFAFA));
            }
        });

        // Không sử dụng icon, chỉ sử dụng field
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setOpaque(false);
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Đặt kích thước responsive cho field - nhỏ hơn để không tràn
        field.setMinimumSize(new Dimension(200, 40));
        field.setPreferredSize(new Dimension(280, 40));
        field.setMaximumSize(new Dimension(320, 40));

        fieldPanel.add(field, BorderLayout.CENTER);

        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(fieldPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createLoginField(String label, boolean password) {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        lbl.setForeground(new Color(0x444444));

        JTextField field = password ? new JPasswordField() : new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Sửa padding để không bị chèn khuất
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xCCCCCC), 1, true),
                new EmptyBorder(12, 15, 12, 15) // Giảm padding trái từ 45 xuống 15
        ));
        field.setBackground(new Color(0xFAFAFA));
        field.setForeground(new Color(0x333333));

        // Thêm focus effect
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(new CompoundBorder(
                        new LineBorder(new Color(33, 150, 243), 2, true),
                        new EmptyBorder(11, 14, 11, 14) // Giảm padding trái từ 44 xuống 14
                ));
                field.setBackground(Color.WHITE);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(new CompoundBorder(
                        new LineBorder(new Color(0xCCCCCC), 1, true),
                        new EmptyBorder(12, 15, 12, 15) // Giảm padding trái từ 45 xuống 15
                ));
                field.setBackground(new Color(0xFAFAFA));
            }
        });

        // Không sử dụng icon, chỉ sử dụng field
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setOpaque(false);
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Đặt kích thước lớn hơn cho trang đăng nhập
        field.setMinimumSize(new Dimension(300, 45));
        field.setPreferredSize(new Dimension(400, 45));
        field.setMaximumSize(new Dimension(500, 45));

        fieldPanel.add(field, BorderLayout.CENTER);

        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(fieldPanel, BorderLayout.CENTER);

        return wrapper;
    }

    // Nút gradient bo tròn + hover
    private JButton createButton(String text, Color baseColor) {
        JButton btn = new JButton(text);

        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setPreferredSize(new Dimension(200, 45));

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                ButtonModel model = ((AbstractButton) c).getModel();
                Color hover = baseColor.brighter();
                Color pressed = baseColor.darker();
                
                // Shadow effect
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 2, c.getWidth() - 4, c.getHeight() - 4, 25, 25);
                
                // Button background
                Color bgColor = model.isPressed() ? pressed : 
                               model.isRollover() ? hover : baseColor;
                GradientPaint gp = new GradientPaint(0, 0, bgColor,
                        0, c.getHeight(), bgColor.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, c.getWidth() - 2, c.getHeight() - 2, 25, 25);

                // Text
                FontMetrics fm = g.getFontMetrics();
                int tw = fm.stringWidth(text);
                int th = fm.getAscent();
                g2.setColor(Color.WHITE);
                g2.drawString(text, (c.getWidth() - tw) / 2, (c.getHeight() + th) / 2 - 3);
            }
        });
        return btn;
    }



    private JPanel createLoginPanel() {
        JPanel bg = createGradientBackground();
        JPanel card = createCardPanel();

        // Header với icon và tiêu đề
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        // Bỏ icon theo yêu cầu
        JLabel title = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(33, 150, 243));
        
        JLabel subtitle = new JLabel("Chào mừng bạn quay trở lại!", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));
        
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);

        // Form đăng nhập
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        form.add(createLoginField("Tên đăng nhập", false), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 1.0;
        form.add(createLoginField("Mật khẩu", true), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.NONE;
        JButton loginBtn = createButton("Đăng nhập", new Color(76, 175, 80));
        loginBtn.setPreferredSize(new Dimension(200, 45));
        loginBtn.addActionListener(e -> performLogin(form));
        form.add(loginBtn, gbc);

        // Footer với link đăng ký
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel footerPanel = new JPanel(new FlowLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        JLabel registerLink = new JLabel("Chưa có tài khoản? ");
        registerLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerLink.setForeground(new Color(100, 100, 100));
        
        JLabel linkText = new JLabel("Đăng ký ngay");
        linkText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        linkText.setForeground(new Color(33, 150, 243));
        linkText.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkText.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "Register");
            }
            public void mouseEntered(MouseEvent e) {
                linkText.setForeground(new Color(25, 118, 210));
            }
            public void mouseExited(MouseEvent e) {
                linkText.setForeground(new Color(33, 150, 243));
            }
        });
        
        footerPanel.add(registerLink);
        footerPanel.add(linkText);
        form.add(footerPanel, gbc);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);

        bg.add(card);
        return bg;
    }

    private JPanel createRegisterPanel() {
        JPanel bg = createGradientBackground();
        JPanel card = createCardPanel();

        // Header với icon và tiêu đề
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        // Bỏ icon theo yêu cầu
        JLabel title = new JLabel("ĐĂNG KÝ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(233, 30, 99));
        
        JLabel subtitle = new JLabel("Tạo tài khoản mới để bắt đầu!", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));
        
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);

        // Form đăng ký với layout responsive
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        form.add(createLabeledField("Họ và tên", false, null), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 1.0;
        form.add(createLabeledField("Tên đăng nhập", false, null), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 1.0;
        form.add(createLabeledField("Mật khẩu", true, null), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 1.0;
        form.add(createLabeledField("Nhập lại mật khẩu", true, null), gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 1.0;
        form.add(createLabeledField("Email", false, null), gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 1.0;
        form.add(createLabeledField("Số điện thoại", false, null), gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.NONE;
        JButton registerBtn = createButton("Đăng ký", new Color(244, 81, 30));
        registerBtn.setPreferredSize(new Dimension(180, 40));
        registerBtn.addActionListener(e -> performRegister(form));
        form.add(registerBtn, gbc);
        
        // Footer với link đăng nhập
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel footerPanel = new JPanel(new FlowLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 15, 0));
        
        JLabel loginLink = new JLabel("Đã có tài khoản? ");
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginLink.setForeground(new Color(100, 100, 100));
        
        JLabel linkText = new JLabel("Quay lại đăng nhập");
        linkText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        linkText.setForeground(new Color(33, 150, 243));
        linkText.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkText.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "Login");
            }
            public void mouseEntered(MouseEvent e) {
                linkText.setForeground(new Color(25, 118, 210));
            }
            public void mouseExited(MouseEvent e) {
                linkText.setForeground(new Color(33, 150, 243));
            }
        });
        
        footerPanel.add(loginLink);
        footerPanel.add(linkText);
        form.add(footerPanel, gbc);

        // Wrap form trong JScrollPane để có thể cuộn nhưng ẩn thanh cuộn
        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Ẩn hoàn toàn thanh cuộn nhưng vẫn có thể cuộn
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getVerticalScrollBar().setVisible(false);
        scrollPane.getHorizontalScrollBar().setVisible(false);
        
        // Đặt kích thước cho scroll pane
        scrollPane.setPreferredSize(new Dimension(400, 500));
        scrollPane.setMaximumSize(new Dimension(400, 500));

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        bg.add(card);
        return bg;
    }

    private boolean connectToServer() {
        try {


            // Đóng kết nối cũ nếu có
            if (socket != null && !socket.isClosed()) {
                closeConnection();
            }
            
            // Tạo kết nối mới
                socket = new Socket("localhost", 12345);
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                isConnected = true;
            return true;
        } catch (IOException e) {

            JOptionPane.showMessageDialog(this, 
                "Không thể kết nối đến server!\nVui lòng kiểm tra server có đang chạy không.", 
                "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            closeConnection();
            return false;
        }
    }



    private void performLogin(JPanel form) {
        if (!connectToServer()) return;



        JTextField usernameField = getTextFieldFromPanel(form, 0);
        JPasswordField passwordField = (JPasswordField) getTextFieldFromPanel(form, 1);

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            dos.writeUTF("LOGIN," + username + "," + password);
            dos.flush();

            String response = dis.readUTF();
            if ("SUCCESS".equals(response)) {


                // Lưu thông tin user và chuyển đến trang chủ
                currentUser = username;
                
                // Bỏ hộp thông báo theo yêu cầu
                updateHomePanel();
                cardLayout.show(mainPanel, "Home");
            } else {

                JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {

            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            closeConnection();
        }
    }



    private void performRegister(JPanel form) {
        if (!connectToServer()) return;



        JTextField fullNameField = getTextFieldFromPanel(form, 0);
        JTextField usernameField = getTextFieldFromPanel(form, 1);
        JPasswordField passwordField = (JPasswordField) getTextFieldFromPanel(form, 2);
        JPasswordField confirmPasswordField = (JPasswordField) getTextFieldFromPanel(form, 3);
        JTextField emailField = getTextFieldFromPanel(form, 4);
        JTextField phoneField = getTextFieldFromPanel(form, 5);

        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate email format
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Email không đúng định dạng!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate phone format
        if (!phone.isEmpty() && !phone.matches("^[0-9]{10,11}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10-11 chữ số!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {


            dos.writeUTF("REGISTER," + fullName + "," + username + "," + password + "," + email + "," + phone);
            dos.flush();

            String response = dis.readUTF();
            if ("REGISTER_SUCCESS".equals(response)) {


                JOptionPane.showMessageDialog(this, "Đăng ký thành công!\nBạn có thể đăng nhập ngay bây giờ.", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "Login");
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            closeConnection();
        }
    }

    private JTextField getTextFieldFromPanel(JPanel panel, int index) {
        Component[] components = panel.getComponents();
        int fieldCount = 0;
        
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel subPanel = (JPanel) comp;
                Component[] subComponents = subPanel.getComponents();
                for (Component subComp : subComponents) {
                    if (subComp instanceof JPanel) {
                        JPanel fieldPanel = (JPanel) subComp;
                        Component[] fieldComponents = fieldPanel.getComponents();
                        for (Component fieldComp : fieldComponents) {
                            if (fieldComp instanceof JTextField || fieldComp instanceof JPasswordField) {
                                if (fieldCount == index) {
                                    return (JTextField) fieldComp;
                                }
                                fieldCount++;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private JPanel createHomePanel() {
        JPanel bg = createGradientBackground();
        JPanel card = createCardPanel();

        // Header với icon và chào mừng
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        // Bỏ icon theo yêu cầu
        JLabel welcomeLabel = new JLabel("Chào mừng " + currentUser, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(33, 150, 243));
        
        JLabel subtitle = new JLabel("Chào mừng bạn đến với hệ thống!", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));
        
        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);

        // Panel thông tin tài khoản
        JPanel infoPanel = createAccountInfoPanel();
        
        // Panel nút chức năng
        JPanel buttonPanel = createHomeButtonPanel();

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        bg.add(card);
        return bg;
    }
    
    private JPanel createAccountInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Thông tin tài khoản
        JLabel lblTitle = new JLabel("Thông tin tài khoản", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(lblTitle, gbc);

        // Lấy thông tin user từ server
        String userInfo = "";
        String username = currentUser;
        String role = "User";
        String status = "Đang hoạt động";
        
        if (!currentUser.isEmpty() && connectToServer()) {
            try {
                dos.writeUTF("GET_USER_INFO," + currentUser);
                dos.flush();
                String response = dis.readUTF();
                
                if (response.startsWith("USER_INFO:")) {
                    userInfo = response.substring(10);
                    String[] parts = userInfo.split(";");
                    for (String part : parts) {
                        if (part.startsWith("Tên đăng nhập: ")) {
                            username = part.substring(15);
                        } else if (part.startsWith("Vai trò: ")) {
                            role = part.substring(9);
                        } else if (part.startsWith("Trạng thái: ")) {
                            status = part.substring(12);
                        }
                    }
                }
                closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Tên đăng nhập
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        JLabel lblUsername = new JLabel(username);
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsername.setForeground(new Color(33, 150, 243));
        panel.add(lblUsername, gbc);

        // Vai trò
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        JLabel lblRole = new JLabel(role);
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRole.setForeground(new Color(76, 175, 80));
        panel.add(lblRole, gbc);

        // Trạng thái
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        JLabel lblStatus = new JLabel(status);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setForeground(new Color(76, 175, 80));
        panel.add(lblStatus, gbc);

        // Thời gian đăng nhập
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Đăng nhập lúc:"), gbc);
        gbc.gridx = 1;
        JLabel lblLoginTime = new JLabel(java.time.LocalTime.now().toString().substring(0, 8));
        lblLoginTime.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLoginTime.setForeground(new Color(156, 39, 176));
        panel.add(lblLoginTime, gbc);

        return panel;
    }
    
    private JPanel createHomeButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JButton btnProfile = createButton("Hồ sơ", new Color(33, 150, 243));
        JButton btnSettings = createButton("Cài đặt", new Color(156, 39, 176));
        JButton btnLogout = createButton("Đăng xuất", new Color(244, 67, 54));

        btnProfile.addActionListener(e -> showProfile());
        btnSettings.addActionListener(e -> showSettings());
        btnLogout.addActionListener(e -> performLogout());

        // Sắp xếp buttons theo grid
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(btnProfile, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(btnSettings, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(btnLogout, gbc);

        return panel;
    }
    
    private void updateHomePanel() {
        // Tạo lại Home panel với thông tin mới
        SwingUtilities.invokeLater(() -> {
            // Xóa Home panel cũ
            mainPanel.remove(mainPanel.getComponent(2)); // Home panel là component thứ 3 (index 2)
            
            // Tạo lại Home panel với thông tin mới
            JPanel newHomePanel = createHomePanel();
            mainPanel.add(newHomePanel, "Home");
            
            // Chuyển về Home panel
            cardLayout.show(mainPanel, "Home");
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }
    
    private void showProfile() {
        if (!connectToServer()) return;
        
        try {
            dos.writeUTF("GET_USER_INFO," + currentUser);
            dos.flush();
            String response = dis.readUTF();
            
            if (response.startsWith("USER_INFO:")) {
                String userInfo = response.substring(10);
                
                // Dialog hồ sơ chỉ xem (view-only)
                JDialog profileDialog = new JDialog(this, "Hồ sơ cá nhân", true);
                profileDialog.setSize(420, 320);
                profileDialog.setLocationRelativeTo(this);
                profileDialog.setResizable(false);
                
                JPanel contentPanel = new JPanel(new BorderLayout(12, 12));
                contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
                
                JLabel titleLabel = new JLabel("Thông tin hồ sơ", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                titleLabel.setForeground(new Color(33, 150, 243));
                contentPanel.add(titleLabel, BorderLayout.NORTH);
                
                // Dùng labels thay vì textarea để không có caret/selection
                JPanel infoGrid = new JPanel(new GridLayout(0, 1, 6, 6));
                infoGrid.setOpaque(false);
                for (String line : userInfo.split(";")) {
                    JLabel row = new JLabel(line.trim());
                    row.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    row.setFocusable(false);
                    infoGrid.add(row);
                }
                contentPanel.add(infoGrid, BorderLayout.CENTER);
                
                JButton closeBtn = createButton("Đóng", new Color(100, 100, 100));
                closeBtn.addActionListener(e -> profileDialog.dispose());
                
                JPanel buttonPanel = new JPanel(new FlowLayout());
                buttonPanel.add(closeBtn);
                contentPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                profileDialog.add(contentPanel);
                profileDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lấy thông tin hồ sơ!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            closeConnection();
        }
    }
    
    private void showSettings() {
        // Tạo dialog đẹp hơn thay vì JOptionPane
        JDialog settingsDialog = new JDialog(this, "Cài đặt tài khoản", true);
        settingsDialog.setSize(500, 400);
        settingsDialog.setLocationRelativeTo(this);
        
        // Tạo tabbed pane cho cài đặt
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Sửa thông tin cá nhân
        JPanel profilePanel = createEditProfilePanel();
        tabbedPane.addTab("Thông tin cá nhân", profilePanel);
        
        // Tab 2: Đổi mật khẩu
        JPanel passwordPanel = createChangePasswordPanel();
        tabbedPane.addTab("Đổi mật khẩu", passwordPanel);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Panel nút với styling đẹp hơn
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okBtn = createButton("OK", new Color(76, 175, 80));
        JButton cancelBtn = createButton("Hủy", new Color(100, 100, 100));
        
        okBtn.addActionListener(e -> settingsDialog.dispose());
        cancelBtn.addActionListener(e -> settingsDialog.dispose());
        
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        settingsDialog.add(contentPanel);
        settingsDialog.setVisible(true);
    }
    
    private JPanel createEditProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Chỉnh sửa thông tin cá nhân"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Lấy thông tin hiện tại
        String currentInfo = getCurrentUserInfo();
        String[] infoParts = currentInfo.split(";");
        String currentHoTen = "", currentEmail = "", currentPhone = "";
        
        for (String part : infoParts) {
            if (part.startsWith("Họ tên: ")) {
                currentHoTen = part.substring(8);
            } else if (part.startsWith("Email: ")) {
                currentEmail = part.substring(7);
                if (currentEmail.equals("Chưa cập nhật")) currentEmail = "";
            } else if (part.startsWith("SĐT: ")) {
                currentPhone = part.substring(5);
                if (currentPhone.equals("Chưa cập nhật")) currentPhone = "";
            }
        }
        
        // Họ tên
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Họ và tên:"), gbc);
        gbc.gridx = 1;
        JTextField hoTenField = new JTextField(currentHoTen, 20);
        panel.add(hoTenField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(currentEmail, 20);
        panel.add(emailField, gbc);
        
        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField(currentPhone, 20);
        panel.add(phoneField, gbc);
        
        // Nút cập nhật
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton updateBtn = createButton("Cập nhật thông tin", new Color(33, 150, 243));
        updateBtn.addActionListener(e -> {
            String newHoTen = hoTenField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newPhone = phoneField.getText().trim();
            
            if (newHoTen.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Họ tên không được để trống!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            updateUserInfo(newHoTen, newEmail, newPhone);
        });
        panel.add(updateBtn, gbc);
        
        return panel;
    }
    
    private JPanel createChangePasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Đổi mật khẩu"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Mật khẩu cũ
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mật khẩu cũ:"), gbc);
        gbc.gridx = 1;
        JPasswordField oldPassField = new JPasswordField(20);
        panel.add(oldPassField, gbc);
        
        // Mật khẩu mới
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mật khẩu mới:"), gbc);
        gbc.gridx = 1;
        JPasswordField newPassField = new JPasswordField(20);
        panel.add(newPassField, gbc);
        
        // Xác nhận mật khẩu
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Xác nhận mật khẩu:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPassField = new JPasswordField(20);
        panel.add(confirmPassField, gbc);
        
        // Nút đổi mật khẩu
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton changeBtn = createButton("Đổi mật khẩu", new Color(255, 152, 0));
        changeBtn.addActionListener(e -> {
            String oldPass = new String(oldPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());
            
            if (newPass.equals(confirmPass)) {
                changePassword(oldPass, newPass);
            } else {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(changeBtn, gbc);
        
        return panel;
    }
    
    private String getCurrentUserInfo() {
        if (!connectToServer()) return "";
        
        try {
            dos.writeUTF("GET_USER_INFO," + currentUser);
            dos.flush();
            String response = dis.readUTF();
            
            if (response.startsWith("USER_INFO:")) {
                return response.substring(10);
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
        return "";
    }
    
    private void updateUserInfo(String hoTen, String email, String soDienThoai) {
        if (!connectToServer()) return;
        
        try {
            dos.writeUTF("UPDATE_USER_INFO," + currentUser + "," + hoTen + "," + email + "," + soDienThoai);
            dos.flush();
            String response = dis.readUTF();
            
            if ("UPDATE_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                // Cập nhật lại trang chủ
                updateHomePanel();
            } else {

                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thất bại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {

            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            closeConnection();
        }
    }
    
    private void changePassword(String oldPass, String newPass) {
        if (!connectToServer()) return;
        
        try {
            dos.writeUTF("CHANGE_PASS," + currentUser + "," + oldPass + "," + newPass);
            dos.flush();
            String response = dis.readUTF();
            
            if ("CHANGE_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            closeConnection();
        }
    }
    
    private void performLogout() {
        try {
            if (socket != null && !socket.isClosed() && isConnected) {
                    dos.writeUTF("LOGOUT");
                    dos.flush();
                }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Đóng kết nối và reset trạng thái
            closeConnection();
            currentUser = "";
            cardLayout.show(mainPanel, "Login");
            JOptionPane.showMessageDialog(this, "Đã đăng xuất thành công!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void closeConnection() {
        try {
            if (dos != null) {
                dos.close();
            }
            if (dis != null) {
                dis.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            socket = null;
            dis = null;
            dos = null;
            isConnected = false;
        }
    }

    @Override
    public void dispose() {
        closeConnection();
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClientMain().setVisible(true);
        });
    }
}




