package com.example.k.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int productId;
    private String type; // "BUY" or "SELL"
    private double amount;
    private double price;
    private long date;
    private String productName;

    public Transaction(int userId, int productId, String type, double amount, double price, long date, String productName) {
        this.userId = userId;
        this.productId = productId;
        this.type = type;
        this.amount = amount;
        this.price = price;
        this.date = date;
        this.productName = productName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}
