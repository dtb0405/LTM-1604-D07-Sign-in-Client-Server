package gui;

import javax.swing.*;

/**
 * Utility class for password validation
 */
public class PasswordValidator {
    public static final int MIN_PASSWORD_LENGTH = 6;
    
    /**
     * Kiểm tra mật khẩu có hợp lệ không
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return password.length() >= MIN_PASSWORD_LENGTH;
    }
    
    /**
     * Lấy thông báo lỗi
     */
    public static String getErrorMessage() {
        return "Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự";
    }
    
    /**
     * Kiểm tra mật khẩu và hiển thị lỗi nếu không hợp lệ
     */
    public static boolean validateAndShowError(String password, java.awt.Component parentComponent) {
        if (!isValidPassword(password)) {
            JOptionPane.showMessageDialog(parentComponent, 
                getErrorMessage(), 
                "Lỗi mật khẩu", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
