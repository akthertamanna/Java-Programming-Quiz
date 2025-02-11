package com.javaprogramming.quiz.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.javaprogramming.quiz.R;
import com.javaprogramming.quiz.adapter.SingleQuizHistoryAdapter;
import com.javaprogramming.quiz.databinding.ActivityFullHistoryBinding;
import com.javaprogramming.quiz.model.QuizHistoryWithCategory;
import com.javaprogramming.quiz.model.QuizHistoryWithQuestion;
import com.javaprogramming.quiz.utilities.Helper;
import com.javaprogramming.quiz.viewmodel.QuizHistoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class FullHistoryActivity extends AppCompatActivity {

    private ActivityFullHistoryBinding binding;

    private QuizHistoryViewModel quizHistoryViewModel;
    private SingleQuizHistoryAdapter singleQuizHistoryAdapter;
    private int quizHistoryID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.layoutProgress.overlayViewLayout.setVisibility(View.VISIBLE);

        Bundle data = getIntent().getExtras();

        if (data != null) {

            QuizHistoryWithCategory quizHistoryWithCategory = (QuizHistoryWithCategory) data.getSerializable("quizHistory");

            if (quizHistoryWithCategory != null) {

                quizHistoryID = quizHistoryWithCategory.quizHistory.getQuizHistoryID_QH();
                Log.d("FullHistoryActivity", "Quiz History ID: " + quizHistoryID);

            }

        }

        setupRecyclerView();

        quizHistoryViewModel = new ViewModelProvider(this).get(QuizHistoryViewModel.class);

        quizHistoryViewModel.getQuizHistoryWithQuestions(quizHistoryID).observe(this, this::updateHistoryList);

        backHandler();

    }

    private void updateHistoryList(List<QuizHistoryWithQuestion> quizHistoryWithQuestions) {
        if (quizHistoryWithQuestions != null) {
            singleQuizHistoryAdapter.setSingleQuiz(quizHistoryWithQuestions);
            binding.layoutProgress.overlayViewLayout.setVisibility(View.GONE);
        }else{
            Log.d("FullHistoryActivity", "No data");
        }
    }


    // Setup RecyclerView with GridLayoutManager
    private void setupRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(
                this,
                getResources().getInteger(R.integer.number_of_grid_items_category)
        );
        binding.recyclerSingleQuizHistory.setLayoutManager(manager);
        binding.recyclerSingleQuizHistory.setHasFixedSize(true);

        // Initialize adapter with an empty ArrayList initially
        singleQuizHistoryAdapter = new SingleQuizHistoryAdapter(this, new ArrayList<>());
        binding.recyclerSingleQuizHistory.setAdapter(singleQuizHistoryAdapter);
    }

    private void backHandler(){
        // Register back-press callback
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Helper.navigateToActivity(FullHistoryActivity.this, QuizHistoryActivity.class, null, true);
            }
        };

        // Add the callback to the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }
}