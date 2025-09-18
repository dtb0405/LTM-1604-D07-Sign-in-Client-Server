package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KetNoiDB {
    private static final String URL = "jdbc:mysql://localhost:3306/LoginDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String TEN_NGUOI_DUNG = "root";  // thay bằng user MySQL
    private static final String MAT_KHAU = "binh";            // thay bằng pass MySQL

    public static Connection ketNoi() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, TEN_NGUOI_DUNG, MAT_KHAU);
    }
}
