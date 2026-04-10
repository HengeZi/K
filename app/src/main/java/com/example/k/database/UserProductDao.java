package com.example.k.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.k.model.UserProduct;
import java.util.List;

@Dao
public interface UserProductDao {
    @Insert
    long insert(UserProduct userProduct);

    @Update
    void update(UserProduct userProduct);

    @Delete
    void delete(UserProduct userProduct);

    @Query("SELECT * FROM user_products WHERE userId = :userId")
    List<UserProduct> getUserProducts(int userId);

    @Query("SELECT * FROM user_products WHERE userId = :userId AND productId = :productId")
    UserProduct getUserProduct(int userId, int productId);

    @Query("DELETE FROM user_products WHERE id = :id")
    void deleteUserProduct(int id);

    @Query("SELECT SUM(amount) FROM user_products WHERE userId = :userId AND productId = :productId")
    Double getTotalAmount(int userId, int productId);
}
