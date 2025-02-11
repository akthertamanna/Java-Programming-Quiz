package com.javaprogramming.quiz.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.javaprogramming.quiz.R;
import com.javaprogramming.quiz.adapter.CategoryAdapter;
import com.javaprogramming.quiz.databinding.ActivityCategoryBinding;
import com.javaprogramming.quiz.model.Category;
import com.javaprogramming.quiz.model.CategoryQuestionCount;
import com.javaprogramming.quiz.utilities.Helper;
import com.javaprogramming.quiz.viewmodel.CategoryViewModel;
import com.javaprogramming.quiz.viewmodel.QuizViewModel;
import com.javaprogramming.quiz.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private ActivityCategoryBinding binding;
    private CategoryAdapter categoryAdapter;
    private UserViewModel userViewModel;
    private CategoryViewModel categoryViewModel;
    private QuizViewModel quizViewModel;
    private List<CategoryQuestionCount> categoryCounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.layoutProgress.overlayViewLayout.setVisibility(View.VISIBLE);

        // Initialize ViewModels
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        // Observe User data to update UI (e.g., coins)
        userViewModel.getUser().observe(this, user -> {
            if (user != null) {
                binding.layoutShopFeatures.txtCoin.setText(String.valueOf(user.getCoins_U()));
            }
        });

        // Fetch category question counts
        categoryCounts = quizViewModel.getCategoryQuestionCounts();

        // Observe Category data and update the adapter
        categoryViewModel.getAllCategories().observe(this, categories -> {
            // Pass additional arguments when calling the update method
            updateCategoryList(categories, categoryCounts);
        });

        // Set up the RecyclerView
        setupRecyclerView();

        // Close button click listener
//        binding.layerCloseBtn.setOnClickListener(v -> userViewModel.updateCoins(20, true));
        binding.layerBackBtn.setOnClickListener(v -> Helper.navigateToActivity(CategoryActivity.this, HomeActivity.class, null, true));


        backHandler();

    }

    // Update the category list in the adapter
    private void updateCategoryList(List<Category> categories, List<CategoryQuestionCount> categoryCounts) {
        if (categories != null) {
            // Convert List<Category> to ArrayList<Category> explicitly
            categoryAdapter.setCategories(categories, categoryCounts);
            binding.layoutProgress.overlayViewLayout.setVisibility(View.GONE);
        }
    }

    // Setup RecyclerView with GridLayoutManager
    private void setupRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(
                this,
                getResources().getInteger(R.integer.number_of_grid_items_category)
        );
        binding.recyclerCategory.setLayoutManager(manager);
        binding.recyclerCategory.setHasFixedSize(true);

        // Initialize adapter with an empty ArrayList initially
        categoryAdapter = new CategoryAdapter(this, new ArrayList<>(), categoryCounts);
        binding.recyclerCategory.setAdapter(categoryAdapter);
    }

    private void backHandler(){
        // Register back-press callback
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Helper.navigateToActivity(CategoryActivity.this, HomeActivity.class, null, true);
            }
        };

        // Add the callback to the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

}
