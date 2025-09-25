package Server;

import javax.swing.SwingUtilities;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class XuLyClient extends Thread {
    private Socket ketNoi;
    private QuanLyNguoiDung qlNguoiDung;
    private String ipDiaChi;
    private ServerMain serverMain;
    private String currentUser = "";

    public XuLyClient(Socket socket, ServerMain serverMain) {
        this.ketNoi = socket;
        this.qlNguoiDung = new QuanLyNguoiDung();
        this.ipDiaChi = socket.getInetAddress().getHostAddress();
        this.serverMain = serverMain;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(ketNoi.getInputStream());
            DataOutputStream out = new DataOutputStream(ketNoi.getOutputStream());

            while (true) {
                String yeuCau = in.readUTF();
                String[] tach = yeuCau.split(",");

                String lenh = tach[0];
                String phanHoi = "";

                switch (lenh) {
                    case "LOGIN":
                        if (qlNguoiDung.dangNhap(tach[1], tach[2])) {
                            phanHoi = "SUCCESS";
                            // Lưu username và thêm vào danh sách online
                            currentUser = tach[1];
                            QuanLyNguoiDung.themUserOnline(tach[1]);
                            
                            // Ghi lịch sử đăng nhập
                            String userInfo = qlNguoiDung.layThongTinNguoiDung(tach[1]);
                            String[] infoParts = userInfo.split(";");
                            String hoTen = "", vaiTro = "";
                            for (String part : infoParts) {
                                if (part.startsWith("Họ tên: ")) {
                                    hoTen = part.substring(8);
                                } else if (part.startsWith("Vai trò: ")) {
                                    vaiTro = part.substring(9);
                                }
                            }
                            QuanLyNguoiDung.ghiLichSuDangNhap(tach[1], hoTen, vaiTro);
                            
                            // Cập nhật UI server realtime
                            if (serverMain != null) {
                                serverMain.updateClientCount();
                            }
                        } else {
                            phanHoi = "FAIL";
                        }
                        break;

                    case "REGISTER":
                        // Format: REGISTER,hoTen,tenDangNhap,matKhau,email,soDienThoai
                        if (tach.length >= 6) {
                            if (qlNguoiDung.dangKy(tach[1], tach[2], tach[3], tach[4], tach[5])) {
                                phanHoi = "REGISTER_SUCCESS";
                            } else {
                                phanHoi = "REGISTER_FAIL";
                            }
                        } else {
                            // Fallback cho format cũ: REGISTER,tenDangNhap,matKhau
                            if (qlNguoiDung.dangKy(tach[1], tach[1], tach[2], "", "")) {
                                phanHoi = "REGISTER_SUCCESS";
                            } else {
                                phanHoi = "REGISTER_FAIL";
                            }
                        }
                        break;

                    case "CHANGE_PASS":
                        if (qlNguoiDung.capNhatMatKhau(tach[1], tach[2], tach[3])) {
                            phanHoi = "CHANGE_SUCCESS";
                            // Cập nhật server UI realtime
                            if (serverMain != null) {
                                serverMain.updateClientCount();
                            }
                        } else {
                            phanHoi = "CHANGE_FAIL";
                        }
                        break;

                    case "UPDATE_USER_INFO":
                        if (tach.length > 4) {
                            if (qlNguoiDung.capNhatThongTinNguoiDung(tach[1], tach[2], tach[3], tach[4])) {
                                phanHoi = "UPDATE_SUCCESS";
                                // Cập nhật server UI realtime
                                if (serverMain != null) {
                                    serverMain.updateClientCount();
                                }
                            } else {
                                phanHoi = "UPDATE_FAIL";
                            }
                        } else {
                            phanHoi = "UPDATE_ERROR";
                        }
                        break;

                    case "GET_ONLINE_USERS":
                        List<String> onlineUsers = qlNguoiDung.layDanhSachNguoiDungOnline();
                        phanHoi = "ONLINE_USERS:" + String.join(";", onlineUsers);
                        break;

                    case "GET_STATS":
                        int[] stats = qlNguoiDung.layThongKeNguoiDung();
                        phanHoi = "STATS:" + stats[0] + "," + stats[1] + "," + stats[2] + "," + stats[3];
                        break;

                    case "SEARCH_USERS":
                        if (tach.length > 1) {
                            List<String> searchResults = qlNguoiDung.timKiemNguoiDung(tach[1]);
                            phanHoi = "SEARCH_RESULTS:" + String.join(";", searchResults);
                        } else {
                            phanHoi = "SEARCH_ERROR";
                        }
                        break;

                    case "FILTER_USERS":
                        if (tach.length > 2) {
                            List<String> filterResults = qlNguoiDung.locNguoiDung(tach[1], tach[2]);
                            phanHoi = "FILTER_RESULTS:" + String.join(";", filterResults);
                        } else {
                            phanHoi = "FILTER_ERROR";
                        }
                        break;

                    case "LOCK_USER":
                        if (tach.length > 2) {
                            boolean lock = Boolean.parseBoolean(tach[2]);
                            if (qlNguoiDung.khoaTaiKhoan(tach[1], lock)) {
                                phanHoi = "LOCK_SUCCESS";
                            } else {
                                phanHoi = "LOCK_FAIL";
                            }
                        } else {
                            phanHoi = "LOCK_ERROR";
                        }
                        break;

                    case "UPDATE_ROLE":
                        if (tach.length > 2) {
                            if (qlNguoiDung.capNhatVaiTro(tach[1], tach[2])) {
                                phanHoi = "ROLE_UPDATE_SUCCESS";
                            } else {
                                phanHoi = "ROLE_UPDATE_FAIL";
                            }
                        } else {
                            phanHoi = "ROLE_UPDATE_ERROR";
                        }
                        break;

                    case "DELETE_USER":
                        if (tach.length > 1) {
                            if (qlNguoiDung.xoaTaiKhoan(tach[1])) {
                                phanHoi = "DELETE_SUCCESS";
                            } else {
                                phanHoi = "DELETE_FAIL";
                            }
                        } else {
                            phanHoi = "DELETE_ERROR";
                        }
                        break;

                    case "GET_LOGIN_HISTORY":
                        if (tach.length > 1) {
                            List<String> history = qlNguoiDung.layLichSuDangNhap(tach[1]);
                            phanHoi = "LOGIN_HISTORY:" + String.join(";", history);
                        } else {
                            phanHoi = "HISTORY_ERROR";
                        }
                        break;

                    case "GET_USER_INFO":
                        if (tach.length > 1) {
                            // Lấy thông tin user từ database
                            String userInfo = qlNguoiDung.layThongTinNguoiDung(tach[1]);
                            phanHoi = "USER_INFO:" + userInfo;
                        } else {
                            phanHoi = "USER_INFO_ERROR";
                        }
                        break;

                    case "LOGOUT":
                        // Xóa user khỏi danh sách online
                        if (!currentUser.isEmpty()) {
                            QuanLyNguoiDung.xoaUserOnline(currentUser);
                            // Cập nhật UI server realtime
                            if (serverMain != null) {
                                serverMain.updateClientCount();
                            }
                        }
                        phanHoi = "BYE";
                        out.writeUTF(phanHoi);
                        out.flush();
                        ketNoi.close();
                        return;

                    default:
                        phanHoi = "UNKNOWN_COMMAND";
                        break;
                }

                out.writeUTF(phanHoi);
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Client inconnected: " + ipDiaChi);
        } finally {
            // Xóa user khỏi danh sách online khi inconnect
            if (!currentUser.isEmpty()) {
                QuanLyNguoiDung.xoaUserOnline(currentUser);
                // Cập nhật UI server realtime
                if (serverMain != null) {
                    serverMain.updateClientCount();
                }
            }
            try {
                if (ketNoi != null && !ketNoi.isClosed()) {
                    ketNoi.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
