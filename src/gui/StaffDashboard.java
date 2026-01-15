package gui;

import database.DB;
import models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class StaffDashboard extends JFrame {
    User user;
    JTable tblProduct;
    JTable tblCart;
    DefaultTableModel mdlProduct;
    DefaultTableModel mdlCart;
    JTextField txtSearch;
    JList<String> listSearch;
    DefaultListModel<String> mdlSearch;
    JLabel lblTotal;
    ArrayList<CartItem> cart;
    ArrayList<ProductData> products;
    
    public StaffDashboard(User u) {
        user = u;
        cart = new ArrayList<>();
        products = new ArrayList<>();
        setTitle("Staff Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setupUI();
        loadProducts();
    }
    
    void setupUI() {
        setLayout(new BorderLayout());
        
        // Header (M) 
        JPanel header = new JPanel();
        header.setBackground(Color.BLACK);
        JLabel lblHeader = new JLabel("Staff: " + user.getFullName());
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(lblHeader);
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> doLogout());
        header.add(btnLogout);
        add(header, BorderLayout.NORTH);
        
        // Main panel with two parts (M) 
        JPanel main = new JPanel();
        main.setLayout(new GridLayout(1, 2, 10, 10));
        
        // Left side - Products (M) 
        JPanel left = new JPanel();
        left.setLayout(new BorderLayout(5, 5));
        
        // Search panel (M) 
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Products"));
        
        JPanel searchTop = new JPanel();
        searchTop.setLayout(new FlowLayout(FlowLayout.LEFT));
        searchTop.add(new JLabel("Search:"));
        txtSearch = new JTextField(20);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                doSearch();
            }
        });
        searchTop.add(txtSearch);
        
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearSearch());
        searchTop.add(btnClear);
        searchPanel.add(searchTop, BorderLayout.NORTH);
        
        mdlSearch = new DefaultListModel<>();
        listSearch = new JList<>(mdlSearch);
        listSearch.setVisibleRowCount(5);
        listSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addFromSearch();
                }
            }
        });
        searchPanel.add(new JScrollPane(listSearch), BorderLayout.CENTER);
        
        JButton btnAddSearch = new JButton("Add to Cart");
        btnAddSearch.addActionListener(e -> addFromSearch());
        searchPanel.add(btnAddSearch, BorderLayout.SOUTH);
        
        left.add(searchPanel, BorderLayout.NORTH);
        
        // Product table with list (M)
        String[] cols1 = {"Code", "Name", "Stock", "Price"};
        mdlProduct = new DefaultTableModel(cols1, 0);
        tblProduct = new JTable(mdlProduct);
        left.add(new JScrollPane(tblProduct), BorderLayout.CENTER);
        
        JButton btnAdd = new JButton("Add to Cart");
        btnAdd.addActionListener(e -> addToCart());
        left.add(btnAdd, BorderLayout.SOUTH);
        
        // Right side - Cart panel (M) 
        JPanel right = new JPanel();
        right.setLayout(new BorderLayout(5, 5));
        
        String[] cols2 = {"Product", "Price", "Qty", "Total"};
        mdlCart = new DefaultTableModel(cols2, 0);
        tblCart = new JTable(mdlCart);
        right.add(new JScrollPane(tblCart), BorderLayout.CENTER);
        
        JPanel cartBtns = new JPanel();
        JButton btnRemove = new JButton("Remove");
        btnRemove.addActionListener(e -> removeFromCart());
        cartBtns.add(btnRemove);
        
        JButton btnClearCart = new JButton("Clear Cart");
        btnClearCart.addActionListener(e -> clearCart());
        cartBtns.add(btnClearCart);
        right.add(cartBtns, BorderLayout.SOUTH);
        
        main.add(left);
        main.add(right);
        add(main, BorderLayout.CENTER);
        
        // Bottom panel (M) 
        lblTotal = new JLabel("Total: Rs 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 20));
        
        JButton btnSale = new JButton("Complete Sale");
        btnSale.addActionListener(e -> completeSale());
        
        JPanel bottom = new JPanel();
        bottom.add(lblTotal);
        bottom.add(btnSale);
        
        // Footer (M) 
        JPanel footer = new JPanel();
        footer.setBackground(Color.BLACK);
        JLabel lblFooter = new JLabel("© 2026 Group 27 - NexusInventory System");
        lblFooter.setForeground(Color.WHITE);
        footer.add(lblFooter);
        
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        southPanel.add(bottom, BorderLayout.NORTH);
        southPanel.add(footer, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    void loadProducts() {
        mdlProduct.setRowCount(0);
        products.clear();
        
        try {
            Connection con = DB.connect();
            String sql = "SELECT * FROM products WHERE quantity > 0";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                String code = rs.getString("product_code");
                String name = rs.getString("product_name");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("unit_price");
                
                Object[] row = {code, name, qty, price};
                mdlProduct.addRow(row);
                
                ProductData p = new ProductData();
                p.code = code;
                p.name = name;
                p.qty = qty;
                p.price = price;
                products.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void doSearch() {
        String text = txtSearch.getText();
        text = text.toLowerCase();
        mdlSearch.clear();
        
        if (text.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < products.size(); i++) {
            ProductData p = products.get(i);
            String name = p.name.toLowerCase();
            String code = p.code.toLowerCase();
            
            if (name.contains(text) || code.contains(text)) {
                String display = p.name + " [" + p.code + "] - Rs." + p.price + " (Stock: " + p.qty + ")";
                mdlSearch.addElement(display);
            }
        }
        
        if (mdlSearch.isEmpty()) {
            mdlSearch.addElement("No results");
        }
    }
    
    void clearSearch() {
        txtSearch.setText("");
        mdlSearch.clear();
    }
    
    void addFromSearch() {
        int idx = listSearch.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product!");
            return;
        }
        
        String item = mdlSearch.getElementAt(idx);
        if (item.equals("No results")) {
            JOptionPane.showMessageDialog(this, "No product selected!");
            return;
        }
        
        String text = txtSearch.getText();
        text = text.toLowerCase();
        ProductData selected = null;
        int count = 0;
        
        for (int i = 0; i < products.size(); i++) {
            ProductData p = products.get(i);
            String name = p.name.toLowerCase();
            String code = p.code.toLowerCase();
            
            if (name.contains(text) || code.contains(text)) {
                if (count == idx) {
                    selected = p;
                    break;
                }
                count++;
            }
        }
        
        if (selected == null) {
            return;
        }
        
        String input = JOptionPane.showInputDialog("Enter quantity:");
        if (input == null) {
            return;
        }
        
        try {
            int qty = Integer.parseInt(input);
            
            if (qty <= 0 || qty > selected.qty) {
                JOptionPane.showMessageDialog(this, "Invalid quantity!");
                return;
            }
            
            boolean found = false;
            for (int i = 0; i < cart.size(); i++) {
                CartItem c = cart.get(i);
                if (c.code.equals(selected.code)) {
                    c.qty = c.qty + qty;
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                CartItem c = new CartItem();
                c.code = selected.code;
                c.name = selected.name;
                c.price = selected.price;
                c.qty = qty;
                cart.add(c);
            }
            
            updateCart();
            clearSearch();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter valid number!");
        }
    }
    
    void addToCart() {
        int row = tblProduct.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product!");
            return;
        }
        
        String code = mdlProduct.getValueAt(row, 0).toString();
        String name = mdlProduct.getValueAt(row, 1).toString();
        int stock = (int) mdlProduct.getValueAt(row, 2);
        double price = (double) mdlProduct.getValueAt(row, 3);
        
        String input = JOptionPane.showInputDialog("Enter quantity:");
        if (input == null) {
            return;
        }
        
        try {
            int qty = Integer.parseInt(input);
            
            if (qty <= 0 || qty > stock) {
                JOptionPane.showMessageDialog(this, "Invalid quantity!");
                return;
            }
            
            boolean found = false;
            for (int i = 0; i < cart.size(); i++) {
                CartItem c = cart.get(i);
                if (c.code.equals(code)) {
                    c.qty = c.qty + qty;
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                CartItem c = new CartItem();
                c.code = code;
                c.name = name;
                c.price = price;
                c.qty = qty;
                cart.add(c);
            }
            
            updateCart();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter valid number!");
        }
    }
    
    void removeFromCart() {
        int row = tblCart.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item!");
            return;
        }
        cart.remove(row);
        updateCart();
    }
    
    void clearCart() {
        cart.clear();
        updateCart();
    }
    
    void updateCart() {
        mdlCart.setRowCount(0);
        double total = 0;
        
        for (int i = 0; i < cart.size(); i++) {
            CartItem c = cart.get(i);
            double itemTotal = c.price * c.qty;
            total = total + itemTotal;
            
            Object[] row = {c.name, c.price, c.qty, itemTotal};
            mdlCart.addRow(row);
        }
        
        lblTotal.setText("Total: Rs " + String.format("%.2f", total));
    }
    
    void completeSale() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }
        
        try {
            Connection con = DB.connect();
            
            for (int i = 0; i < cart.size(); i++) {
                CartItem c = cart.get(i);
                
                // Get product ID logic
                String sql1 = "SELECT id FROM products WHERE product_code = ?";
                PreparedStatement ps1 = con.prepareStatement(sql1);
                ps1.setString(1, c.code);
                ResultSet rs = ps1.executeQuery();
                
                int productId = 0;
                if (rs.next()) {
                    productId = rs.getInt("id");
                }
                
                // Save sale logic
                String sql2 = "INSERT INTO sales (product_id, product_name, quantity_sold, unit_price, total_amount, sold_by) VALUES (?,?,?,?,?,?)";
                PreparedStatement ps2 = con.prepareStatement(sql2);
                ps2.setInt(1, productId);
                ps2.setString(2, c.name);
                ps2.setInt(3, c.qty);
                ps2.setDouble(4, c.price);
                ps2.setDouble(5, c.price * c.qty);
                ps2.setString(6, user.getUsername());
                ps2.executeUpdate();
                
                // Update stock logic
                String sql3 = "UPDATE products SET quantity = quantity - ? WHERE product_code = ?";
                PreparedStatement ps3 = con.prepareStatement(sql3);
                ps3.setInt(1, c.qty);
                ps3.setString(2, c.code);
                ps3.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(this, "Sale completed successfully!");
            cart.clear();
            updateCart();
            loadProducts();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    void doLogout() {
        dispose();
        LoginFrame login = new LoginFrame();
        login.setVisible(true);
    }
    
    class CartItem {
        String code;
        String name;
        int qty;
        double price;
    }
    
    class ProductData {
        String code;
        String name;
        int qty;
        double price;
    }
}

