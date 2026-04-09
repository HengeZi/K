package com.finance.app.repository;

import com.finance.app.model.Transaction;
import com.finance.app.model.Holding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repository for managing transactions and holdings with file-based persistence
 */
public class TransactionRepository {
    private static final String TRANSACTIONS_FILE = "transactions.json";
    private static final String HOLDINGS_FILE = "holdings.json";
    
    private List<Transaction> transactions;
    private Map<String, Map<String, Holding>> holdings; // username -> productId -> Holding
    private Gson gson;
    private AtomicInteger transactionCounter;
    
    public TransactionRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.transactions = new ArrayList<>();
        this.holdings = new HashMap<>();
        this.transactionCounter = new AtomicInteger(1000);
        loadData();
    }
    
    private void loadData() {
        // Load transactions
        File transFile = new File(TRANSACTIONS_FILE);
        if (transFile.exists()) {
            try (Reader reader = new FileReader(transFile)) {
                Type transType = new TypeToken<List<Transaction>>() {}.getType();
                transactions = gson.fromJson(reader, transType);
                if (transactions == null) {
                    transactions = new ArrayList<>();
                } else {
                    // Update counter based on existing transactions
                    for (Transaction t : transactions) {
                        try {
                            int id = Integer.parseInt(t.getTransactionId().replace("TXN", ""));
                            if (id >= transactionCounter.get()) {
                                transactionCounter.set(id + 1);
                            }
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading transaction data: " + e.getMessage());
                transactions = new ArrayList<>();
            }
        }
        
        // Load holdings
        File holdFile = new File(HOLDINGS_FILE);
        if (holdFile.exists()) {
            try (Reader reader = new FileReader(holdFile)) {
                Type holdType = new TypeToken<Map<String, Map<String, Holding>>>() {}.getType();
                holdings = gson.fromJson(reader, holdType);
                if (holdings == null) {
                    holdings = new HashMap<>();
                }
            } catch (IOException e) {
                System.err.println("Error loading holdings data: " + e.getMessage());
                holdings = new HashMap<>();
            }
        }
    }
    
    private void saveTransactions() {
        try (Writer writer = new FileWriter(TRANSACTIONS_FILE)) {
            gson.toJson(transactions, writer);
        } catch (IOException e) {
            System.err.println("Error saving transaction data: " + e.getMessage());
        }
    }
    
    private void saveHoldings() {
        try (Writer writer = new FileWriter(HOLDINGS_FILE)) {
            gson.toJson(holdings, writer);
        } catch (IOException e) {
            System.err.println("Error saving holdings data: " + e.getMessage());
        }
    }
    
    public synchronized Transaction buyProduct(String username, String productId, String productName, 
                                                double quantity, double unitPrice) {
        String transactionId = "TXN" + transactionCounter.getAndIncrement();
        Transaction transaction = new Transaction(transactionId, username, productId, productName,
                                                   "BUY", quantity, unitPrice);
        transactions.add(transaction);
        saveTransactions();
        
        // Update holdings
        updateHolding(username, productId, productName, quantity, unitPrice, true);
        
        return transaction;
    }
    
    public synchronized Transaction sellProduct(String username, String productId, String productName,
                                                 double quantity, double unitPrice) {
        // Check if user has enough holdings
        Holding holding = getHolding(username, productId);
        if (holding == null || holding.getQuantity() < quantity) {
            return null; // Insufficient holdings
        }
        
        String transactionId = "TXN" + transactionCounter.getAndIncrement();
        Transaction transaction = new Transaction(transactionId, username, productId, productName,
                                                   "SELL", quantity, unitPrice);
        transactions.add(transaction);
        saveTransactions();
        
        // Update holdings
        updateHolding(username, productId, productName, quantity, unitPrice, false);
        
        return transaction;
    }
    
    private void updateHolding(String username, String productId, String productName,
                               double quantity, double price, boolean isBuy) {
        holdings.putIfAbsent(username, new HashMap<>());
        Map<String, Holding> userHoldings = holdings.get(username);
        
        if (isBuy) {
            Holding holding = userHoldings.get(productId);
            if (holding == null) {
                holding = new Holding(username, productId, productName, quantity, price);
            } else {
                // Calculate new average cost
                double totalCost = holding.getTotalCost() + (quantity * price);
                double newQuantity = holding.getQuantity() + quantity;
                double newAverageCost = totalCost / newQuantity;
                holding.setQuantity(newQuantity);
                holding.setAverageCost(newAverageCost);
            }
            holding.setCurrentValue(holding.getQuantity() * price);
            userHoldings.put(productId, holding);
        } else {
            Holding holding = userHoldings.get(productId);
            if (holding != null) {
                double newQuantity = holding.getQuantity() - quantity;
                if (newQuantity <= 0) {
                    userHoldings.remove(productId);
                } else {
                    holding.setQuantity(newQuantity);
                    holding.setCurrentValue(newQuantity * price);
                    userHoldings.put(productId, holding);
                }
            }
        }
        saveHoldings();
    }
    
    public List<Transaction> getUserTransactions(String username) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getUsername().equals(username)) {
                result.add(t);
            }
        }
        // Sort by date descending
        result.sort((a, b) -> b.getTransactionDate().compareTo(a.getTransactionDate()));
        return result;
    }
    
    public List<Holding> getUserHoldings(String username) {
        List<Holding> result = new ArrayList<>();
        Map<String, Holding> userHoldings = holdings.get(username);
        if (userHoldings != null) {
            result.addAll(userHoldings.values());
        }
        return result;
    }
    
    public Holding getHolding(String username, String productId) {
        Map<String, Holding> userHoldings = holdings.get(username);
        if (userHoldings != null) {
            return userHoldings.get(productId);
        }
        return null;
    }
    
    public double getTotalHoldingsValue(String username) {
        List<Holding> userHoldings = getUserHoldings(username);
        double total = 0;
        for (Holding h : userHoldings) {
            total += h.getCurrentValue();
        }
        return total;
    }
}
