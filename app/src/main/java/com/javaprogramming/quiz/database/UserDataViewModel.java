package com.javaprogramming.quiz.database;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserDataViewModel extends ViewModel {
    // User coins
    private MutableLiveData<Integer> coins;

    // User hints available
    private MutableLiveData<Integer> hintsAvailable;

    private MutableLiveData<Integer> fiftyFiftyAvailable;
    private MutableLiveData<Integer> timerFeatureAvailable;

    // Coins
    public MutableLiveData<Integer> getCoin() {
        if (coins == null) {
            coins = new MutableLiveData<>();
            coins.setValue(0); // Initialize with 0
        }
        return coins;
    }

    public void addCoins(int coin) {
        getCoin().setValue(getCoin().getValue() + coin);
    }

    public void removeCoins(int coin) {
        int currentCoins = getCoin().getValue();
        getCoin().setValue(Math.max(currentCoins - coin, 0)); // Prevent going below 0
    }

    // Hints
    public MutableLiveData<Integer> getHintsAvailable() {
        if (hintsAvailable == null) {
            hintsAvailable = new MutableLiveData<>();
            hintsAvailable.setValue(5); // Initialize with 0
        }
        return hintsAvailable;
    }

    public void addHints(int hint) {
        getHintsAvailable().setValue(getHintsAvailable().getValue() + hint);
    }

    public void removeHints(int hint) {
        int currentHints = getHintsAvailable().getValue();
        getHintsAvailable().setValue(Math.max(currentHints - hint, 0)); // Prevent going below 0
    }

    // Timer
    public MutableLiveData<Integer> getTimerFeatureAvailable() {
        if (timerFeatureAvailable == null) {
            timerFeatureAvailable = new MutableLiveData<>();
            timerFeatureAvailable.setValue(1); // Initialize with 0
        }
        return timerFeatureAvailable;
    }

    public void addTimer(int timer) {
        getTimerFeatureAvailable().setValue(getTimerFeatureAvailable().getValue() + timer);
    }

    public void removeTimer(int timer) {
        int currentTimer = getTimerFeatureAvailable().getValue();
        getTimerFeatureAvailable().setValue(Math.max(currentTimer - timer, 0)); // Prevent going below 0
    }

    // Fifty-fifty
    public MutableLiveData<Integer> getFiftyFiftyAvailable() {
        if (fiftyFiftyAvailable == null) {
            fiftyFiftyAvailable = new MutableLiveData<>();
            fiftyFiftyAvailable.setValue(5); // Initialize with 0
        }
        return fiftyFiftyAvailable;
    }

    public void addFiftyFifty(int fiftyFifty) {
        getFiftyFiftyAvailable().setValue(getFiftyFiftyAvailable().getValue() + fiftyFifty);
    }

    public void removeFiftyFifty(int fiftyFifty) {
        int currentFiftyFifty = getFiftyFiftyAvailable().getValue();
        getFiftyFiftyAvailable().setValue(Math.max(currentFiftyFifty - fiftyFifty, 0)); // Prevent going below 0
    }
}
