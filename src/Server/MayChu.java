package Server;

import javax.swing.*;
import java.awt.*;

public class MayChu extends JFrame {
    private DefaultListModel<String> onlineUsersModel;
    private JList<String> onlineUsersList;
    private JButton btnLogout, btnAdd, btnEdit, btnDelete;

    public MayChu() {
        setTitle("Quản lý Server - Người dùng Online");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== Panel chính =====
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));

        // ===== Tiêu đề =====
        JLabel lblTitle = new JLabel("Danh sách người dùng Online", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(50, 50, 50));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // ===== Danh sách user =====
        onlineUsersModel = new DefaultListModel<>();
        onlineUsersList = new JList<>(onlineUsersModel);
        onlineUsersList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(onlineUsersList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ===== Panel chứa nút chức năng =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        btnLogout = createButton("Đăng xuất tài khoản");
        btnAdd = createButton("Thêm tài khoản");
        btnEdit = createButton("Sửa tài khoản");
        btnDelete = createButton("Xóa tài khoản");

        buttonPanel.add(btnLogout);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // ===== Hàm tạo nút với hover effect và bo góc =====
    private JButton createButton(String text) {
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

    // ===== Thêm user vào danh sách online =====
    public void addUser(String username) {
        onlineUsersModel.addElement(username);
    }

    // ===== Xóa user khỏi danh sách online =====
    public void removeUser(String username) {
        onlineUsersModel.removeElement(username);
    }

    // ===== Chạy thử GUI =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MayChu::new);
    }
}
