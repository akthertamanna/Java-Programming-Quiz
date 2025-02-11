package com.javaprogramming.quiz.interfaces.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.javaprogramming.quiz.model.User;
import com.javaprogramming.quiz.utilities.Helper;

@Dao
public interface UserDao {
    // Insert a user (only needed the first time, assuming only one user)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    // Get the single user (returns a LiveData<User>)
    @Query("SELECT * FROM " + Helper.USER_TABLE_NAME + " LIMIT 1")
    LiveData<User> getUser();

    // Update the user data (coins, hints, etc.)
    @Update
    void updateUser(User user);

    // Get a single user (non-LiveData)
    @Query("SELECT * FROM " + Helper.USER_TABLE_NAME + " LIMIT 1")
    User getUserNonLiveData();  // For checking if user exists

    // Update the coins field
    @Query("UPDATE " + Helper.USER_TABLE_NAME + " SET coins_U = :newCoins WHERE userID_U = 1")
    void updateCoins(int newCoins);

    // Update the hints field
    @Query("UPDATE " + Helper.USER_TABLE_NAME + " SET hints_U = :newHints WHERE userID_U = 1")
    void updateHints(int newHints);

    // Update the timerFeature field
    @Query("UPDATE "+ Helper.USER_TABLE_NAME + " SET timerFeature_U = :newTimerFeature WHERE userID_U = 1")
    void updateTimerFeature(int newTimerFeature);

    // Update the fiftyFifty field
    @Query("UPDATE "+ Helper.USER_TABLE_NAME +" SET fiftyFifty_U = :newFiftyFifty WHERE userID_U = 1")
    void updateFiftyFifty(int newFiftyFifty);

}
