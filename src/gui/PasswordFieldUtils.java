package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Utility class để tạo JPasswordField với nút ẩn/hiện mật khẩu
 */
public class PasswordFieldUtils {
    
    /**
     * Tạo JPasswordField với nút ẩn/hiện mật khẩu
     * @param columns Số cột cho password field
     * @return JPanel chứa password field và nút toggle
     */
    public static JPanel createPasswordFieldWithToggle(int columns) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JPasswordField passwordField = new JPasswordField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.setFont(getFont().deriveFont(Font.ITALIC));
                    g2d.drawString("Nhập mật khẩu...", 15, getHeight()/2 + 5);
                    g2d.dispose();
                }
            }
        };
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Nút toggle với icon con mắt
        EyeIconButton toggleButton = new EyeIconButton();
        
        // Action listener cho nút toggle
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleButton.toggle();
                if (toggleButton.isShowingPassword()) {
                    // Hiện mật khẩu
                    passwordField.setEchoChar((char) 0);
                } else {
                    // Ẩn mật khẩu
                    passwordField.setEchoChar('•');
                }
                passwordField.repaint();
            }
        });
        
        // Focus listener để ẩn/hiện placeholder
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setEchoChar('•');
                }
                passwordField.repaint();
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setEchoChar((char) 0);
                }
                passwordField.repaint();
            }
        });
        
        panel.add(passwordField, BorderLayout.CENTER);
        panel.add(toggleButton, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Tạo JPasswordField với nút ẩn/hiện mật khẩu (không có placeholder)
     * @param columns Số cột cho password field
     * @return JPanel chứa password field và nút toggle
     */
    public static JPanel createPasswordFieldWithToggleNoPlaceholder(int columns) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Nút toggle với icon con mắt
        EyeIconButton toggleButton = new EyeIconButton();
        
        // Action listener cho nút toggle
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleButton.toggle();
                if (toggleButton.isShowingPassword()) {
                    // Hiện mật khẩu
                    passwordField.setEchoChar((char) 0);
                } else {
                    // Ẩn mật khẩu
                    passwordField.setEchoChar('•');
                }
                passwordField.repaint();
            }
        });
        
        panel.add(passwordField, BorderLayout.CENTER);
        panel.add(toggleButton, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Lấy JPasswordField từ panel
     * @param panel Panel chứa password field
     * @return JPasswordField
     */
    public static JPasswordField getPasswordField(JPanel panel) {
        return (JPasswordField) panel.getComponent(0);
    }
}
