package com.javaprogramming.quiz.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.javaprogramming.quiz.interfaces.OnInsertCompleteListener;
import com.javaprogramming.quiz.model.Category;
import com.javaprogramming.quiz.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {

    private CategoryRepository categoryRepository;
    private LiveData<List<Category>> allCategories;

    public CategoryViewModel(Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
        allCategories = categoryRepository.getAllCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insertCategory(Category category) {
        categoryRepository.insertCategory(category);
    }

//    public void insertCategories(List<Category> categories) {
//        categoryRepository.insertCategories(categories);
//    }


    public void insertCategories(List<Category> categories, OnInsertCompleteListener listener) {
        categoryRepository.insertCategories(categories, listener);
    }

    public void incrementLevelCompleted(int categoryId) {
        categoryRepository.incrementLevelCompleted(categoryId);
    }
}
