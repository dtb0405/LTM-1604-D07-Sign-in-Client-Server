package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Dialog để upload và quản lý ảnh đại diện
 */
public class AvatarUploadDialog extends JDialog {
    private JLabel lblPreview;
    private JLabel lblCurrentAvatar;
    private JButton btnSelectImage;
    private JButton btnRemoveImage;
    private JButton btnSave;
    private JButton btnCancel;
    
    private String currentAvatarPath;
    private String selectedImagePath;
    private BufferedImage selectedImage;
    private boolean imageChanged = false;
    private boolean isDeletingAvatar = false;
    
    // Callback để thông báo khi ảnh được thay đổi
    private Runnable onAvatarChanged;
    
    public AvatarUploadDialog(JFrame parent, String currentAvatarPath, Runnable onAvatarChanged) {
        super(parent, "Quản lý ảnh đại diện", true);
        this.currentAvatarPath = currentAvatarPath;
        this.onAvatarChanged = onAvatarChanged;
        
        khoiTaoGiaoDien();
        hienThiAnhHienTai();
    }
    
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        // Panel chính với nền gradient mềm mại
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                // Gradient nền hồng mềm mại với nhiều lớp
                Color color1 = new Color(255, 240, 245); // Hồng nhạt
                Color color2 = new Color(255, 235, 240); // Hồng trung
                Color color3 = new Color(255, 230, 235); // Hồng đậm hơn
                
                // Gradient phức tạp với 3 lớp
                for (int y = 0; y < getHeight(); y++) {
                    float ratio = (float) y / getHeight();
                    Color currentColor;
                    if (ratio < 0.33f) {
                        float localRatio = ratio * 3;
                        int r = (int) (color1.getRed() * (1 - localRatio) + color2.getRed() * localRatio);
                        int green = (int) (color1.getGreen() * (1 - localRatio) + color2.getGreen() * localRatio);
                        int b = (int) (color1.getBlue() * (1 - localRatio) + color2.getBlue() * localRatio);
                        currentColor = new Color(r, green, b);
                    } else if (ratio < 0.66f) {
                        float localRatio = (ratio - 0.33f) * 3;
                        int r = (int) (color2.getRed() * (1 - localRatio) + color3.getRed() * localRatio);
                        int green = (int) (color2.getGreen() * (1 - localRatio) + color3.getGreen() * localRatio);
                        int b = (int) (color2.getBlue() * (1 - localRatio) + color3.getBlue() * localRatio);
                        currentColor = new Color(r, green, b);
                    } else {
                        float localRatio = (ratio - 0.66f) * 3;
                        int r = (int) (color3.getRed() * (1 - localRatio) + color1.getRed() * localRatio);
                        int green = (int) (color3.getGreen() * (1 - localRatio) + color1.getGreen() * localRatio);
                        int b = (int) (color3.getBlue() * (1 - localRatio) + color1.getBlue() * localRatio);
                        currentColor = new Color(r, green, b);
                    }
                    g2d.setColor(currentColor);
                    g2d.drawLine(0, y, getWidth(), y);
                }
                
                // Thêm hiệu ứng ánh sáng hồng mềm mại
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillOval(-80, -80, 250, 250);
                g2d.setColor(new Color(255, 220, 230, 15));
                g2d.fillOval(getWidth() - 120, getHeight() - 120, 200, 200);
                g2d.setColor(new Color(255, 210, 225, 12));
                g2d.fillOval(getWidth() / 2 - 100, getHeight() / 2 - 100, 200, 200);
                
                // Thêm hoa văn nền chìm
                g2d.setColor(new Color(255, 200, 220, 8));
                for (int i = 0; i < 20; i++) {
                    int x = (int) (Math.random() * getWidth());
                    int y = (int) (Math.random() * getHeight());
                    int size = (int) (Math.random() * 40 + 15);
                    g2d.fillOval(x, y, size, size);
                }
                
                // Thêm hoa văn trái tim nhỏ
                g2d.setColor(new Color(255, 180, 200, 6));
                for (int i = 0; i < 10; i++) {
                    int x = (int) (Math.random() * getWidth());
                    int y = (int) (Math.random() * getHeight());
                    int size = (int) (Math.random() * 25 + 10);
                    drawHeart(g2d, x, y, size);
                }
            }
        };
        mainPanel.setOpaque(false);
        add(mainPanel);
        
        // Thanh tiêu đề (AppBar)
        JPanel appBar = taoAppBar();
        mainPanel.add(appBar, BorderLayout.NORTH);
        
        // Nội dung chính với layout dọc
        JPanel contentPanel = taoNoiDungChinh();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel taoAppBar() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Nền thanh tiêu đề màu hồng
                g2d.setColor(new Color(255, 240, 250, 220));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Viền dưới màu hồng
                g2d.setColor(new Color(255, 180, 200, 150));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setPreferredSize(new Dimension(0, 60));
        
        // Tiêu đề căn giữa (không có nút quay lại)
        JLabel lblTitle = new JLabel("CÀI ĐẶT ẢNH ĐẠI DIỆN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(200, 50, 120));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitle, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel taoNoiDungChinh() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Vùng hiển thị ảnh đại diện
        JPanel avatarPanel = taoVungAnhDaiDien();
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(avatarPanel);
        
        panel.add(Box.createVerticalStrut(40));
        
        // Nút cập nhật ảnh
        btnSelectImage = ButtonUtils.createCoolButton("CẬP NHẬT ẢNH ĐẠI DIỆN");
        btnSelectImage.setPreferredSize(new Dimension(300, 50));
        btnSelectImage.setMaximumSize(new Dimension(300, 50));
        btnSelectImage.setFont(new Font("Arial", Font.BOLD, 16));
        btnSelectImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSelectImage.addActionListener(e -> chonAnh());
        panel.add(btnSelectImage);
        
        panel.add(Box.createVerticalStrut(20));
        
        // Nút xóa ảnh
        btnRemoveImage = ButtonUtils.createRedCoolButton("XOÁ ẢNH ĐẠI DIỆN");
        btnRemoveImage.setPreferredSize(new Dimension(300, 50));
        btnRemoveImage.setMaximumSize(new Dimension(300, 50));
        btnRemoveImage.setFont(new Font("Arial", Font.BOLD, 16));
        btnRemoveImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRemoveImage.addActionListener(e -> xoaAnh());
        panel.add(btnRemoveImage);
        
        panel.add(Box.createVerticalStrut(20));
        
        // Nút lưu thay đổi
        btnSave = ButtonUtils.createGreenCoolButton("LƯU THAY ĐỔI");
        btnSave.setPreferredSize(new Dimension(300, 50));
        btnSave.setMaximumSize(new Dimension(300, 50));
        btnSave.setFont(new Font("Arial", Font.BOLD, 16));
        btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSave.addActionListener(e -> luuThayDoi());
        panel.add(btnSave);
        
        return panel;
    }
    
    private JPanel taoVungAnhDaiDien() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ vòng tròn cho ảnh đại diện với nền màu hồng
                int size = Math.min(getWidth(), getHeight()) - 20;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Nền tròn màu hồng nhạt
                g2d.setColor(new Color(255, 240, 250));
                g2d.fillOval(x, y, size, size);
                
                // Viền màu hồng đậm hơn
                g2d.setColor(new Color(255, 180, 200));
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawOval(x, y, size, size);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(null); // Bỏ tất cả viền
        panel.setPreferredSize(new Dimension(200, 200));
        panel.setMaximumSize(new Dimension(200, 200));
        
        lblCurrentAvatar = new JLabel();
        lblCurrentAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblCurrentAvatar.setVerticalAlignment(SwingConstants.CENTER);
        lblCurrentAvatar.setBorder(null); // Bỏ viền của label
        panel.add(lblCurrentAvatar, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient nền hiện đại
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 245, 250, 90),
                    getWidth(), getHeight(), new Color(255, 235, 245, 70)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 30, 30);
                
                // Shadow hiện đại với nhiều lớp
                g2d.setColor(new Color(200, 100, 150, 8));
                g2d.fillRoundRect(25, 25, getWidth() - 40, getHeight() - 40, 30, 30);
                
                g2d.setColor(new Color(200, 100, 150, 5));
                g2d.fillRoundRect(30, 30, getWidth() - 40, getHeight() - 40, 30, 30);
                
                // Viền gradient hiện đại
                g2d.setColor(new Color(255, 120, 180, 80));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 30, 30);
                
                // Highlight tinh tế
                g2d.setColor(new Color(255, 200, 220, 50));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(21, 21, getWidth() - 42, getHeight() - 42, 29, 29);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel lblTitle = new JLabel("🖼️ Quản lý ảnh đại diện");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 36));
        lblTitle.setForeground(new Color(180, 40, 100));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblSubtitle = new JLabel("Tùy chỉnh ảnh đại diện của bạn");
        lblSubtitle.setFont(new Font("Arial", Font.ITALIC, 16));
        lblSubtitle.setForeground(new Color(150, 80, 100));
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(lblTitle, BorderLayout.CENTER);
        panel.add(lblSubtitle, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel taoPanelNoiDung() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient nền hiện đại
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 250, 252, 70),
                    getWidth(), getHeight(), new Color(255, 245, 250, 50)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 35, 35);
                
                // Shadow hiện đại với nhiều lớp
                g2d.setColor(new Color(200, 100, 150, 6));
                g2d.fillRoundRect(25, 25, getWidth() - 40, getHeight() - 40, 35, 35);
                
                g2d.setColor(new Color(200, 100, 150, 3));
                g2d.fillRoundRect(30, 30, getWidth() - 40, getHeight() - 40, 35, 35);
                
                // Viền gradient hiện đại
                g2d.setColor(new Color(255, 120, 180, 60));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 35, 35);
                
                // Highlight tinh tế
                g2d.setColor(new Color(255, 200, 220, 30));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(21, 21, getWidth() - 42, getHeight() - 42, 34, 34);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        
        // Nút chọn ảnh - Đẩy lên trên
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 15, 35, 15); // Tăng khoảng cách dưới nút
        btnSelectImage = ButtonUtils.createCoolButton("📁 Chọn ảnh mới");
        btnSelectImage.setPreferredSize(new Dimension(220, 50));
        btnSelectImage.setFont(new Font("Arial", Font.BOLD, 18));
        btnSelectImage.addActionListener(e -> chonAnh());
        panel.add(btnSelectImage, gbc);
        
        // Nút xóa ảnh
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.insets = new Insets(15, 15, 35, 15); // Tăng khoảng cách dưới nút
        btnRemoveImage = ButtonUtils.createRedCoolButton("🗑️ Xóa ảnh đại diện");
        btnRemoveImage.setPreferredSize(new Dimension(220, 50));
        btnRemoveImage.setFont(new Font("Arial", Font.BOLD, 18));
        btnRemoveImage.addActionListener(e -> xoaAnh());
        panel.add(btnRemoveImage, gbc);
        
        // Ảnh hiện tại - Đẩy xuống dưới với khoảng cách phù hợp
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 15, 15, 15); // Khoảng cách phù hợp
        JLabel lblCurrentTitle = new JLabel("🖼️ Ảnh hiện tại");
        lblCurrentTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblCurrentTitle.setForeground(new Color(180, 40, 100));
        panel.add(lblCurrentTitle, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 15, 25, 15); // Khoảng cách dưới ảnh
        lblCurrentAvatar = new JLabel();
        lblCurrentAvatar.setPreferredSize(new Dimension(200, 200)); // Tăng kích thước
        lblCurrentAvatar.setBorder(new LineBorder(new Color(255, 120, 180, 180), 3));
        lblCurrentAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblCurrentAvatar.setVerticalAlignment(SwingConstants.CENTER);
        panel.add(lblCurrentAvatar, gbc);
        
        // Ảnh mới được chọn
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.insets = new Insets(25, 15, 15, 15); // Khoảng cách phù hợp
        JLabel lblNewTitle = new JLabel("✨ Ảnh mới");
        lblNewTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblNewTitle.setForeground(new Color(180, 40, 100));
        panel.add(lblNewTitle, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 15, 25, 15); // Khoảng cách dưới ảnh
        lblPreview = new JLabel("Chưa chọn ảnh");
        lblPreview.setPreferredSize(new Dimension(200, 200)); // Tăng kích thước
        lblPreview.setBorder(new LineBorder(new Color(255, 180, 200, 180), 3));
        lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreview.setVerticalAlignment(SwingConstants.CENTER);
        lblPreview.setForeground(new Color(150, 100, 120));
        lblPreview.setFont(new Font("Arial", Font.ITALIC, 18));
        panel.add(lblPreview, gbc);
        
        return panel;
    }
    
    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 30)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient nền hiện đại
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 245, 250, 80),
                    getWidth(), getHeight(), new Color(255, 240, 250, 60)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 30, 30);
                
                // Shadow hiện đại với nhiều lớp
                g2d.setColor(new Color(200, 100, 150, 10));
                g2d.fillRoundRect(25, 25, getWidth() - 40, getHeight() - 40, 30, 30);
                
                g2d.setColor(new Color(200, 100, 150, 5));
                g2d.fillRoundRect(30, 30, getWidth() - 40, getHeight() - 40, 30, 30);
                
                // Viền gradient hiện đại
                g2d.setColor(new Color(255, 120, 180, 70));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 30, 30);
                
                // Highlight tinh tế
                g2d.setColor(new Color(255, 200, 220, 40));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(21, 21, getWidth() - 42, getHeight() - 42, 29, 29);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(35, 35, 35, 35));
        
        btnSave = ButtonUtils.createGreenCoolButton("💾 Lưu thay đổi");
        btnSave.setPreferredSize(new Dimension(220, 55));
        btnSave.setFont(new Font("Arial", Font.BOLD, 20));
        btnSave.addActionListener(e -> luuThayDoi());
        panel.add(btnSave);
        
        btnCancel = ButtonUtils.createRedCoolButton("❌ Hủy");
        btnCancel.setPreferredSize(new Dimension(220, 55));
        btnCancel.setFont(new Font("Arial", Font.BOLD, 20));
        btnCancel.addActionListener(e -> dispose());
        panel.add(btnCancel);
        
        return panel;
    }
    
    /**
     * Vẽ hoa văn trái tim
     */
    private void drawHeart(Graphics2D g2d, int x, int y, int size) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Vẽ trái tim đơn giản
        int heartSize = size / 2;
        int centerX = x + heartSize;
        int centerY = y + heartSize;
        
        // Vẽ 2 nửa trái tim
        g2d.fillOval(centerX - heartSize, centerY - heartSize/2, heartSize, heartSize);
        g2d.fillOval(centerX, centerY - heartSize/2, heartSize, heartSize);
        
        // Vẽ phần nhọn dưới
        int[] xPoints = {centerX - heartSize, centerX, centerX + heartSize};
        int[] yPoints = {centerY + heartSize/2, centerY + heartSize, centerY + heartSize/2};
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    private void hienThiAnhHienTai() {
        if (currentAvatarPath != null && !currentAvatarPath.isEmpty() && !currentAvatarPath.equals("default_avatar.png")) {
            try {
                File avatarFile = new File(currentAvatarPath);
                if (avatarFile.exists()) {
                    BufferedImage currentImage = ImageIO.read(avatarFile);
                    BufferedImage croppedImage = cropToSquare(currentImage, 180); // 200 - 20 (border + margin)
                    ImageIcon icon = new ImageIcon(croppedImage);
                    lblCurrentAvatar.setIcon(icon);
                    lblCurrentAvatar.setText("");
                } else {
                    hienThiAnhMacDinh();
                }
            } catch (IOException e) {
                hienThiAnhMacDinh();
            }
        } else {
            hienThiAnhMacDinh();
        }
    }
    
    private void hienThiAnhMacDinh() {
        // Tạo ảnh mặc định với chữ cái đầu của tên đăng nhập
        BufferedImage defaultImage = new BufferedImage(180, 180, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = defaultImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Vẽ nền tròn màu hồng nhạt
        g2d.setColor(new Color(255, 240, 250));
        g2d.fillOval(6, 6, 168, 168);
        
        // Lấy chữ cái đầu của tên đăng nhập
        String firstLetter = getFirstLetterOfUsername();
        
        // Vẽ chữ cái đầu căn giữa hoàn hảo
        g2d.setColor(new Color(200, 50, 120)); // Màu hồng đậm cho chữ
        g2d.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics fm = g2d.getFontMetrics();
        
        // Căn giữa hoàn hảo
        int textWidth = fm.stringWidth(firstLetter);
        int textHeight = fm.getHeight();
        int x = (180 - textWidth) / 2;
        int y = (180 - textHeight) / 2 + fm.getAscent();
        
        g2d.drawString(firstLetter, x, y);
        
        g2d.dispose();
        
        ImageIcon icon = new ImageIcon(defaultImage);
        lblCurrentAvatar.setIcon(icon);
        lblCurrentAvatar.setText("");
    }
    
    /**
     * Lấy chữ cái đầu của tên đăng nhập
     */
    private String getFirstLetterOfUsername() {
        try {
            // Lấy tên đăng nhập từ parent frame
            if (getParent() instanceof GiaoDienUser) {
                GiaoDienUser parentFrame = (GiaoDienUser) getParent();
                String username = parentFrame.getTaiKhoanHienTai().getTenDangNhap();
                return username.substring(0, 1).toUpperCase();
            } else if (getParent() instanceof GiaoDienAdmin) {
                GiaoDienAdmin parentFrame = (GiaoDienAdmin) getParent();
                String username = parentFrame.getTaiKhoanAdmin().getTenDangNhap();
                return username.substring(0, 1).toUpperCase();
            }
        } catch (Exception e) {
            // Fallback nếu không lấy được
        }
        return "U"; // Default fallback
    }
    
    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn ảnh đại diện");
        
        // Chỉ cho phép chọn file ảnh
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Ảnh (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();
            
            try {
                selectedImage = ImageIO.read(selectedFile);
                
                 // Cắt ảnh thành hình vuông và resize
                 BufferedImage croppedImage = cropToSquare(selectedImage, 180); // 200 - 20 (border + margin)
                 ImageIcon icon = new ImageIcon(croppedImage);
                lblCurrentAvatar.setIcon(icon);
                lblCurrentAvatar.setText("");
                
                imageChanged = true;
                btnSave.setEnabled(true);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Không thể đọc file ảnh: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void xoaAnh() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa ảnh đại diện?",
            "Xác nhận xóa ảnh", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            selectedImage = null;
            selectedImagePath = null;
            imageChanged = true;
            isDeletingAvatar = true;
            
            // Hiển thị ảnh mặc định với chữ cái đầu
            hienThiAnhMacDinh();
            
            btnSave.setEnabled(true);
        }
    }
    
    private void luuThayDoi() {
        if (!imageChanged) {
            JOptionPane.showMessageDialog(this, 
                "Không có thay đổi nào để lưu.", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            database.KetNoiDatabase db = database.KetNoiDatabase.getInstance();
            boolean success = false;
            
            if (isDeletingAvatar) {
                // Xóa ảnh đại diện khỏi database
                success = db.xoaAnhDaiDien(getCurrentUserId());
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Đã xóa ảnh đại diện thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (selectedImage != null) {
                // Lưu ảnh vào thư mục avatars
                File avatarsDir = new File("avatars");
                if (!avatarsDir.exists()) {
                    avatarsDir.mkdirs();
                }
                
                // Tạo tên file unique
                String fileName = "avatar_" + System.currentTimeMillis() + ".png";
                File avatarFile = new File(avatarsDir, fileName);
                
                // Lưu ảnh
                ImageIO.write(selectedImage, "PNG", avatarFile);
                String newAvatarPath = avatarFile.getAbsolutePath();
                
                // Cập nhật database với đường dẫn ảnh
                success = db.capNhatAnhDaiDien(getCurrentUserId(), newAvatarPath);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Đã cập nhật ảnh đại diện thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            if (success) {
                // Cập nhật giao diện
                if (onAvatarChanged != null) {
                    onAvatarChanged.run();
                }
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi cập nhật ảnh đại diện trong database.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi lưu ảnh đại diện: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int getCurrentUserId() {
        // Lấy ID của user hiện tại từ parent frame
        if (getParent() instanceof GiaoDienUser) {
            GiaoDienUser parentFrame = (GiaoDienUser) getParent();
            return parentFrame.getTaiKhoanHienTai().getId();
        } else if (getParent() instanceof GiaoDienAdmin) {
            GiaoDienAdmin parentFrame = (GiaoDienAdmin) getParent();
            return parentFrame.getTaiKhoanAdmin().getId();
        }
        return 0; // Fallback
    }
    
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        
        return resizedImage;
    }
    
    /**
     * Cắt ảnh thành hình vuông từ giữa
     */
    private BufferedImage cropToSquare(BufferedImage originalImage, int size) {
        if (originalImage == null) {
            return null;
        }
        
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // Tạo ảnh vuông mới với nền trong suốt
        BufferedImage squareImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = squareImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        
        // Tính toán vùng cắt thông minh
        int cropSize;
        int startX, startY;
        
        if (originalWidth > originalHeight) {
            // Ảnh ngang - cắt theo chiều rộng, giữ chiều cao
            cropSize = originalHeight;
            startX = (originalWidth - cropSize) / 2; // Cắt ở giữa
            startY = 0;
        } else if (originalHeight > originalWidth) {
            // Ảnh dọc - cắt theo chiều cao, giữ chiều rộng
            cropSize = originalWidth;
            startX = 0;
            startY = (originalHeight - cropSize) / 2; // Cắt ở giữa
        } else {
            // Ảnh vuông - giữ nguyên
            cropSize = Math.min(originalWidth, originalHeight);
            startX = 0;
            startY = 0;
        }
        
        // Vẽ ảnh đã cắt và resize để vừa khung vuông
        g2d.drawImage(originalImage, 0, 0, size, size, 
                     startX, startY, startX + cropSize, startY + cropSize, null);
        
        g2d.dispose();
        return squareImage;
    }
    
    public String getNewAvatarPath() {
        return selectedImagePath;
    }
    
    public boolean isImageRemoved() {
        return imageChanged && selectedImage == null;
    }
}
