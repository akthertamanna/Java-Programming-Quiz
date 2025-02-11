package com.javaprogramming.quiz;

import android.app.Activity;
import android.os.Bundle;
import androidx.core.splashscreen.SplashScreen;
import com.javaprogramming.quiz.activity.HomeActivity;
import com.javaprogramming.quiz.database.QuizDataLoader;
import com.javaprogramming.quiz.database.SaveData;
import com.javaprogramming.quiz.model.Category;
import com.javaprogramming.quiz.model.Quiz;
import com.javaprogramming.quiz.utilities.Helper;
import com.javaprogramming.quiz.viewmodel.CategoryViewModel;
import java.io.Serializable;
import java.util.List;

public class SplashClass extends Activity {

    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // Set the splash screen condition (wait for loading to finish)
        splashScreen.setKeepOnScreenCondition(() -> false);

        // Load categories and save them to the database

        if (SaveData.shouldSaveData(SplashClass.this)) {
            // 🚀 Save data to database
            loadAndSaveCategories();

        } else {
            // ❌ Data is already saved, skip and go to home activity
            Helper.navigateToActivity(SplashClass.this, HomeActivity.class, null, true);
        }

    }

    private void loadAndSaveCategories() {
        // Use QuizDataLoader to load the categories
        QuizDataLoader.loadQuizData(this, new QuizDataLoader.QuizDataCallback() {
            @Override
            public void onCategoriesLoaded(List<Category> categories, List<Quiz> quizzes) {
                // Categories are loaded, pass them to HomeActivity to save in Room
                Bundle data = new Bundle();
                data.putSerializable("categories", (Serializable) categories);
                data.putSerializable("quizzes", (Serializable) quizzes);
                Helper.navigateToActivity(SplashClass.this, HomeActivity.class, data, true);
            }
        });
    }

}
