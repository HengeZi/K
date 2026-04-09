package com.finance.app.service;

import com.finance.app.model.*;
import com.finance.app.repository.*;

import java.util.List;

/**
 * Service layer for handling business logic
 */
public class FinanceService {
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private TransactionRepository transactionRepository;
    
    public FinanceService() {
        this.userRepository = new UserRepository();
        this.productRepository = new ProductRepository();
        this.transactionRepository = new TransactionRepository();
    }
    
    // User Management
    public boolean register(String username, String password, String fullName) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.length() < 6) {
            return false; // Password must be at least 6 characters
        }
        User user = new User(username, password, fullName);
        return userRepository.register(user);
    }
    
    public User login(String username, String password) {
        return userRepository.login(username, password);
    }
    
    public boolean updateUser(User user) {
        return userRepository.updateUser(user);
    }
    
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            return false;
        }
        return userRepository.changePassword(username, oldPassword, newPassword);
    }
    
    public User getUser(String username) {
        return userRepository.getUser(username);
    }
    
    // Product Management
    public List<FinancialProduct> getAllProducts() {
        return productRepository.getAllProducts();
    }
    
    public FinancialProduct getProductById(String productId) {
        return productRepository.getProductById(productId);
    }
    
    public List<FinancialProduct> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }
    
    // Transaction Management
    public Transaction buyProduct(String username, String productId, double quantity) {
        FinancialProduct product = productRepository.getProductById(productId);
        if (product == null) {
            return null;
        }
        if (quantity <= 0) {
            return null;
        }
        return transactionRepository.buyProduct(username, productId, product.getProductName(),
                                                 quantity, product.getUnitPrice());
    }
    
    public Transaction sellProduct(String username, String productId, double quantity) {
        FinancialProduct product = productRepository.getProductById(productId);
        if (product == null) {
            return null;
        }
        if (quantity <= 0) {
            return null;
        }
        return transactionRepository.sellProduct(username, productId, product.getProductName(),
                                                  quantity, product.getUnitPrice());
    }
    
    public List<Transaction> getUserTransactions(String username) {
        return transactionRepository.getUserTransactions(username);
    }
    
    public List<Holding> getUserHoldings(String username) {
        return transactionRepository.getUserHoldings(username);
    }
    
    public double getTotalHoldingsValue(String username) {
        return transactionRepository.getTotalHoldingsValue(username);
    }
}
