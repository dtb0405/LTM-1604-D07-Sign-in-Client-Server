package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Utility class để tạo nút với icon con mắt
 */
public class EyeIconButton extends JButton {
    private boolean showPassword = false;
    
    public EyeIconButton() {
        setOpaque(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setPreferredSize(new Dimension(50, 40));
        setToolTipText("Hiện mật khẩu");
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int eyeSize = Math.min(width, height) * 2 / 3;
        
        // Vẽ nền trong suốt
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, width, height);
        
        if (showPassword) {
            // Mắt mở - vẽ con mắt đẹp hơn
            g2d.setColor(new Color(25, 118, 210));
            g2d.setStroke(new BasicStroke(2.5f));
            
            // Vẽ hình oval cho mắt (to hơn)
            g2d.drawOval(centerX - eyeSize/2, centerY - eyeSize/2, eyeSize, eyeSize);
            
            // Vẽ con ngươi với gradient
            g2d.setColor(new Color(25, 118, 210));
            g2d.fillOval(centerX - eyeSize/3, centerY - eyeSize/3, eyeSize*2/3, eyeSize*2/3);
            
            // Vẽ highlight trên con ngươi
            g2d.setColor(new Color(255, 255, 255, 180));
            g2d.fillOval(centerX - eyeSize/6, centerY - eyeSize/6, eyeSize/3, eyeSize/3);
            
        } else {
            // Mắt nhắm - vẽ đường cong mềm mại hơn
            g2d.setColor(new Color(25, 118, 210));
            g2d.setStroke(new BasicStroke(3f));
            
            // Vẽ đường cong mềm mại
            g2d.drawArc(centerX - eyeSize/2, centerY - eyeSize/2, eyeSize, eyeSize, 0, 180);
            
            // Vẽ thêm đường cong nhỏ bên trong
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawArc(centerX - eyeSize/3, centerY - eyeSize/3, eyeSize*2/3, eyeSize*2/3, 0, 180);
        }
    }
    
    public void toggle() {
        showPassword = !showPassword;
        setToolTipText(showPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu");
        repaint();
    }
    
    public boolean isShowingPassword() {
        return showPassword;
    }
}

