package com.finance.app.model;

import java.io.Serializable;

/**
 * User's holding of a financial product
 */
public class Holding implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String productId;
    private String productName;
    private double quantity;
    private double averageCost;
    private double currentValue;
    
    public Holding() {}
    
    public Holding(String username, String productId, String productName, double quantity, double averageCost) {
        this.username = username;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.averageCost = averageCost;
    }
    
    // Getters and Setters
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
    
    public double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    
    public double getAverageCost() {
        return averageCost;
    }
    
    public void setAverageCost(double averageCost) {
        this.averageCost = averageCost;
    }
    
    public double getCurrentValue() {
        return currentValue;
    }
    
    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }
    
    public double getTotalCost() {
        return quantity * averageCost;
    }
    
    public double getProfitLoss() {
        return currentValue - getTotalCost();
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s: %.2f shares, Avg Cost: ¥%.2f, Current: ¥%.2f, P/L: ¥%.2f",
                productId, productName, quantity, averageCost, currentValue, getProfitLoss());
    }
}
