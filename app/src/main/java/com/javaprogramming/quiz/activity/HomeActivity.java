package com.javaprogramming.quiz.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.javaprogramming.quiz.database.SaveData;
import com.javaprogramming.quiz.databinding.ActivityHomeBinding;
import com.javaprogramming.quiz.interfaces.OnInsertCompleteListener;
import com.javaprogramming.quiz.model.Category;
import com.javaprogramming.quiz.model.Quiz;
import com.javaprogramming.quiz.utilities.Helper;
import com.javaprogramming.quiz.viewmodel.CategoryViewModel;
import com.javaprogramming.quiz.viewmodel.QuizViewModel;
import com.javaprogramming.quiz.viewmodel.UserViewModel;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityHomeBinding binding;
    private CategoryViewModel categoryViewModel;
    private QuizViewModel quizViewModel;
    private UserViewModel userViewModel;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = HomeActivity.this;

        // Initialize the ViewModel
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getUser().observe(this, user -> {
            if (user != null) {
                binding.layoutShopFeatures.txtCoin.setText(String.valueOf(user.getCoins_U()));
                binding.layoutShopFeatures.txtHintAvailable.setText(String.valueOf(user.getHints_U()));
                binding.layoutShopFeatures.txtFiftyFifty.setText(String.valueOf(user.getFiftyFifty_U()));
                binding.layoutShopFeatures.txtTimerFeature.setText(String.valueOf(user.getTimerFeature_U()));
            }
        });


        // Retrieve the Bundle passed from SplashClass
        Bundle data = getIntent().getExtras();

        if (data != null) {
            // Extract the categories and quizzes from the Bundle
            List<Category> categories = (List<Category>) data.getSerializable("categories");
            List<Quiz> quizzes = (List<Quiz>) data.getSerializable("quizzes");

            if (categories != null) {
                // Save the categories to Room
                categoryViewModel.insertCategories(categories, new OnInsertCompleteListener() {
                    @Override
                    public void onInsertComplete(boolean isSuccess) {
                        if (quizzes != null) {
                            // Save the quizzes to Room
                            quizViewModel.saveQuizzes(quizzes);
                        }

                        // ✅ Mark data as saved
                        SaveData.markDataAsSaved(HomeActivity.this);
                    }
                });

                Toast.makeText(this, "Total categories: "+ categories.size(), Toast.LENGTH_SHORT).show();
            }
        }



        binding.btnHomePlay.setOnClickListener(this);

        binding.btnShop.setOnClickListener(this);

        binding.btnHistory.setOnClickListener(this);
        binding.btnSettingsPlay.setOnClickListener(this);

        binding.btnQuit.setOnClickListener(this);

        binding.layoutShopFeatures.imgCoinVideo.setOnClickListener(this);
        binding.layoutShopFeatures.imgFiftyFiftyVideo.setOnClickListener(this);
        binding.layoutShopFeatures.imgTimerFeatureVideo.setOnClickListener(this);
        binding.layoutShopFeatures.imgHintVideo.setOnClickListener(this);

        backHandler();

    }

    public void displayExitAlert() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText("Are you sure you want to exit?").setContentText("You can come back anytime to play quiz!");
        sweetAlertDialog.setConfirmText("Exit").setCancelText("No")
                .setCancelClickListener(Dialog::dismiss);
        sweetAlertDialog.setConfirmClickListener(sweetAlertDialog1 -> finishAffinity());
        sweetAlertDialog.setConfirmButtonBackgroundColor(Color.RED);
        sweetAlertDialog.show();
    }

    private void backHandler(){
        // Register back-press callback
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                displayExitAlert();
            }
        };

        // Add the callback to the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnShop
                || v == binding.layoutShopFeatures.imgCoinVideo
                || v == binding.layoutShopFeatures.imgFiftyFiftyVideo
                || v == binding.layoutShopFeatures.imgTimerFeatureVideo
                || v == binding.layoutShopFeatures.imgHintVideo) {
            Helper.navigateToActivity(context, ShopActivity.class, null , true);
        }

        if (v == binding.btnHomePlay) {
            Helper.navigateToActivity(context, CategoryActivity.class, null , false);
        }

        if (v == binding.btnHistory) {
            Helper.navigateToActivity(context, QuizHistoryActivity.class, null , false);
        }

        if (v == binding.btnQuit) {
//            displayExitAlert();
            Helper.navigateToActivity(context, TestActivity.class, null , false);
        }

        if (v == binding.btnSettingsPlay) {
            Helper.navigateToActivity(context, SettingActivity.class, null , false);
        }
    }
}