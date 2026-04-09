package com.finance.app.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Financial Product model class
 */
public class FinancialProduct implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String productId;
    private String productName;
    private String productType; // e.g., Fund, Stock, Bond, Insurance
    private double unitPrice;
    private double expectedReturnRate;
    private String riskLevel; // Low, Medium, High
    private String description;
    
    public FinancialProduct() {}
    
    public FinancialProduct(String productId, String productName, String productType, 
                           double unitPrice, double expectedReturnRate, String riskLevel) {
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.unitPrice = unitPrice;
        this.expectedReturnRate = expectedReturnRate;
        this.riskLevel = riskLevel;
    }
    
    // Getters and Setters
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
    
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public double getExpectedReturnRate() {
        return expectedReturnRate;
    }
    
    public void setExpectedReturnRate(double expectedReturnRate) {
        this.expectedReturnRate = expectedReturnRate;
    }
    
    public String getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s) - ¥%.2f - Expected: %.2f%% - Risk: %s",
                productId, productName, productType, unitPrice, expectedReturnRate, riskLevel);
    }
}
