package Client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class GiaoDienNguoiDung extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public GiaoDienNguoiDung() {
        setTitle("Hệ thống đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 640);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "Login");
        mainPanel.add(createRegisterPanel(), "Register");

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
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xAAAAAA), 1, true),
                new EmptyBorder(10, 40, 10, 12)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBounds(10, 8, 25, 25);

        JPanel fieldPanel = new JPanel(null);
        fieldPanel.setOpaque(false);
        field.setBounds(0, 0, 360, 40);
        fieldPanel.add(field);
        fieldPanel.add(iconLabel);
        fieldPanel.setPreferredSize(new Dimension(360, 40));

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
        btn.setBorder(new EmptyBorder(12, 15, 12, 15));

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                ButtonModel model = ((AbstractButton) c).getModel();
                Color hover = baseColor.brighter();
                GradientPaint gp = new GradientPaint(0, 0,
                        model.isRollover() ? hover : baseColor,
                        0, c.getHeight(),
                        model.isRollover() ? baseColor : baseColor.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 30, 30);

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

        JLabel title = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(33, 150, 243));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.add(createLabeledField("Tên đăng nhập", false,
                UIManager.getIcon("FileView.fileIcon")));
        form.add(Box.createVerticalStrut(15));
        form.add(createLabeledField("Mật khẩu", true,
                UIManager.getIcon("FileView.hardDriveIcon")));
        form.add(Box.createVerticalStrut(25));

        JButton loginBtn = createButton("Đăng nhập", new Color(76, 175, 80));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(loginBtn);

        JLabel registerLink = new JLabel("Chưa có tài khoản? Đăng ký ngay");
        registerLink.setForeground(new Color(0x1565C0));
        registerLink.setHorizontalAlignment(SwingConstants.CENTER);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "Register");
            }
        });

        card.add(title, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(registerLink, BorderLayout.SOUTH);

        bg.add(card);
        return bg;
    }

    private JPanel createRegisterPanel() {
        JPanel bg = createGradientBackground();
        JPanel card = createCardPanel();

        JLabel title = new JLabel("ĐĂNG KÝ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(233, 30, 99));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.add(createLabeledField("Tên đăng nhập", false,
                UIManager.getIcon("FileView.fileIcon")));
        form.add(Box.createVerticalStrut(15));
        form.add(createLabeledField("Mật khẩu", true,
                UIManager.getIcon("FileView.hardDriveIcon")));
        form.add(Box.createVerticalStrut(15));
        form.add(createLabeledField("Nhập lại mật khẩu", true,
                UIManager.getIcon("FileView.hardDriveIcon")));
        form.add(Box.createVerticalStrut(25));

        JButton registerBtn = createButton("Đăng ký", new Color(244, 81, 30));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(registerBtn);

        JLabel loginLink = new JLabel("Đã có tài khoản? Quay lại đăng nhập");
        loginLink.setForeground(new Color(0x1565C0));
        loginLink.setHorizontalAlignment(SwingConstants.CENTER);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "Login");
            }
        });

        card.add(title, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(loginLink, BorderLayout.SOUTH);

        bg.add(card);
        return bg;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GiaoDienNguoiDung().setVisible(true));
    }
}
