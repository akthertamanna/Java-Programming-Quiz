package com.javaprogramming.quiz.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.javaprogramming.quiz.R;
import com.javaprogramming.quiz.adapter.QuizHistoryAdapter;
import com.javaprogramming.quiz.databinding.ActivityQuizHistoryBinding;
import com.javaprogramming.quiz.model.QuizHistory;
import com.javaprogramming.quiz.model.QuizHistoryWithCategory;
import com.javaprogramming.quiz.utilities.Helper;
import com.javaprogramming.quiz.viewmodel.QuizHistoryViewModel;
import com.javaprogramming.quiz.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class QuizHistoryActivity extends AppCompatActivity {

    private ActivityQuizHistoryBinding binding;
    private QuizHistoryAdapter quizHistoryAdapter;
    private QuizHistoryViewModel quizHistoryViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.layoutProgress.overlayViewLayout.setVisibility(View.VISIBLE);

        // Initialize ViewModels
        quizHistoryViewModel = new ViewModelProvider(this).get(QuizHistoryViewModel.class);

        setupRecyclerView();

        // Pass additional arguments when calling the update method
        quizHistoryViewModel.getAllQuizHistory().observe(this, this::updateCategoryList);

        backHandler();
    }

    private void backHandler(){
        // Register back-press callback
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Helper.navigateToActivity(QuizHistoryActivity.this, HomeActivity.class, null, true);
            }
        };

        // Add the callback to the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    // Update the category list in the adapter
    private void updateCategoryList(List<QuizHistoryWithCategory> quizHistoryList) {
        if (quizHistoryList != null) {
            // Convert List<Category> to ArrayList<Category> explicitly
            quizHistoryAdapter.setCategories(quizHistoryList);
            // Update the total category count and questions
            binding.txtTotalQuizParticipate.setText(String.format("%s %d quiz", getResources().getString(R.string.you_have_participated_in_X_quiz), quizHistoryList.size()));
            binding.layoutProgress.overlayViewLayout.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(
                this,
                getResources().getInteger(R.integer.number_of_grid_items_category)
        );
        binding.recyclerQuizHistory.setLayoutManager(manager);
        binding.recyclerQuizHistory.setHasFixedSize(true);

        // Initialize adapter with an empty ArrayList initially
        quizHistoryAdapter = new QuizHistoryAdapter(this, new ArrayList<>());
        binding.recyclerQuizHistory.setAdapter(quizHistoryAdapter);
    }

}