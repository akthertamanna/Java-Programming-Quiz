package com.javaprogramming.quiz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.javaprogramming.quiz.utilities.Helper;

@Entity(tableName = Helper.USER_TABLE_NAME)
public class User {

    @PrimaryKey(autoGenerate = true)
    private int userID_U;

    // User's coin balance
    private int coins_U;

    // Count of available hints
    private int hints_U;

    // Timer feature usage
    private int timerFeature_U;

    // Count of available fifty-fifty usages
    private int fiftyFifty_U;

    // No-argument constructor required by Room
    public User() {
        // Default values can be assigned here
        this.coins_U = 10;
        this.hints_U = 3;
        this.timerFeature_U = 3;
        this.fiftyFifty_U = 3;
    }

    // Constructor to initialize all fields
    @Ignore
    public User(int coins_U, int hints_U, int timerFeature_U, int fiftyFifty_U) {
        this.coins_U = coins_U;
        this.hints_U = hints_U;
        this.timerFeature_U = timerFeature_U;
        this.fiftyFifty_U = fiftyFifty_U;
    }

    public int getUserID_U() {
        return userID_U;
    }

    public void setUserID_U(int userID_U) {
        this.userID_U = userID_U;
    }

    public int getCoins_U() {
        return coins_U;
    }

    public void setCoins_U(int coins_U) {
        this.coins_U = coins_U;
    }

    public int getHints_U() {
        return hints_U;
    }

    public void setHints_U(int hints_U) {
        this.hints_U = hints_U;
    }

    public int getTimerFeature_U() {
        return timerFeature_U;
    }

    public void setTimerFeature_U(int timerFeature_U) {
        this.timerFeature_U = timerFeature_U;
    }

    public int getFiftyFifty_U() {
        return fiftyFifty_U;
    }

    public void setFiftyFifty_U(int fiftyFifty_U) {
        this.fiftyFifty_U = fiftyFifty_U;
    }
}
