package database;
import java.sql.*;

public class DB {
    private static Connection conn;
    
    public static Connection connect() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/nexusinventory", "root", ""
                );
            }
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
