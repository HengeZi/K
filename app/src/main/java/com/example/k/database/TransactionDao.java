package com.example.k.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.k.model.Transaction;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    long insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    List<Transaction> getUserTransactions(int userId);

    @Query("SELECT * FROM transactions WHERE userId = :userId AND productId = :productId ORDER BY timestamp DESC")
    List<Transaction> getProductTransactions(int userId, int productId);

    @Query("SELECT * FROM transactions WHERE id = :id")
    Transaction getTransactionById(int id);
}
