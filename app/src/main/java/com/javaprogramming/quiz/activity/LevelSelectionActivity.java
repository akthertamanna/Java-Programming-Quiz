package com.javaprogramming.quiz.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.javaprogramming.quiz.databinding.ActivityLevelSelectionBinding;
import com.javaprogramming.quiz.model.Category;

public class LevelSelectionActivity extends AppCompatActivity {
    private ActivityLevelSelectionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLevelSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //The key argument here must match that used in the other activity
        Bundle data = getIntent().getExtras();
        if (data != null) {

            // Cast the result of getSerializable to the category class
            Category category = (Category) data.getSerializable("category");

            if (category != null) {
               binding.txtLevelTitle.setText(category.getCategoryTitle_C());
            }

        }

        binding.layerCloseBtn.setOnClickListener(v -> finish());

    }
}