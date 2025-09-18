package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class XuLyClient extends Thread {
    private Socket ketNoi;
    private QuanLyNguoiDung qlNguoiDung;

    public XuLyClient(Socket socket) {
        this.ketNoi = socket;
        this.qlNguoiDung = new QuanLyNguoiDung();
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(ketNoi.getInputStream());
            DataOutputStream dos = new DataOutputStream(ketNoi.getOutputStream());

            while (true) {
                String yeuCau = dis.readUTF();
                String[] tach = yeuCau.split(",");

                String lenh = tach[0];
                String phanHoi = "";

                switch (lenh) {
                    case "LOGIN":
                        if (qlNguoiDung.dangNhap(tach[1], tach[2])) {
                            phanHoi = "SUCCESS";
                        } else {
                            phanHoi = "FAIL";
                        }
                        break;

                    case "REGISTER":
                        if (qlNguoiDung.dangKy(tach[1], tach[2])) {
                            phanHoi = "REGISTER_SUCCESS";
                        } else {
                            phanHoi = "REGISTER_FAIL";
                        }
                        break;

                    case "CHANGE_PASS":
                        if (qlNguoiDung.doiMatKhau(tach[1], tach[2], tach[3])) {
                            phanHoi = "CHANGE_SUCCESS";
                        } else {
                            phanHoi = "CHANGE_FAIL";
                        }
                        break;

                    case "LOGOUT":
                        phanHoi = "BYE";
                        dos.writeUTF(phanHoi);
                        dos.flush();
                        ketNoi.close();
                        return;
                }

                dos.writeUTF(phanHoi);
                dos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
