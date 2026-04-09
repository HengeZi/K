package com.finance.app.model;

import java.io.Serializable;

/**
 * Transaction record for buying or selling financial products
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String transactionId;
    private String username;
    private String productId;
    private String productName;
    private String type; // BUY or SELL
    private double quantity;
    private double unitPrice;
    private double totalAmount;
    private String transactionDate; // Store as formatted string for JSON compatibility
    
    public Transaction() {}
    
    public Transaction(String transactionId, String username, String productId, String productName,
                      String type, double quantity, double unitPrice) {
        this.transactionId = transactionId;
        this.username = username;
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = quantity * unitPrice;
        this.transactionDate = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public String getFormattedDate() {
        return transactionDate != null ? transactionDate : "";
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s %s x %.2f @ ¥%.2f = ¥%.2f",
                getFormattedDate(), type, productId, productName, quantity, unitPrice, totalAmount);
    }
}
