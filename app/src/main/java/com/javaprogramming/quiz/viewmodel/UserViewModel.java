package com.javaprogramming.quiz.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.javaprogramming.quiz.model.User;
import com.javaprogramming.quiz.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private LiveData<User> user;

    public UserViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application);
        user = userRepository.getUser();  // Get LiveData to observe
    }

    // Get LiveData for observing in the UI
    public LiveData<User> getUser() {
        return user;
    }

    // Update user data in Room through the repository
    public void updateUser(User user) {
        userRepository.updateUser(user);  // Update data in Room
    }

    // Update specific fields (coins, hints, etc.)

    // Update coins safely
    public void updateCoins(int amount, boolean isAdd) {
        User currentUser = user.getValue();
        if (currentUser != null) {

            Log.d("Coins", "Current coins: " + currentUser.getCoins_U() + ", Amount: " + amount + ", IsAdd: " + isAdd);
            int updatedCoins = isAdd ? currentUser.getCoins_U() + amount : Math.max(0, currentUser.getCoins_U() - amount);
            userRepository.updateCoins(updatedCoins);
        }
        else{
            Log.d("Coins", "User is null");
        }
    }


    // Method to check if the user has enough coins to buy something
    public boolean hasEnoughCoins(int itemCost) {
        User currentUser = user.getValue();
        if (currentUser != null) {
            return currentUser.getCoins_U() >= itemCost;
        }
        return false;
    }

    // Update hints safely
    public void updateHints(int amount, boolean isAdd) {
        User currentUser = user.getValue();
        if (currentUser != null) {
            int updatedHints = isAdd ? currentUser.getHints_U() + amount : Math.max(0, currentUser.getHints_U() - amount);
            userRepository.updateHints(updatedHints);
        }
    }

    // Update timerFeature safely
    public void updateTimerFeature(int amount, boolean isAdd) {
        User currentUser = user.getValue();
        if (currentUser != null) {
            int updatedTimerFeature = isAdd ? currentUser.getTimerFeature_U() + amount : Math.max(0, currentUser.getTimerFeature_U() - amount);
            userRepository.updateTimerFeature(updatedTimerFeature);
        }
    }

    // Update fiftyFifty safely
    public void updateFiftyFifty(int amount, boolean isAdd) {
        User currentUser = user.getValue();
        if (currentUser != null) {
            int updatedFiftyFifty = isAdd ? currentUser.getFiftyFifty_U() + amount : Math.max(0, currentUser.getFiftyFifty_U() - amount);
            userRepository.updateFiftyFifty(updatedFiftyFifty);
        }
    }
}
