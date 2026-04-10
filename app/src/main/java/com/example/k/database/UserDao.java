package com.example.k.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.k.model.User;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(int userId);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User validateUser(String username, String password);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    boolean isUsernameTaken(String username);
}
