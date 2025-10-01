package server;

import protocol.ThongDiepTCP;
import database.KetNoiDatabase;
import database.TaiKhoan;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Lớp máy chủ TCP xử lý các yêu cầu từ client
 */
public class MayChuTCP {
    private ServerSocket serverSocket;
    private boolean dangChay = false;
    private ExecutorService threadPool;
    private Map<String, Socket> clientOnline; // Map lưu client đang online
    private List<String> logServer; // Danh sách log
    private KetNoiDatabase database;
    
    private static final int CONG_MAC_DINH = 2712;
    
    public MayChuTCP() {
        threadPool = Executors.newCachedThreadPool();
        clientOnline = new ConcurrentHashMap<>();
        logServer = Collections.synchronizedList(new ArrayList<>());
        database = KetNoiDatabase.getInstance();
    }
    
    /**
     * Khởi động server
     */
    public boolean khoidongServer(int cong) {
        try {
            ghiLog("=== KHỞI ĐỘNG SERVER ===");
            ghiLog("Đang khởi tạo ServerSocket trên cổng " + cong + "...");
            
            serverSocket = new ServerSocket(cong);
            dangChay = true;
            
            ghiLog("ServerSocket đã được tạo thành công");
            ghiLog("Đang đặt tất cả tài khoản về offline...");
            
            // Đặt tất cả tài khoản về offline khi khởi động server
            database.datTatCaTaiKhoanOffline();
            
            ghiLog("Đã đặt tất cả tài khoản về offline");
            ghiLog("Server đã khởi động thành công trên cổng " + cong);
            ghiLog("Đang chờ kết nối từ client...");
            
            // Thread lắng nghe kết nối
            Thread threadLangNghe = new Thread(() -> {
                while (dangChay) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        String diaChiClient = clientSocket.getInetAddress().getHostAddress();
                        int portClient = clientSocket.getPort();
                        
                        ghiLog(">>> Client kết nối từ: " + diaChiClient + ":" + portClient);
                        ghiLog("Số client đang online: " + clientOnline.size());
                        
                        // Tạo thread xử lý client
                        threadPool.execute(new XuLyClient(clientSocket, MayChuTCP.this));
                        ghiLog("Đã tạo thread xử lý cho client " + diaChiClient);
                        
                    } catch (IOException e) {
                        if (dangChay) {
                            ghiLog("Lỗi khi chấp nhận kết nối: " + e.getMessage());
                        }
                    }
                }
            });
            
            threadLangNghe.start();
            return true;
            
        } catch (IOException e) {
            ghiLog("Lỗi khởi động server: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Dừng server
     */
    public void dungServer() {
        try {
            dangChay = false;
            
            // Đóng tất cả kết nối client
            for (Socket client : clientOnline.values()) {
                try {
                    client.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
            clientOnline.clear();
            
            // Đóng server socket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            // Shutdown thread pool
            threadPool.shutdown();
            
            ghiLog("Server đã dừng");
            
        } catch (IOException e) {
            ghiLog("Lỗi khi dừng server: " + e.getMessage());
        }
    }
    
    /**
     * Ghi log
     */
    public synchronized void ghiLog(String thongDiep) {
        String logMessage = "[" + new Date() + "] " + thongDiep;
        logServer.add(logMessage);
        System.out.println("SERVER LOG: " + logMessage);
        System.err.println("SERVER LOG: " + logMessage); // Thêm stderr để đảm bảo hiển thị
        
        // Giới hạn số lượng log (giữ 1000 dòng gần nhất)
        if (logServer.size() > 1000) {
            logServer.remove(0);
        }
    }
    
    /**
     * Lấy danh sách log
     */
    public List<String> layDanhSachLog() {
        return new ArrayList<>(logServer);
    }
    
    /**
     * Thêm client online
     */
    public void themClientOnline(String tenDangNhap, Socket socket) {
        clientOnline.put(tenDangNhap, socket);
        ghiLog(">>> Người dùng " + tenDangNhap + " đã đăng nhập thành công");
        ghiLog("Tổng số client online: " + clientOnline.size());
    }
    
    /**
     * Xóa client online
     */
    public void xoaClientOnline(String tenDangNhap) {
        Socket socket = clientOnline.remove(tenDangNhap);
        if (socket != null) {
            try {
                socket.close();
                ghiLog(">>> Người dùng " + tenDangNhap + " đã đăng xuất");
                ghiLog("Tổng số client online: " + clientOnline.size());
            } catch (IOException e) {
                ghiLog("Lỗi khi đóng socket của " + tenDangNhap + ": " + e.getMessage());
            }
        } else {
            ghiLog("Không tìm thấy client " + tenDangNhap + " trong danh sách online");
        }
    }
    
    
    /**
     * Buộc đăng xuất một tài khoản
     */
    public boolean buocDangXuatTaiKhoan(String tenDangNhap) {
        Socket socket = clientOnline.get(tenDangNhap);
        if (socket != null) {
            try {
                // Gửi thông báo buộc đăng xuất
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ThongDiepTCP thongDiep = new ThongDiepTCP();
                thongDiep.setLoaiYeuCau("BUOC_DANG_XUAT");
                thongDiep.setMaPhanhoi(ThongDiepTCP.THANH_CONG);
                oos.writeObject(thongDiep);
                oos.flush();
                
                // Xóa khỏi danh sách online
                xoaClientOnline(tenDangNhap);
                
                // Cập nhật database
                TaiKhoan tk = database.timTaiKhoanTheoTen(tenDangNhap);
                if (tk != null) {
                    database.capNhatTrangThaiOnline(tk.getId(), false);
                }
                
                return true;
            } catch (IOException e) {
                ghiLog("Lỗi khi buộc đăng xuất " + tenDangNhap + ": " + e.getMessage());
            }
        }
        return false;
    }
    
    /**
     * Lấy số lượng client online
     */
    public int getSoLuongClientOnline() {
        return clientOnline.size();
    }
    
    /**
     * Kiểm tra server có đang chạy không
     */
    public boolean isDangChay() {
        return dangChay;
    }
    
    /**
     * Lấy database instance
     */
    public KetNoiDatabase getDatabase() {
        return database;
    }
    
    public static void main(String[] args) {
        MayChuTCP server = new MayChuTCP();
        
        // Thêm shutdown hook để đóng server khi thoát chương trình
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Đang đóng server...");
            server.dungServer();
        }));
        
        // Khởi động server
        if (server.khoidongServer(CONG_MAC_DINH)) {
            System.out.println("Server đang chạy. Nhấn Ctrl+C để dừng.");
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                // Server bị interrupt
            }
        }
    }
}