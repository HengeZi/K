package com.example.k.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_products")
public class UserProduct {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int productId;
    private double amount;
    private double buyPrice;
    private long buyDate;
    private String productName;

    public UserProduct(int userId, int productId, double amount, double buyPrice, long buyDate, String productName) {
        this.userId = userId;
        this.productId = productId;
        this.amount = amount;
        this.buyPrice = buyPrice;
        this.buyDate = buyDate;
        this.productName = productName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }
    public long getBuyDate() { return buyDate; }
    public void setBuyDate(long buyDate) { this.buyDate = buyDate; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}
