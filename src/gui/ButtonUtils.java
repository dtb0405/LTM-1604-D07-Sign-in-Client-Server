package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Utility class để tạo các nút với hiệu ứng đổ bóng lên
 */
public class ButtonUtils {
    
    /**
     * Tạo nút với hiệu ứng đổ bóng lên và tông màu lạnh
     */
    public static JButton createElevatedButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            
            int width = getWidth();
            int height = getHeight();
            int arcRadius = 25;
            
            // Màu nền động dựa trên trạng thái
            Color currentBg = backgroundColor;
            if (isPressed) {
                currentBg = backgroundColor.darker();
            } else if (isHovered) {
                currentBg = backgroundColor.brighter();
            }
            
            // Vẽ shadow đa lớp xuống dưới để tránh bị khuất
            int shadowOffset = isPressed ? 1 : (isHovered ? 3 : 2);
            
            // Shadow 1 - đậm nhất
            g2d.setColor(new Color(0, 0, 0, isHovered ? 25 : 15));
            g2d.fillRoundRect(shadowOffset, shadowOffset, width, height, arcRadius, arcRadius);
            
            // Shadow 2 - trung bình
            g2d.setColor(new Color(0, 0, 0, isHovered ? 20 : 10));
            g2d.fillRoundRect(shadowOffset - 1, shadowOffset - 1, width, height, arcRadius, arcRadius);
            
            // Shadow 3 - nhạt nhất
            g2d.setColor(new Color(0, 0, 0, isHovered ? 15 : 8));
            g2d.fillRoundRect(shadowOffset - 2, shadowOffset - 2, width, height, arcRadius, arcRadius);
            
            // Vẽ background chính
            g2d.setColor(currentBg);
            g2d.fillRoundRect(0, 0, width - shadowOffset, height - shadowOffset, arcRadius, arcRadius);
            
            // Vẽ highlight trên cùng
            g2d.setColor(new Color(255, 255, 255, isHovered ? 60 : 40));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(0, 0, width - shadowOffset - 1, height - shadowOffset - 1, arcRadius, arcRadius);
            
            // Vẽ border tinh tế
            g2d.setColor(new Color(255, 255, 255, isHovered ? 80 : 60));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(1, 1, width - shadowOffset - 3, height - shadowOffset - 3, arcRadius - 2, arcRadius - 2);
            
            // Vẽ text với shadow
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            String buttonText = getText();
            int textWidth = fm.stringWidth(buttonText);
            int textHeight = fm.getHeight();
            int x = (width - textWidth) / 2;
            int y = (height + textHeight / 2) / 2;
            
            // Text shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.drawString(buttonText, x + 1, y + 1);
            
            // Text chính
            g2d.setColor(Color.WHITE);
            g2d.drawString(buttonText, x, y);
            }
            
            @Override
            public void addNotify() {
                super.addNotify();
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        repaint();
                    }
                    
                    @Override
                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        isPressed = false;
                        repaint();
                    }
                });
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(180, 60)); // Tăng kích thước để đảm bảo hiển thị đủ
        button.setMinimumSize(new Dimension(160, 50)); // Kích thước tối thiểu
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }
    
    /**
     * Tạo nút nhỏ với hiệu ứng đổ bóng lên và tông màu lạnh
     */
    public static JButton createSmallElevatedButton(String text, Color backgroundColor) {
        JButton button = createElevatedButton(text, backgroundColor);
        button.setPreferredSize(new Dimension(150, 50)); // Tăng kích thước
        button.setMinimumSize(new Dimension(130, 45));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        return button;
    }
    
    /**
     * Tạo nút lớn với hiệu ứng đổ bóng lên và tông màu lạnh
     */
    public static JButton createLargeElevatedButton(String text, Color backgroundColor) {
        JButton button = createElevatedButton(text, backgroundColor);
        button.setPreferredSize(new Dimension(240, 70)); // Tăng kích thước
        button.setMinimumSize(new Dimension(200, 60));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        return button;
    }
    
    /**
     * Tạo nút với tông màu lạnh mặc định
     */
    public static JButton createCoolButton(String text) {
        return createElevatedButton(text, new Color(33, 150, 243)); // Xanh dương
    }
    
    /**
     * Tạo nút xanh lá với tông màu lạnh
     */
    public static JButton createGreenCoolButton(String text) {
        return createElevatedButton(text, new Color(76, 175, 80)); // Xanh lá
    }
    
    /**
     * Tạo nút xanh cyan với tông màu lạnh
     */
    public static JButton createCyanCoolButton(String text) {
        return createElevatedButton(text, new Color(0, 188, 212)); // Cyan
    }
    
    /**
     * Tạo nút tím với tông màu lạnh
     */
    public static JButton createPurpleCoolButton(String text) {
        return createElevatedButton(text, new Color(156, 39, 176)); // Tím
    }
    
    /**
     * Tạo nút cam với tông màu lạnh
     */
    public static JButton createOrangeCoolButton(String text) {
        return createElevatedButton(text, new Color(255, 152, 0)); // Cam
    }
    
    /**
     * Tạo nút đỏ với tông màu lạnh
     */
    public static JButton createRedCoolButton(String text) {
        return createElevatedButton(text, new Color(244, 67, 54)); // Đỏ
    }
}
