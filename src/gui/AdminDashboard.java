package gui;

import database.DB;
import models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    User user;
    JTable tblProduct;
    JTable tblSales;
    JTable tblFastMoving;
    JTable tblLowStock;
    DefaultTableModel mdlProduct;
    DefaultTableModel mdlSales;
    DefaultTableModel mdlFastMoving;
    DefaultTableModel mdlLowStock;
    JTextField txtName;
    JTextField txtQty;
    JTextField txtPrice;
    JComboBox<String> cmbCategory;
    JLabel lblLowStockCount;
    JLabel lblDailySales;
    JLabel lblTotalItems;
    JLabel lblTotalRevenue;
    
    public AdminDashboard(User u) {
        user = u;
        setTitle("Admin Dashboard");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setupUI();
        loadData();
    }
    
    void setupUI() {
        setLayout(new BorderLayout());
        
        // Header part (M)
        JPanel header = new JPanel();
        header.setBackground(Color.BLACK);
        JLabel lblHeader = new JLabel("Admin: " + user.getFullName());
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(lblHeader);
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> doLogout());
        header.add(btnLogout);
        add(header, BorderLayout.NORTH);
        
        // Tabs part (M)
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Products", createProductPanel());
        tabs.addTab("Sales", createSalesPanel());
        add(tabs, BorderLayout.CENTER);
        
        // Footer part (M)
        JPanel footer = new JPanel();
        footer.setBackground(Color.BLACK);
        JLabel lblFooter = new JLabel("© 2026 Group 27 - NexusInventory System");
        lblFooter.setForeground(Color.WHITE);
        footer.add(lblFooter);
        add(footer, BorderLayout.SOUTH);
    }
    
    JPanel createProductPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        
        // Alert panel (M)
        JPanel alert = new JPanel();
        alert.setBackground(new Color(255, 240, 240));
        alert.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        lblLowStockCount = new JLabel("⚠ Low Stock: 0");
        lblLowStockCount.setFont(new Font("Arial", Font.BOLD, 16));
        lblLowStockCount.setForeground(Color.RED);
        alert.add(lblLowStockCount);
        panel.add(alert, BorderLayout.NORTH);
        
        // Center panel (M)
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout(5, 5));
        
        // Form (M)
        JPanel form = new JPanel();
        form.setLayout(new GridLayout(3, 3, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        form.add(new JLabel("Name:"));
        txtName = new JTextField();
        form.add(txtName);
        
        form.add(new JLabel("Category:"));
        String[] cats = {"Electronics", "Accessories", "Storage", "Networking", "Software"};
        cmbCategory = new JComboBox<>(cats);
        form.add(cmbCategory);
        
        form.add(new JLabel("Quantity:"));
        txtQty = new JTextField();
        form.add(txtQty);
        
        form.add(new JLabel("Price:"));
        txtPrice = new JTextField();
        form.add(txtPrice);
        
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> addProduct());
        form.add(btnAdd);
        
        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(e -> updateProduct());
        form.add(btnUpdate);
        
        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(e -> deleteProduct());
        form.add(btnDelete);
        
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearForm());
        form.add(btnClear);
        
        center.add(form, BorderLayout.NORTH);
        
        // Table with list (M) 
        String[] cols = {"Code", "Name", "Category", "Qty", "Price", "Status"};
        mdlProduct = new DefaultTableModel(cols, 0);
        tblProduct = new JTable(mdlProduct);
        tblProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectRow();
            }
        });
        center.add(new JScrollPane(tblProduct), BorderLayout.CENTER);
        
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }
    
    JPanel createSalesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel summary = new JPanel();
        summary.setLayout(new GridLayout(2, 3, 10, 10));
        summary.setBorder(BorderFactory.createTitledBorder("Today's Summary"));
        
        // Daily Sales Box part (M) 
        JPanel box1 = new JPanel();
        box1.setLayout(new BorderLayout());
        box1.setBorder(BorderFactory.createLineBorder(new Color(33, 150, 243), 2));
        box1.setBackground(new Color(230, 240, 255));
        JLabel lbl1 = new JLabel("Daily Sales", JLabel.CENTER);
        lbl1.setFont(new Font("Arial", Font.BOLD, 12));
        box1.add(lbl1, BorderLayout.NORTH);
        lblDailySales = new JLabel("Rs 0.00", JLabel.CENTER);
        lblDailySales.setFont(new Font("Arial", Font.BOLD, 20));
        lblDailySales.setForeground(new Color(0, 100, 200));
        box1.add(lblDailySales, BorderLayout.CENTER);
        summary.add(box1);
        
        // Items Sold Box part (M) 
        JPanel box2 = new JPanel();
        box2.setLayout(new BorderLayout());
        box2.setBorder(BorderFactory.createLineBorder(new Color(76, 175, 80), 2));
        box2.setBackground(new Color(230, 255, 230));
        JLabel lbl2 = new JLabel("Items Sold", JLabel.CENTER);
        lbl2.setFont(new Font("Arial", Font.BOLD, 12));
        box2.add(lbl2, BorderLayout.NORTH);
        lblTotalItems = new JLabel("0", JLabel.CENTER);
        lblTotalItems.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalItems.setForeground(new Color(0, 150, 0));
        box2.add(lblTotalItems, BorderLayout.CENTER);
        summary.add(box2);
        
        // Revenue Box part (M) 
        JPanel box3 = new JPanel();
        box3.setLayout(new BorderLayout());
        box3.setBorder(BorderFactory.createLineBorder(new Color(255, 152, 0), 2));
        box3.setBackground(new Color(255, 245, 230));
        JLabel lbl3 = new JLabel("Total Revenue", JLabel.CENTER);
        lbl3.setFont(new Font("Arial", Font.BOLD, 12));
        box3.add(lbl3, BorderLayout.NORTH);
        lblTotalRevenue = new JLabel("Rs 0.00", JLabel.CENTER);
        lblTotalRevenue.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalRevenue.setForeground(new Color(200, 100, 0));
        box3.add(lblTotalRevenue, BorderLayout.CENTER);
        summary.add(box3);
        
        // Fast Moving Table
        JPanel pFast = new JPanel();
        pFast.setLayout(new BorderLayout());
        pFast.setBorder(BorderFactory.createTitledBorder("Fast Moving"));
        String[] cols1 = {"Product", "Sold", "Revenue"};
        mdlFastMoving = new DefaultTableModel(cols1, 0);
        tblFastMoving = new JTable(mdlFastMoving);
        JScrollPane sp1 = new JScrollPane(tblFastMoving);
        sp1.setPreferredSize(new Dimension(0, 100));
        pFast.add(sp1, BorderLayout.CENTER);
        summary.add(pFast);
        
        // Low Stock Table part (M)
        JPanel pLow = new JPanel();
        pLow.setLayout(new BorderLayout());
        pLow.setBorder(BorderFactory.createTitledBorder("⚠ Low Stock"));
        pLow.setBackground(new Color(255, 240, 240));
        String[] cols2 = {"Code", "Product", "Stock"};
        mdlLowStock = new DefaultTableModel(cols2, 0);
        tblLowStock = new JTable(mdlLowStock);
        tblLowStock.setBackground(new Color(255, 250, 250));
        JScrollPane sp2 = new JScrollPane(tblLowStock);
        sp2.setPreferredSize(new Dimension(0, 100));
        pLow.add(sp2, BorderLayout.CENTER);
        summary.add(pLow);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadData());
        summary.add(btnRefresh);
        
        panel.add(summary, BorderLayout.NORTH);
        
        // Transactions Table with logic 
        JPanel trans = new JPanel();
        trans.setLayout(new BorderLayout());
        trans.setBorder(BorderFactory.createTitledBorder("Transactions"));
        String[] cols3 = {"ID", "Product", "Qty", "Price", "Total", "Sold By", "Date"};
        mdlSales = new DefaultTableModel(cols3, 0);
        tblSales = new JTable(mdlSales);
        trans.add(new JScrollPane(tblSales), BorderLayout.CENTER);
        panel.add(trans, BorderLayout.CENTER);
        
        return panel;
    }
    
    void loadData() {
        loadProducts();
        loadSales();
        loadDailySummary();
        loadFastMoving();
        loadLowStock();
    }
    
    void loadProducts() {
        mdlProduct.setRowCount(0);
        int lowCount = 0;
        
        try {
            Connection con = DB.connect();
            String sql = "SELECT * FROM products ORDER BY quantity ASC";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                String code = rs.getString("product_code");
                String name = rs.getString("product_name");
                String cat = rs.getString("category");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("unit_price");
                
                String status = "✓ OK";
                if (qty <= 10) {
                    status = "⚠ LOW";
                    lowCount++;
                }
                
                Object[] row = {code, name, cat, qty, price, status};
                mdlProduct.addRow(row);
            }
            
            lblLowStockCount.setText("⚠ Low Stock: " + lowCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void loadSales() {
        mdlSales.setRowCount(0);
        try {
            Connection con = DB.connect();
            String sql = "SELECT * FROM sales ORDER BY sale_date DESC LIMIT 50";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("product_name"),
                    rs.getInt("quantity_sold"),
                    rs.getDouble("unit_price"),
                    rs.getDouble("total_amount"),
                    rs.getString("sold_by"),
                    rs.getTimestamp("sale_date")
                };
                mdlSales.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void loadDailySummary() {
        try {
            Connection con = DB.connect();
            String sql = "SELECT COUNT(*) as items, SUM(total_amount) as revenue FROM sales WHERE DATE(sale_date) = CURDATE()";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                int items = rs.getInt("items");
                double revenue = rs.getDouble("revenue");
                
                lblTotalItems.setText(String.valueOf(items));
                lblDailySales.setText("Rs " + String.format("%.2f", revenue));
                lblTotalRevenue.setText("Rs " + String.format("%.2f", revenue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void loadFastMoving() {
        mdlFastMoving.setRowCount(0);
        try {
            Connection con = DB.connect();
            String sql = "SELECT product_name, SUM(quantity_sold) as total, SUM(total_amount) as revenue FROM sales GROUP BY product_name ORDER BY total DESC LIMIT 5";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("product_name"),
                    rs.getInt("total"),
                    "Rs " + String.format("%.2f", rs.getDouble("revenue"))
                };
                mdlFastMoving.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void loadLowStock() {
        mdlLowStock.setRowCount(0);
        try {
            Connection con = DB.connect();
            String sql = "SELECT product_code, product_name, quantity FROM products WHERE quantity <= 10 ORDER BY quantity ASC";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("product_code"),
                    rs.getString("product_name"),
                    rs.getInt("quantity")
                };
                mdlLowStock.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void addProduct() {
        try {
            String name = txtName.getText();
            String cat = (String) cmbCategory.getSelectedItem();
            String qtyText = txtQty.getText();
            String priceText = txtPrice.getText();
            
            if (name.isEmpty() || qtyText.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }
            
            int qty = Integer.parseInt(qtyText);
            double price = Double.parseDouble(priceText);
            String code = generateCode();
            
            Connection con = DB.connect();
            String sql = "INSERT INTO products (product_code, product_name, category, quantity, unit_price, reorder_level) VALUES (?,?,?,?,?,10)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, cat);
            ps.setInt(4, qty);
            ps.setDouble(5, price);
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Product added!\nCode: " + code);
            clearForm();
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    String generateCode() {
        try {
            Connection con = DB.connect();
            String sql = "SELECT MAX(id) as maxid FROM products";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt("maxid") + 1;
            }
            
            return "PRD" + String.format("%04d", nextId);
        } catch (Exception e) {
            return "PRD0001";
        }
    }
    
    void updateProduct() {
        int row = tblProduct.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product!");
            return;
        }
        
        try {
            String code = mdlProduct.getValueAt(row, 0).toString();
            String name = txtName.getText();
            String cat = (String) cmbCategory.getSelectedItem();
            int qty = Integer.parseInt(txtQty.getText());
            double price = Double.parseDouble(txtPrice.getText());
            
            Connection con = DB.connect();
            String sql = "UPDATE products SET product_name=?, category=?, quantity=?, unit_price=? WHERE product_code=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, cat);
            ps.setInt(3, qty);
            ps.setDouble(4, price);
            ps.setString(5, code);
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Product updated!");
            clearForm();
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    void deleteProduct() {
        int row = tblProduct.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this product?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String code = mdlProduct.getValueAt(row, 0).toString();
                
                Connection con = DB.connect();
                String sql = "DELETE FROM products WHERE product_code=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, code);
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Product deleted!");
                clearForm();
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    void selectRow() {
        int row = tblProduct.getSelectedRow();
        if (row != -1) {
            txtName.setText(mdlProduct.getValueAt(row, 1).toString());
            String cat = mdlProduct.getValueAt(row, 2).toString();
            cmbCategory.setSelectedItem(cat);
            txtQty.setText(mdlProduct.getValueAt(row, 3).toString());
            txtPrice.setText(mdlProduct.getValueAt(row, 4).toString());
        }
    }
    
    void clearForm() {
        txtName.setText("");
        cmbCategory.setSelectedIndex(0);
        txtQty.setText("");
        txtPrice.setText("");
    }
    
    void doLogout() {
        dispose();
        LoginFrame login = new LoginFrame();
        login.setVisible(true);
    }
}
