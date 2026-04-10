package com.example.k.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.k.model.User;
import com.example.k.model.Product;
import com.example.k.model.UserProduct;
import com.example.k.model.Transaction;

@Database(entities = {User.class, Product.class, UserProduct.class, Transaction.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract UserProductDao userProductDao();
    public abstract TransactionDao transactionDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "wealth_manager_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
