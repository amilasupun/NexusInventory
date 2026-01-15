package models;

import java.sql.Timestamp;

public class Sale {
    int id;
    int productId;
    String productName;
    int qtySold;
    double unitPrice;
    double total;
    String soldBy;
    Timestamp date;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public int getQtySold() {
        return qtySold;
    }
    
    public void setQtySold(int qtySold) {
        this.qtySold = qtySold;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public String getSoldBy() {
        return soldBy;
    }
    
    public void setSoldBy(String soldBy) {
        this.soldBy = soldBy;
    }
    
    public Timestamp getDate() {
        return date;
    }
    
    public void setDate(Timestamp date) {
        this.date = date;
    }
}
