package com.finance.app.repository;

import com.finance.app.model.FinancialProduct;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Repository for managing financial products with file-based persistence
 */
public class ProductRepository {
    private static final String DATA_FILE = "products.json";
    private Map<String, FinancialProduct> products;
    private Gson gson;
    
    public ProductRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.products = new LinkedHashMap<>();
        loadData();
        
        // Initialize with sample products if empty
        if (products.isEmpty()) {
            initializeSampleProducts();
        }
    }
    
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type productType = new TypeToken<Map<String, FinancialProduct>>() {}.getType();
                products = gson.fromJson(reader, productType);
                if (products == null) {
                    products = new LinkedHashMap<>();
                }
            } catch (IOException e) {
                System.err.println("Error loading product data: " + e.getMessage());
                products = new LinkedHashMap<>();
            }
        }
    }
    
    private void saveData() {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            gson.toJson(products, writer);
        } catch (IOException e) {
            System.err.println("Error saving product data: " + e.getMessage());
        }
    }
    
    private void initializeSampleProducts() {
        products.put("FP001", new FinancialProduct("FP001", "稳健增长基金", "Fund", 1.523, 5.8, "Low"));
        products.put("FP002", new FinancialProduct("FP002", "科技创新股票", "Stock", 25.67, 12.5, "High"));
        products.put("FP003", new FinancialProduct("FP003", "国债逆回购", "Bond", 100.0, 3.2, "Low"));
        products.put("FP004", new FinancialProduct("FP004", "平衡混合基金", "Fund", 2.341, 7.3, "Medium"));
        products.put("FP005", new FinancialProduct("FP005", "医疗健康保险", "Insurance", 500.0, 4.5, "Low"));
        products.put("FP006", new FinancialProduct("FP006", "新能源ETF", "Fund", 1.876, 9.8, "Medium"));
        products.put("FP007", new FinancialProduct("FP007", "蓝筹股组合", "Stock", 45.32, 8.6, "Medium"));
        products.put("FP008", new FinancialProduct("FP008", "货币基金", "Fund", 1.0, 2.5, "Low"));
        saveData();
    }
    
    public List<FinancialProduct> getAllProducts() {
        return new ArrayList<>(products.values());
    }
    
    public FinancialProduct getProductById(String productId) {
        return products.get(productId);
    }
    
    public List<FinancialProduct> getProductsByType(String type) {
        List<FinancialProduct> result = new ArrayList<>();
        for (FinancialProduct product : products.values()) {
            if (product.getProductType().equalsIgnoreCase(type)) {
                result.add(product);
            }
        }
        return result;
    }
    
    public List<FinancialProduct> searchProducts(String keyword) {
        List<FinancialProduct> result = new ArrayList<>();
        for (FinancialProduct product : products.values()) {
            if (product.getProductName().contains(keyword) || 
                product.getProductId().contains(keyword) ||
                product.getProductType().contains(keyword)) {
                result.add(product);
            }
        }
        return result;
    }
}
