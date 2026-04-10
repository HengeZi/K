package com.example.k.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String type;
    private double price;
    private double expectedReturn;
    private String riskLevel;
    private String description;

    public Product(String name, String type, double price, double expectedReturn, String riskLevel, String description) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.expectedReturn = expectedReturn;
        this.riskLevel = riskLevel;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getExpectedReturn() { return expectedReturn; }
    public void setExpectedReturn(double expectedReturn) { this.expectedReturn = expectedReturn; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
