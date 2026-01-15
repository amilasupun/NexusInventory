package gui;

import database.DB;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    JTextField txtUser;
    JPasswordField txtPass;
    
    public LoginFrame() {
        setTitle("NexusInventory Login");
        setSize(350, 220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setupUI();
    }
    
    void setupUI() {
        setLayout(new BorderLayout());
        
        // Header part (M) 
        JPanel header = new JPanel();
        header.setBackground(Color.BLACK);
        JLabel lblHeader = new JLabel("NexusInventory System");
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(lblHeader);
        add(header, BorderLayout.NORTH);
        
        // Main panel part (M) 
        JPanel main = new JPanel();
        main.setLayout(new GridLayout(3, 2, 10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        main.add(new JLabel("Username:"));
        txtUser = new JTextField();
        main.add(txtUser);
        
        main.add(new JLabel("Password:"));
        txtPass = new JPasswordField();
        main.add(txtPass);
        
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> doLogin());
        main.add(btnLogin);
        
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> doClear());
        main.add(btnClear);
        
        add(main, BorderLayout.CENTER);
        
        // Footer part ( M) 
        JPanel footer = new JPanel();
        footer.setBackground(Color.BLACK);
        JLabel lblFooter = new JLabel("© 2026 Group 27 - NexusInventory System");
        lblFooter.setForeground(Color.WHITE);
        footer.add(lblFooter);
        add(footer, BorderLayout.SOUTH);
    }
    
    void doLogin() {
        String username = txtUser.getText();
        String password = new String(txtPass.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }
        
        User user = checkUser(username, password);
        
        if (user != null) {
            dispose();
            if (user.getRole().equals("ADMIN")) {
                AdminDashboard admin = new AdminDashboard(user);
                admin.setVisible(true);
            } else {
                StaffDashboard staff = new StaffDashboard(user);
                staff.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!");
            doClear();
        }
    }
    
    User checkUser(String username, String password) {
        try {
            Connection con = DB.connect();
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setFullName(rs.getString("full_name"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    void doClear() {
        txtUser.setText("");
        txtPass.setText("");
    }
}
