package com.javaprogramming.quiz.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.javaprogramming.quiz.R;
import com.javaprogramming.quiz.viewmodel.UserViewModel;
import com.javaprogramming.quiz.databinding.ActivityQuizPreStartBinding;
import com.javaprogramming.quiz.model.Category;
import com.javaprogramming.quiz.model.User;
import com.javaprogramming.quiz.utilities.Helper;

public class QuizPreStartActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityQuizPreStartBinding binding;
    private int totalQuestionShouldDisplay = 10;
    private int rewardPerQuestion = 10;
    private UserViewModel userViewModel;
    private int coin = 0;
    private int hintAvailable = 0;
    private int fiftyFiftyAvailable = 0;
    private int timerFeatureAvailable = 0;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizPreStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the context variable
        context = QuizPreStartActivity.this;

        //The key argument here must match that used in the other activity
        Bundle data = getIntent().getExtras();
        if (data != null) {

            // Cast the result of getSerializable to the category class
            Category category = (Category) data.getSerializable("category");

            if (category != null) {
                binding.txtLevelTitle.setText(category.getCategoryTitle_C());
                binding.txtReward.setText(String.valueOf(rewardPerQuestion * totalQuestionShouldDisplay));
            }

        }


        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observe the LiveData to get updates whenever the user data changes
        userViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                // Update UI with user data (e.g., display user coins, hints)
                if (user != null) {

                    coin = user.getCoins_U();
                    hintAvailable = user.getHints_U();
                    fiftyFiftyAvailable = user.getFiftyFifty_U();
                    timerFeatureAvailable = user.getTimerFeature_U();

                    binding.txtTimerFeature.setText(String.valueOf(user.getTimerFeature_U()));
                    binding.txtFiftyFifty.setText(String.valueOf(user.getFiftyFifty_U()));
                    binding.txtHint.setText(String.valueOf(user.getHints_U()));

                    setQuizFeatures();

                }
            }
        });

        initializeClickableUI();

        backHandler();


    }

    private void backHandler(){
        // Register back-press callback
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Helper.navigateToActivity(QuizPreStartActivity.this, CategoryActivity.class, null, true);
            }
        };

        // Add the callback to the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    private void initializeClickableUI() {
        binding.layerBtnHint.setOnClickListener(this);
        binding.layerBtnFiftyFifty.setOnClickListener(this);
        binding.layerBtnTimerFeature.setOnClickListener(this);

        binding.btnClose.setOnClickListener(this);
        binding.btnPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == binding.btnClose){
            finish();
        }

        if (v == binding.btnPlay){
            Helper.navigateToActivity(this, QuizPlayActivity.class, null, true);
        }

        if (v == binding.layerBtnHint){
            Helper.displayBuyTimer(context,"Hint", userViewModel.getUser().getValue(), userViewModel);
        }

        if (v == binding.layerBtnFiftyFifty){
            Helper.displayBuyTimer(context,"Fifty", userViewModel.getUser().getValue(), userViewModel);
        }

        if (v == binding.layerBtnTimerFeature){
            Helper.displayBuyTimer(context,"Timer", userViewModel.getUser().getValue(), userViewModel);
        }

    }


    private void setQuizFeatures() {
        setupHintUI();
        setupTimerFeatureUI();
        setupFiftyFiftyLifeLineUI();
    }

    private void setupTimerFeatureUI() {
        if(timerFeatureAvailable <= 0){
            binding.txtTimerFeature.setVisibility(GONE);
            binding.imgTimerFeatureVideo.setVisibility(VISIBLE);
            binding.layoutTextTimer.setBackgroundResource(R.drawable.bg_round_with_border_white_1px);
        }else{
            binding.txtTimerFeature.setVisibility(VISIBLE);
            binding.imgTimerFeatureVideo.setVisibility(GONE);
            binding.layoutTextTimer.setBackgroundResource(R.drawable.bg_circle_white);
        }
    }

    private void setupFiftyFiftyLifeLineUI() {
        if(fiftyFiftyAvailable <= 0){
            binding.txtFiftyFifty.setVisibility(GONE);
            binding.imgFiftyFiftyVideo.setVisibility(VISIBLE);
            binding.layoutTextFiftyFifty.setBackgroundResource(R.drawable.bg_round_with_border_white_1px);
        }else{
            binding.txtFiftyFifty.setVisibility(VISIBLE);
            binding.imgFiftyFiftyVideo.setVisibility(GONE);
            binding.layoutTextFiftyFifty.setBackgroundResource(R.drawable.bg_circle_white);
        }
    }

    private void setupHintUI(){

        binding.txtHint.setVisibility(View.GONE);

        if (hintAvailable > 0){
            binding.txtHint.setVisibility(VISIBLE);
            binding.imgHintVideo.setVisibility(GONE);
            binding.layoutTextHint.setBackgroundResource(R.drawable.bg_circle_white);
        }else{
            binding.txtHint.setVisibility(GONE);
            binding.imgHintVideo.setVisibility(VISIBLE);
            binding.layoutTextHint.setBackgroundResource(R.drawable.bg_round_with_border_white_1px);
        }
    }



}