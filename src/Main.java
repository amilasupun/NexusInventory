import gui.LoginFrame;
import database.DB;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (DB.connect() != null) {
            System.out.println("Database connected!");
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Database Error! Please start XAMPP");
        }
    }
}
