package com.javaprogramming.quiz.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.javaprogramming.quiz.database.AppDatabase;
import com.javaprogramming.quiz.interfaces.dao.UserDao;
import com.javaprogramming.quiz.model.User;

public class UserRepository {
    private UserDao userDao;
    private LiveData<User> userLiveData;

    public UserRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        userDao = database.userDao();
        userLiveData = userDao.getUser();

        // Check if a user exists, if not, insert a default user
        new Thread(() -> {
            User existingUser = userDao.getUserNonLiveData();  // Custom method to get non-LiveData user
            if (existingUser == null) {
                // No user exists, insert the default user
                User newUser = new User();
                userDao.insert(newUser);
            }
        }).start();
    }

    // Get LiveData of the user
    public LiveData<User> getUser() {
        return userLiveData;
    }

    // Update entire user data
    public void updateUser(User user) {
        new Thread(() -> {
            userDao.updateUser(user);  // Insert or update the user in the database
        }).start();
    }

    // Update specific fields (coins, hints, etc.)

    public void updateCoins(int newCoins) {
        new Thread(() -> userDao.updateCoins(newCoins)).start();
    }

    public void updateHints(int newHints) {
        new Thread(() -> userDao.updateHints(newHints)).start();
    }

    public void updateTimerFeature(int newTimerFeature) {
        new Thread(() -> userDao.updateTimerFeature(newTimerFeature)).start();
    }

    public void updateFiftyFifty(int newFiftyFifty) {
        new Thread(() -> userDao.updateFiftyFifty(newFiftyFifty)).start();
    }
}
