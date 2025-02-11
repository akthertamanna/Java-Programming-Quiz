package com.javaprogramming.quiz.activity;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.javaprogramming.quiz.databinding.ActivityShopBinding;
import com.javaprogramming.quiz.utilities.Helper;
import com.javaprogramming.quiz.viewmodel.UserViewModel;

public class ShopActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityShopBinding binding;
    private UserViewModel userViewModel;
    private int coins = 0;
    private int price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Initialize ViewModels
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observe User data to update UI (e.g., coins)
        userViewModel.getUser().observe(this, user -> {
            if (user != null) {
                coins = user.getCoins_U();
                binding.layoutShopFeatures.txtCoin.setText(String.valueOf(user.getCoins_U()));
                binding.layoutShopFeatures.txtHintAvailable.setText(String.valueOf(user.getHints_U()));
                binding.layoutShopFeatures.txtFiftyFifty.setText(String.valueOf(user.getFiftyFifty_U()));
                binding.layoutShopFeatures.txtTimerFeature.setText(String.valueOf(user.getTimerFeature_U()));
            }
        });


        binding.layerBtnWatchToBuyCoin.setOnClickListener(this);

        binding.layerBtnWatchToGetHint.setOnClickListener(this);
        binding.layerBtnBuyHint.setOnClickListener(this);
        binding.layerBtnBuyHintPackage.setOnClickListener(this);


        binding.layerBtnWatchToGetFiftyFifty.setOnClickListener(this);
        binding.layerBtnBuyFiftyFifty.setOnClickListener(this);
        binding.layerBtnBuyFiftyFiftyPackage.setOnClickListener(this);

        binding.layerBtnWatchToGetTimer.setOnClickListener(this);
        binding.layerBtnBuyTimer.setOnClickListener(this);
        binding.layerBtnBuyTimerPackage.setOnClickListener(this);


        binding.layoutShopFeatures.layoutTextCoin.setVisibility(GONE);
        binding.layoutShopFeatures.layoutTextFiftyFifty.setVisibility(GONE);
        binding.layoutShopFeatures.layoutTextHint.setVisibility(GONE);
        binding.layoutShopFeatures.layoutTextTimer.setVisibility(GONE);


        backHandler();

    }

    @Override
    public void onClick(View v) {
        // Get the coin with video
        if (v == binding.layerBtnWatchToBuyCoin){
            loadAds("coin_video", 10);
        }

        // Get the hint
        if (v == binding.layerBtnWatchToGetHint){
            loadAds("hint_video", 1);
        }

        if (v == binding.layerBtnBuyHint){
            price = 25;
            buyFeatures("hint_coin");
        }

        if (v == binding.layerBtnBuyHintPackage){
            price = 100;
            buyFeatures("hint_coin_package");
        }

        // Get the fifty fifty
        if (v == binding.layerBtnWatchToGetFiftyFifty){
            loadAds("fifty_fifty_video", 1);
        }

        if (v == binding.layerBtnBuyFiftyFifty){
            price = 25;
            buyFeatures("fifty_fifty_coin");
        }

        if (v == binding.layerBtnBuyFiftyFiftyPackage){
            price = 100;
            buyFeatures("fifty_fifty_coin_package");
        }

        // Get the timer
        if (v == binding.layerBtnWatchToGetTimer){
            loadAds("timer_video", 1);
        }

        if (v == binding.layerBtnBuyTimer){
            price = 25;
            buyFeatures("timer_coin");
        }

        if (v == binding.layerBtnBuyTimerPackage){
            price = 100;
            buyFeatures("timer_coin_package");
        }


    }

    private void buyFeatures(String type) {
        if (userViewModel.hasEnoughCoins(price)){

            if (type.equals("hint_coin")){
                userViewModel.updateHints(1, true);
                userViewModel.updateCoins(price, false);
            }
            else if (type.equals("hint_coin_package")){
                userViewModel.updateHints(5, true);
                userViewModel.updateCoins(price, false);

            } else if (type.equals("fifty_fifty_coin")){
                userViewModel.updateFiftyFifty(1, true);
                userViewModel.updateCoins(price, false);

            }
            else if (type.equals("fifty_fifty_coin_package")){
                userViewModel.updateFiftyFifty(5, true);
                userViewModel.updateCoins(price, false);
            }

            else if (type.equals("timer_coin")){
                userViewModel.updateTimerFeature(1, true);
                userViewModel.updateCoins(price, false);
            }

            else if (type.equals("timer_coin_package")){
                userViewModel.updateTimerFeature(5, true);
                userViewModel.updateCoins(price, false);
            }
        }else{
            Helper.showMessageSnackbar(findViewById(android.R.id.content), "Not have an enough coins!");

        }
    }

    private void loadAds(String type, int reward) {
        switch (type) {
            case "coin_video" -> userViewModel.updateCoins(reward, true);
            case "hint_video" -> userViewModel.updateHints(reward, true);
            case "fifty_fifty_video" -> userViewModel.updateFiftyFifty(reward, true);
            case "timer_video" -> userViewModel.updateTimerFeature(reward, true);
        }
    }


    private void backHandler(){
        // Register back-press callback
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Helper.navigateToActivity(ShopActivity.this, HomeActivity.class, null, true);
            }
        };

        // Add the callback to the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }


}