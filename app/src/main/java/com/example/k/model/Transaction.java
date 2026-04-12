package com.example.k.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int productId;
    private String type; // "买入" or "卖出"
    private double shares; // 份额
    private double price; // 单价
    private double amount; // 总金额
    private long timestamp; // 时间戳
    private String productName;

    public Transaction() {}

    @Ignore
    public Transaction(int userId, int productId, String type, double shares, double price, double amount, long timestamp, String productName) {
        this.userId = userId;
        this.productId = productId;
        this.type = type;
        this.shares = shares;
        this.price = price;
        this.amount = amount;
        this.timestamp = timestamp;
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
    public double getShares() { return shares; }
    public void setShares(double shares) { this.shares = shares; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}
