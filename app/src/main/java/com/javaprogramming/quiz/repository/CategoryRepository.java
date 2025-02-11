package com.javaprogramming.quiz.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Query;

import com.javaprogramming.quiz.database.AppDatabase;
import com.javaprogramming.quiz.interfaces.OnInsertCompleteListener;
import com.javaprogramming.quiz.interfaces.dao.CategoryDao;
import com.javaprogramming.quiz.model.Category;

import java.util.List;

public class CategoryRepository {

    private CategoryDao categoryDao;

    public CategoryRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        categoryDao = db.categoryDao();
    }

    public void insertCategory(Category category) {
        // Perform the insert operation on a background thread (using Executor or background thread)
        new Thread(() -> categoryDao.insert(category)).start();
    }

    public void insertCategories(List<Category> categories, OnInsertCompleteListener listener) {
        new Thread(() -> {
            try {
                long[] insertedIds = categoryDao.insertCategories(categories);  // Insert categories
                // If insertion is successful, we notify the listener
                // Notify failure
                listener.onInsertComplete(insertedIds.length > 0);  // Notify success
            } catch (Exception ignored) {
                listener.onInsertComplete(false);  // Notify failure in case of an error
            }
        }).start();
    }

    // Update levelCompleted field for a category
    public void incrementLevelCompleted(int categoryId) {
        new Thread(() -> categoryDao.updateLevelCompleted(categoryId)).start();
    }

    // Get all categories as LiveData
    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

}
